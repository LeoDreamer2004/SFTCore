package org.leodreamer.sftcore.common.machine.multiblock.part;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.api.gui.GasTankWidget;
import org.leodreamer.sftcore.common.machine.trait.NotifiableGasTank;
import org.leodreamer.sftcore.integration.mek.SFTMekanismCapabilities;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;
import com.gregtechceu.gtceu.api.sync_system.annotations.SaveField;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.gregtechceu.gtceu.utils.ISubscription;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import com.lowdragmc.lowdraglib.gui.widget.ImageWidget;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@DataGenScanned
public class GasHatchPartMachine extends TieredIOPartMachine {

    public static final long INITIAL_TANK_CAPACITY_1X = 8_000;
    public static final long INITIAL_TANK_CAPACITY_4X = 2_000;
    public static final long INITIAL_TANK_CAPACITY_9X = 1_000;

    private final int slots;

    @SaveField
    public final NotifiableGasTank tank;

    @Nullable
    protected TickableSubscription autoIOSubs;

    @Nullable
    protected ISubscription tankSubs;

    private final LazyOptional<IGasHandler> gasHandlerCap;

    public GasHatchPartMachine(BlockEntityCreationInfo info, int tier, IO io, long initialCapacity, int slots) {
        super(info, tier, io);
        this.tank = attachTrait(createTank(initialCapacity, slots));
        this.slots = slots;
        this.gasHandlerCap = LazyOptional.of(() -> tank);
    }

    protected NotifiableGasTank createTank(long initialCapacity, int slots) {
        return new NotifiableGasTank(slots, getTankCapacity(initialCapacity, getTier()), io);
    }

    public static long getTankCapacity(long initialCapacity, int tier) {
        return initialCapacity * (1L << Math.min(9, tier));
    }

    @Override
    public void onLoad() {
        super.onLoad();
        scheduleForNextServerTick(this::updateTankSubscription);
        tankSubs = tank.addChangedListener(this::updateTankSubscription);
    }

    @Override
    public void onUnload() {
        super.onUnload();

        if (tankSubs != null) {
            tankSubs.unsubscribe();
            tankSubs = null;
        }

        if (autoIOSubs != null) {
            autoIOSubs.unsubscribe();
            autoIOSubs = null;
        }
    }

    @Override
    public void onNeighborChanged(Block block, BlockPos fromPos, boolean isMoving) {
        super.onNeighborChanged(block, fromPos, isMoving);
        updateTankSubscription();
    }

    @Override
    public void onRotated(Direction oldFacing, Direction newFacing) {
        super.onRotated(oldFacing, newFacing);
        updateTankSubscription(newFacing);
    }

    protected void updateTankSubscription() {
        updateTankSubscription(getFrontFacing());
    }

    protected void updateTankSubscription(Direction newFacing) {
        if (
            isWorkingEnabled() && (io.support(IO.IN) || (io.support(IO.OUT) && !tank.isEmpty())) &&
                NotifiableGasTank.hasAdjacentGasHandler(getLevel(), getBlockPos(), newFacing)
        ) {
            autoIOSubs = subscribeServerTick(autoIOSubs, this::autoIO);
        } else if (autoIOSubs != null) {
            autoIOSubs.unsubscribe();
            autoIOSubs = null;
        }
    }

    protected void autoIO() {
        if (getOffsetTimer() % 5 != 0) {
            return;
        }

        if (!isWorkingEnabled()) {
            updateTankSubscription();
            return;
        }

        if (io == IO.OUT) {
            tank.exportToNearby(getFrontFacing());
        } else if (io == IO.IN) {
            tank.importFromNearby(getFrontFacing());
        } else if (io == IO.BOTH) {
            tank.importFromNearby(getFrontFacing());
            tank.exportToNearby(getFrontFacing().getOpposite());
        }

        updateTankSubscription();
    }

    @Override
    public void setWorkingEnabled(boolean workingEnabled) {
        super.setWorkingEnabled(workingEnabled);
        updateTankSubscription();
    }

    @Override
    public Widget createUIWidget() {
        if (slots == 1) {
            return createSingleSlotGUI();
        }
        return createMultiSlotGUI();
    }

    protected Widget createSingleSlotGUI() {
        var group = new WidgetGroup(0, 0, 89, 63);

        group.addWidget(new ImageWidget(4, 4, 81, 55, GuiTextures.DISPLAY));

        var tankWidget = new GasTankWidget(
            this.tank,
            0,
            67,
            22,
            18,
            18
        )
            .setAllowClickDrained(io.support(IO.IN))
            .setBackground(GuiTextures.FLUID_SLOT);

        group.addWidget(tankWidget);

        group.addWidget(new LabelWidget(8, 8, GasTankWidget.GAS_AMOUNT))
            .addWidget(new LabelWidget(8, 18, () -> getGasAmountText(tankWidget)))
            .addWidget(new LabelWidget(8, 28, () -> getGasNameText(tankWidget).getString()));

        group.setBackground(GuiTextures.BACKGROUND_INVERSE);

        return group;
    }

    protected Widget createMultiSlotGUI() {
        int rowSize = (int) Math.sqrt(slots);
        int colSize = rowSize;

        if (slots == 8) {
            rowSize = 4;
            colSize = 2;
        }

        var group = new WidgetGroup(0, 0, 18 * rowSize + 16, 18 * colSize + 16);
        var container = new WidgetGroup(4, 4, 18 * rowSize + 8, 18 * colSize + 8);

        int index = 0;

        for (int y = 0; y < colSize; y++) {
            for (int x = 0; x < rowSize; x++) {
                container.addWidget(
                    new GasTankWidget(
                        this.tank,
                        index++,
                        4 + x * 18,
                        4 + y * 18,
                        18,
                        18
                    )
                        .setAllowClickDrained(io.support(IO.IN))
                        .setBackground(GuiTextures.FLUID_SLOT)
                );
            }
        }

        container.setBackground(GuiTextures.BACKGROUND_INVERSE);
        group.addWidget(container);

        return group;
    }

    @Override
    public <T> LazyOptional<T> getCapability(
        Capability<T> cap,
        @Nullable Direction side
    ) {
        if (cap == SFTMekanismCapabilities.GAS_HANDLER && (side == null || side == getFrontFacing())) {
            return gasHandlerCap.cast();
        }
        return super.getCapability(cap, side);
    }

    @RegisterLanguage("Gas Input for Multiblocks")
    private static final String IMPORT_TOOLTIP = "sftcore.machine.gas_hatch.import.tooltip";

    @RegisterLanguage("Gas Output for Multiblocks")
    private static final String EXPORT_TOOLTIP = "sftcore.machine.gas_hatch.export.tooltip";

    @RegisterLanguage("§9Gas Capacity: §f%d mB")
    private static final String GAS_CAPACITY = "sftcore.tooltip.fluid_storage_capacity";

    @RegisterLanguage("§9Gas Capacity: §f%d §7Tanks, §f%d mB §7each")
    private static final String GAS_CAPACITY_MULTI = "sftcore.tooltip.fluid_storage_capacity_multi";

    @Override
    public List<Component> getTabTooltips() {
        var tooltips = super.getTabTooltips();
        tooltips.add(Component.translatable(io == IO.IN ? IMPORT_TOOLTIP : EXPORT_TOOLTIP));
        long capacity = tank.getStorages()[0].getCapacity();
        if (slots == 1) {
            tooltips.add(
                Component.translatable(
                    GAS_CAPACITY, FormattingUtil
                        .formatNumbers(capacity)
                )
            );
        } else {
            tooltips.add(
                Component.translatable(
                    GAS_CAPACITY_MULTI, slots, FormattingUtil
                        .formatNumbers(capacity)
                )
            );
        }
        return tooltips;
    }

    private Component getGasNameText(GasTankWidget tankWidget) {
        var gas = tank.getChemicalInTank(tankWidget.getTank());

        if (!gas.isEmpty()) {
            return gas.getTextComponent();
        }

        return Component.translatable(GasTankWidget.GAS_EMPTY);
    }

    private String getGasAmountText(GasTankWidget tankWidget) {
        var gas = tank.getChemicalInTank(tankWidget.getTank());

        if (!gas.isEmpty()) {
            return getFormattedGasAmount(gas);
        }

        return "";
    }

    public String getFormattedGasAmount(GasStack gasStack) {
        return String.format("%,d", gasStack.isEmpty() ? 0 : gasStack.getAmount());
    }
}
