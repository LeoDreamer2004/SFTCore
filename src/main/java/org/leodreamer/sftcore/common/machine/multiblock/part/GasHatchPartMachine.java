package org.leodreamer.sftcore.common.machine.multiblock.part;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;
import com.gregtechceu.gtceu.api.sync_system.annotations.SaveField;
import com.gregtechceu.gtceu.utils.ISubscription;
import com.lowdragmc.lowdraglib.gui.widget.ImageWidget;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import mekanism.api.chemical.gas.GasStack;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import org.leodreamer.sftcore.common.machine.trait.NotifiableGasTank;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GasHatchPartMachine extends TieredIOPartMachine {
    public static final long INITIAL_TANK_CAPACITY_1X = 8_000;
    public static final long INITIAL_TANK_CAPACITY_4X = 2_000;
    public static final long INITIAL_TANK_CAPACITY_9X = 1_000;

    @SaveField
    public final NotifiableGasTank tank;

    private final int slots;

    @Nullable
    protected TickableSubscription autoIOSubs;

    @Nullable
    protected ISubscription tankSubs;

    public GasHatchPartMachine(BlockEntityCreationInfo info, int tier, IO io, long initialCapacity, int slots) {
        super(info, tier, io);
        this.slots = slots;
        this.tank = attachTrait(createTank(initialCapacity, slots));
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

    protected void updateTankSubscription(Direction facing) {
        if (isWorkingEnabled() && (io.support(IO.IN) || io.support(IO.OUT))) {
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

        group.addWidget(new LabelWidget(8, 8, "sftcore.gui.gas_amount"));
        group.addWidget(new LabelWidget(8, 18, () -> formatGasAmount(tank.getChemicalInTank(0))));
        group.addWidget(new LabelWidget(8, 28, () -> getGasName(tank.getChemicalInTank(0)).getString()));

        group.setBackground(GuiTextures.BACKGROUND_INVERSE);
        return group;
    }

    protected Widget createMultiSlotGUI() {
        int size = (int) Math.sqrt(slots);

        var group = new WidgetGroup(0, 0, 18 * size + 16, 18 * size + 16);
        var container = new WidgetGroup(4, 4, 18 * size + 8, 18 * size + 8);

        int index = 0;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                int tankIndex = index++;
                container.addWidget(new LabelWidget(
                    4 + x * 18,
                    6 + y * 18,
                    () -> shortGasAmount(tank.getChemicalInTank(tankIndex))
                ));
            }
        }

        container.setBackground(GuiTextures.BACKGROUND_INVERSE);
        group.addWidget(container);
        return group;
    }

    private static Component getGasName(GasStack stack) {
        return stack.isEmpty() ? Component.translatable("gtceu.gui.empty") : stack.getTextComponent();
    }

    private static String formatGasAmount(GasStack stack) {
        return String.format("%,d", stack.isEmpty() ? 0 : stack.getAmount());
    }

    private static String shortGasAmount(GasStack stack) {
        if (stack.isEmpty()) {
            return "-";
        }

        long amount = stack.getAmount();
        if (amount >= 1_000_000) {
            return amount / 1_000_000 + "M";
        }
        if (amount >= 1_000) {
            return amount / 1_000 + "K";
        }
        return Long.toString(amount);
    }
}
