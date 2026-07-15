package org.leodreamer.sftcore.common.machine.multiblock.part;

import org.leodreamer.sftcore.api.gui.gas.ExportOnlyAEGasSlot;
import org.leodreamer.sftcore.api.gui.gas.GasGuiHelper;
import org.leodreamer.sftcore.common.machine.trait.gas.ExportOnlyAEGasList;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.api.sync_system.annotations.SaveField;
import com.gregtechceu.gtceu.api.sync_system.annotations.SyncToClient;
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.gregtechceu.gtceu.integration.ae2.machine.feature.multiblock.IMEStockingPart;
import com.gregtechceu.gtceu.integration.ae2.slot.ExportOnlyAESlot;
import com.gregtechceu.gtceu.integration.ae2.slot.IConfigurableSlotList;
import com.gregtechceu.gtceu.utils.ExtendedUseOnContext;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.MEStorage;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import lombok.Getter;
import lombok.Setter;
import mekanism.api.Action;
import mekanism.api.chemical.gas.GasStack;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.function.Predicate;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Copy version for gas from {@link com.gregtechceu.gtceu.integration.ae2.machine.MEStockingHatchPartMachine}
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MEGasStockingInputHatchPartMachine extends MEGasInputHatchPartMachine implements IMEStockingPart {

    @SyncToClient
    @SaveField
    @Getter
    private boolean autoPull;

    @Getter
    @Setter
    @SaveField
    private int minStackSize = 1;

    @Getter
    @SaveField
    private int ticksPerCycle = 40;

    @Setter
    private Predicate<GenericStack> autoPullTest;

    public MEGasStockingInputHatchPartMachine(BlockEntityCreationInfo info) {
        super(info);
        this.autoPullTest = $ -> false;
        setOffsetBound(ticksPerCycle);
    }

    @Override
    protected ExportOnlyAEGasList createGasHandler(int slots) {
        return new ExportOnlyAEStockingGasList(slots);
    }

    private ExportOnlyAEStockingGasList stockingHandler() {
        return (ExportOnlyAEStockingGasList) gasHandler;
    }

    @Override
    public void addedToController(MultiblockControllerMachine controller, String name) {
        super.addedToController(controller, name);
        IMEStockingPart.super.addedToController(controller, name);
    }

    @Override
    public void removedFromController(MultiblockControllerMachine controller) {
        IMEStockingPart.super.removedFromController(controller);
        super.removedFromController(controller);
    }

    @Override
    protected void autoIO() {
        super.autoIO();
        if (ticksPerCycle == 0) {
            ticksPerCycle = ConfigHolder.INSTANCE.compat.ae2.updateIntervals;
        }
        if (getOffsetTimer() % ticksPerCycle == 0) {
            if (autoPull) {
                refreshList();
            }
            syncME();
        }
    }

    @Override
    protected void syncME() {
        var grid = getMainNode().getGrid();
        if (grid == null) {
            return;
        }
        var network = grid.getStorageService().getInventory();
        stockingHandler().syncFromME(network, actionSource);
    }

    @Override
    protected void flushInventory() {
        // Stocking hatch does not own real gas.
    }

    @Override
    public IConfigurableSlotList getSlotList() {
        return gasHandler;
    }

    @Override
    public boolean testConfiguredInOtherPart(@Nullable GenericStack config) {
        if (!GasGuiHelper.isGas(config) || !isFormed()) {
            return false;
        }

        for (var controller : getControllers()) {
            for (var part : controller.getParts()) {
                if (part instanceof MEGasStockingInputHatchPartMachine hatch && hatch != this) {
                    if (hatch.gasHandler.hasStackInConfig(config, false)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void setAutoPull(boolean autoPull) {
        this.autoPull = autoPull;

        if (!isRemote()) {
            syncDataHolder.markClientSyncFieldDirty("autoPull");

            if (!autoPull) {
                gasHandler.clearInventory(0);
            } else if (updateMEStatus()) {
                refreshList();
                updateTankSubscription();
            }
        }
    }

    @Override
    public void setTicksPerCycle(int ticksPerCycle) {
        this.ticksPerCycle = ticksPerCycle;
        setOffsetBound(ticksPerCycle);
    }

    @Override
    protected InteractionResult onScrewdriverClick(ExtendedUseOnContext context) {
        if (!isRemote()) {
            setAutoPull(!autoPull);

            context.getPlayer().sendSystemMessage(
                Component.translatable(
                    autoPull ? "gtceu.machine.me.stocking_auto_pull_enabled" :
                        "gtceu.machine.me.stocking_auto_pull_disabled"
                )
            );
        }

        return InteractionResult.sidedSuccess(isRemote());
    }

    private void refreshList() {
        var grid = getMainNode().getGrid();
        if (grid == null) {
            gasHandler.clearInventory(0);
            return;
        }

        var network = grid.getStorageService().getInventory();
        var topGases = new PriorityQueue<>(Comparator.comparingLong(Object2LongMap.Entry<AEKey>::getLongValue));

        for (var entry : network.getAvailableStacks()) {
            long amount = entry.getLongValue();
            var what = entry.getKey();

            if (amount <= 0) {
                continue;
            }
            if (!GasGuiHelper.isGas(what)) {
                continue;
            }

            long request = network.extract(what, amount, Actionable.SIMULATE, actionSource);
            if (request == 0) {
                continue;
            }

            if (autoPullTest != null && !autoPullTest.test(new GenericStack(what, amount))) {
                continue;
            }
            if (amount >= minStackSize) {
                if (topGases.size() < CONFIG_SIZE) {
                    topGases.offer(entry);
                } else if (topGases.peek() != null && amount > topGases.peek().getLongValue()) {
                    topGases.poll();
                    topGases.offer(entry);
                }
            }
        }

        int index;
        int gasAmount = topGases.size();
        for (index = 0; index < CONFIG_SIZE; index++) {
            if (topGases.isEmpty()) {
                break;
            }
            var entry = topGases.poll();
            var what = entry.getKey();
            long amount = entry.getLongValue();

            long request = network.extract(what, amount, Actionable.SIMULATE, actionSource);

            var slot = gasHandler.getInventory()[gasAmount - index - 1];
            slot.setConfig(new GenericStack(what, 1));
            slot.setStock(new GenericStack(what, request));
        }

        gasHandler.clearInventory(index);
    }

    @Override
    protected CompoundTag writeConfigToTag() {
        if (!autoPull) {
            var tag = super.writeConfigToTag();
            tag.putBoolean("AutoPull", false);
            return tag;
        }

        var tag = new CompoundTag();
        tag.putBoolean("AutoPull", true);
        tag.putByte("GhostCircuit", (byte) circuitSlot.getCurrentCircuit());

        return tag;
    }

    @Override
    protected void readConfigFromTag(CompoundTag tag) {
        if (tag.getBoolean("AutoPull")) {
            setAutoPull(true);
            circuitSlot.setCurrentCircuit(tag.getByte("GhostCircuit"));
            return;
        }

        setAutoPull(false);
        super.readConfigFromTag(tag);
    }

    private class ExportOnlyAEStockingGasList extends ExportOnlyAEGasList {

        public ExportOnlyAEStockingGasList(int slots) {
            super(slots);
        }

        @Override
        protected ExportOnlyAEGasSlot createSlot() {
            return new ExportOnlyAEStockingGasSlot();
        }

        @Override
        public void syncFromME(MEStorage network, IActionSource actionSource) {
            for (var slot : inventory) {
                var config = slot.getConfig();

                if (!GasGuiHelper.isGas(config)) {
                    slot.setStock(null);
                    continue;
                }

                long available = network.extract(
                    config.what(),
                    Long.MAX_VALUE,
                    Actionable.SIMULATE,
                    actionSource
                );

                if (available >= minStackSize) {
                    slot.setStock(new GenericStack(config.what(), available));
                    continue;
                }

                slot.setStock(null);
            }
        }

        @Override
        public void flushToME(@Nullable MEStorage network, IActionSource actionSource) {
            // Stocking hatch has no real local gas buffer.
        }

        @Override
        public boolean isAutoPull() {
            return autoPull;
        }

        @Override
        public boolean isStocking() {
            return true;
        }

        @Override
        public boolean hasStackInConfig(GenericStack stack, boolean checkExternal) {
            boolean inThisHatch = super.hasStackInConfig(stack, false);
            if (inThisHatch) {
                return true;
            }
            return checkExternal && testConfiguredInOtherPart(stack);
        }
    }

    private class ExportOnlyAEStockingGasSlot extends ExportOnlyAEGasSlot {

        public ExportOnlyAEStockingGasSlot() {
            super();
        }

        public ExportOnlyAEStockingGasSlot(@Nullable GenericStack config, @Nullable GenericStack stock) {
            super(config, stock);
        }

        @Override
        public ExportOnlyAEGasSlot copy() {
            return new ExportOnlyAEStockingGasSlot(
                this.config == null ? null : ExportOnlyAESlot.copy(this.config),
                this.stock == null ? null : ExportOnlyAESlot.copy(this.stock)
            );
        }

        @Override
        public GasStack drain(GasStack requested, Action action) {
            if (requested.isEmpty() || this.stock == null || this.config == null || !isOnline()) {
                return GasStack.EMPTY;
            }

            var grid = getMainNode().getGrid();
            if (grid == null) {
                return GasStack.EMPTY;
            }

            var requestedKey = GasGuiHelper.getGasKey(requested);
            if (!this.config.what().equals(requestedKey)) {
                return GasStack.EMPTY;
            }

            var actionable = action.simulate() ? Actionable.SIMULATE : Actionable.MODULATE;
            var network = grid.getStorageService().getInventory();
            long extracted = network.extract(requestedKey, requested.getAmount(), actionable, actionSource);
            if (extracted <= 0) {
                return GasStack.EMPTY;
            }

            var result = GasGuiHelper.getGasStack(new GenericStack(requestedKey, extracted));
            if (!result.isEmpty() && action.execute()) {
                this.stock = ExportOnlyAESlot.copy(stock, stock.amount() - extracted);
                if (this.stock.amount() == 0) {
                    this.stock = null;
                }
                onContentsChanged.run();
            }

            return result;
        }
    }
}
