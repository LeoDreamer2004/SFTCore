package org.leodreamer.sftcore.common.machine.multiblock.part;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.api.gui.GasTankWidget;
import org.leodreamer.sftcore.common.machine.trait.gas.NotifiableGasTank;
import org.leodreamer.sftcore.integration.mek.SFTMekanismCapabilities;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.IHasCircuitSlot;
import com.gregtechceu.gtceu.api.machine.feature.IMuiMachine;
import com.gregtechceu.gtceu.api.machine.mui.MachineUIPanel;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.sync_system.annotations.SaveField;
import com.gregtechceu.gtceu.api.sync_system.annotations.SyncToClient;
import com.gregtechceu.gtceu.common.item.behavior.IntCircuitBehaviour;
import com.gregtechceu.gtceu.common.mui.GTGuiTextures;
import com.gregtechceu.gtceu.common.mui.GTMuiMachineUtil;
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.gregtechceu.gtceu.utils.ISubscription;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import brachy.modularui.api.drawable.Text;
import brachy.modularui.factory.PosGuiData;
import brachy.modularui.screen.UISettings;
import brachy.modularui.utils.Alignment;
import brachy.modularui.value.sync.PanelSyncManager;
import brachy.modularui.widget.ParentWidget;
import brachy.modularui.widgets.SlotGroupWidget;
import brachy.modularui.widgets.TextWidget;
import brachy.modularui.widgets.layout.Flow;
import lombok.Getter;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@DataGenScanned
public class GasHatchPartMachine extends TieredIOPartMachine implements IMuiMachine, IHasCircuitSlot {

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

    @Getter
    @SaveField
    @SyncToClient
    protected boolean circuitSlotEnabled;

    @Getter
    @SaveField
    protected final NotifiableItemStackHandler circuitInventory;

    private final LazyOptional<IGasHandler> gasHandlerCap;

    @RegisterLanguage("Gas in Mekanism Input for Multiblocks")
    public static final String IMPORT_TOOLTIP = "sftcore.machine.gas_hatch.import.tooltip";

    @RegisterLanguage("Gas in Mekanism Output for Multiblocks")
    public static final String EXPORT_TOOLTIP = "sftcore.machine.gas_hatch.export.tooltip";

    @RegisterLanguage("§9Gas Capacity: §f%d mB")
    public static final String GAS_CAPACITY = "sftcore.tooltip.gas_storage_capacity";

    @RegisterLanguage("§9Gas Capacity: §f%d §7Tanks, §f%d mB §7each")
    public static final String GAS_CAPACITY_MULTI = "sftcore.tooltip.gas_storage_capacity_multi";

    public GasHatchPartMachine(BlockEntityCreationInfo info, int tier, IO io, long initialCapacity, int slots) {
        super(info, tier, io);
        this.tank = attachTrait(createTank(initialCapacity, slots));
        this.slots = slots;
        this.gasHandlerCap = LazyOptional.of(() -> tank);

        if (io == IO.IN) {
            this.circuitSlotEnabled = true;
            this.circuitInventory = attachTrait(new NotifiableItemStackHandler(1, IO.IN, IO.NONE))
                .setFilter(IntCircuitBehaviour::isIntegratedCircuit)
                .shouldSearchContent(false)
                .shouldDropInventoryInWorld(!ConfigHolder.INSTANCE.machines.ghostCircuit);
        } else {
            this.circuitSlotEnabled = false;
            this.circuitInventory = attachTrait(new NotifiableItemStackHandler(0, IO.NONE))
                .shouldSearchContent(false);
        }
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
    public void addedToController(MultiblockControllerMachine controller, String substructureName) {
        if (!controller.allowCircuitSlots()) {
            if (!ConfigHolder.INSTANCE.machines.ghostCircuit) {
                circuitInventory.dropInventoryInWorld();
            } else {
                circuitInventory.setStackInSlot(0, ItemStack.EMPTY);
            }
            setCircuitSlotEnabled(false);
        }

        super.addedToController(controller, substructureName);
    }

    @Override
    public void removedFromController(MultiblockControllerMachine controller) {
        super.removedFromController(controller);

        for (var c : controllers) {
            if (!c.allowCircuitSlots()) {
                return;
            }
        }

        setCircuitSlotEnabled(true);
    }

    public void setCircuitSlotEnabled(boolean enabled) {
        circuitSlotEnabled = enabled;
        syncDataHolder.markClientSyncFieldDirty("circuitSlotEnabled");
    }

    @Override
    public void setWorkingEnabled(boolean workingEnabled) {
        super.setWorkingEnabled(workingEnabled);
        updateTankSubscription();
    }

    @Override
    public void buildMainUI(
        ParentWidget<?> mainWidget, PosGuiData guiData, PanelSyncManager syncManager,
        UISettings settings
    ) {
        mainWidget.child(slots == 1 ? createSingleSlotUI() : createMultiSlotUI());
    }

    protected Flow createSingleSlotUI() {
        return Flow.col()
            .width(MachineUIPanel.DEFAULT_CONTENT_WIDTH)
            .height(60)
            .mainAxisAlignment(Alignment.MainAxis.CENTER)
            .childPadding(4)
            .child(new TextWidget<>(Text.dynamic(this::getGasNameText)).horizontalCenter())
            .child(new TextWidget<>(Text.dynamic(this::getGasAmountText)).horizontalCenter())
            .child(
                new GasTankWidget(this.tank, 0)
                    .setAllowClickDrained(io.support(IO.IN))
                    .background(GTGuiTextures.FLUID_SLOT)
            );
    }

    protected SlotGroupWidget createMultiSlotUI() {
        return SlotGroupWidget.builder()
            .matrix(GTMuiMachineUtil.createSquareMatrix(slots, 'G'))
            .key(
                'G', i -> new GasTankWidget(this.tank, i)
                    .setAllowClickDrained(io.support(IO.IN))
                    .background(GTGuiTextures.FLUID_SLOT)
            )
            .build();
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

    private Component getGasNameText() {
        var gas = tank.getChemicalInTank(0);

        if (!gas.isEmpty()) {
            return gas.getTextComponent();
        }

        return Component.translatable(GasTankWidget.GAS_EMPTY);
    }

    private Component getGasAmountText() {
        var gas = tank.getChemicalInTank(0);

        if (!gas.isEmpty()) {
            return Component.literal(getFormattedGasAmount(gas));
        }

        return Component.empty();
    }

    public String getFormattedGasAmount(GasStack gasStack) {
        return String.format("%,d", gasStack.isEmpty() ? 0 : gasStack.getAmount());
    }
}
