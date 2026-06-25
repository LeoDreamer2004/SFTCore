package org.leodreamer.sftcore.common.machine.multiblock.part;

import org.leodreamer.sftcore.common.machine.trait.gas.MEGasInputHandler;
import org.leodreamer.sftcore.common.machine.trait.gas.MEGasStockingInputHandler;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.api.sync_system.annotations.SaveField;
import com.gregtechceu.gtceu.api.sync_system.annotations.SyncToClient;
import com.gregtechceu.gtceu.common.item.behavior.IntCircuitBehaviour;
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.gregtechceu.gtceu.integration.ae2.machine.feature.multiblock.IMEStockingPart;
import com.gregtechceu.gtceu.integration.ae2.slot.IConfigurableSlotList;
import com.gregtechceu.gtceu.utils.ExtendedUseOnContext;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;

import appeng.api.stacks.GenericStack;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

import javax.annotation.ParametersAreNonnullByDefault;

@ApiStatus.Experimental
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
    @Setter
    @SaveField
    private int ticksPerCycle = 40;

    @Setter
    private Predicate<GenericStack> autoPullTest = $ -> false;

    public MEGasStockingInputHatchPartMachine(BlockEntityCreationInfo info) {
        super(info);
    }

    @Override
    protected MEGasInputHandler createGasHandler() {
        return new MEGasStockingInputHandler(CONFIG_SIZE);
    }

    protected MEGasStockingInputHandler stockingHandler() {
        return (MEGasStockingInputHandler) gasHandler;
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
    protected void syncME() {
        var grid = getMainNode().getGrid();
        if (grid == null) {
            stockingHandler().bindNetwork(false, null, actionSource);
            gasHandler.clearInventory(0);
            return;
        }

        var network = grid.getStorageService().getInventory();
        stockingHandler().bindNetwork(isOnline(), network, actionSource);

        if (ticksPerCycle == 0) {
            ticksPerCycle = ConfigHolder.INSTANCE.compat.ae2.updateIntervals;
        }

        if (autoPull && getOffsetTimer() % ticksPerCycle == 0) {
            stockingHandler().refreshList(network, actionSource, minStackSize, autoPullTest);
        }

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
        if (config == null || !isFormed()) {
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
                syncME();
                updateGasSubscription();
            }
        }
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

    @Override
    protected CompoundTag writeConfigToTag() {
        if (!autoPull) {
            var tag = super.writeConfigToTag();
            tag.putBoolean("AutoPull", false);
            return tag;
        }

        var tag = new CompoundTag();
        tag.putBoolean("AutoPull", true);
        tag.putByte(
            "GhostCircuit", (byte) IntCircuitBehaviour.getCircuitConfiguration(
                circuitInventory.getStackInSlot(0)
            )
        );

        return tag;
    }

    @Override
    protected void readConfigFromTag(CompoundTag tag) {
        if (tag.getBoolean("AutoPull")) {
            setAutoPull(true);
            circuitInventory.setStackInSlot(0, IntCircuitBehaviour.stack(tag.getByte("GhostCircuit")));
            return;
        }

        setAutoPull(false);
        super.readConfigFromTag(tag);
    }
}
