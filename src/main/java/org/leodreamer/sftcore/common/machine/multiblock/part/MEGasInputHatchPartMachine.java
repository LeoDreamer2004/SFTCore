package org.leodreamer.sftcore.common.machine.multiblock.part;

import org.leodreamer.sftcore.common.machine.trait.gas.MEGasInputHandler;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.IDataStickInteractable;
import com.gregtechceu.gtceu.api.machine.feature.IHasCircuitSlot;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.sync_system.annotations.SaveField;
import com.gregtechceu.gtceu.api.sync_system.annotations.SyncToClient;
import com.gregtechceu.gtceu.common.item.behavior.IntCircuitBehaviour;
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.gregtechceu.gtceu.integration.ae2.machine.feature.IGridConnectedMachine;
import com.gregtechceu.gtceu.integration.ae2.machine.trait.GridNodeHolder;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import appeng.api.networking.IGridNodeListener;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.GenericStack;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import lombok.Getter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Still unavailable until GTM finished their LDLib removal refactor.
 */
@ApiStatus.Experimental
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MEGasInputHatchPartMachine extends TieredIOPartMachine
    implements IGridConnectedMachine, IDataStickInteractable, IHasCircuitSlot {

    protected static final int CONFIG_SIZE = 16;

    @SaveField
    protected final GridNodeHolder nodeHolder;

    protected final IActionSource actionSource;

    @SaveField
    @Getter
    protected final MEGasInputHandler gasHandler;

    @SaveField
    @Getter
    protected final NotifiableItemStackHandler circuitInventory;

    @SyncToClient
    @Getter
    protected boolean isOnline;

    @Nullable
    protected TickableSubscription autoIOSubs;

    public MEGasInputHatchPartMachine(BlockEntityCreationInfo info) {
        super(info, GTValues.EV, IO.IN);

        this.nodeHolder = attachTrait(new GridNodeHolder(this));
        this.actionSource = IActionSource.ofMachine(nodeHolder.getMainNode()::getNode);

        this.gasHandler = attachTrait(createGasHandler());

        this.circuitInventory = attachTrait(new NotifiableItemStackHandler(1, IO.IN, IO.NONE))
            .setFilter(IntCircuitBehaviour::isIntegratedCircuit)
            .shouldSearchContent(false)
            .shouldDropInventoryInWorld(!ConfigHolder.INSTANCE.machines.ghostCircuit);

        this.gasHandler.notifyListeners();
    }

    protected MEGasInputHandler createGasHandler() {
        return new MEGasInputHandler(CONFIG_SIZE);
    }

    @Override
    public IManagedGridNode getMainNode() {
        return nodeHolder.getMainNode();
    }

    @Override
    public void setOnline(boolean online) {
        this.isOnline = online;
        syncDataHolder.markClientSyncFieldDirty("isOnline");
    }

    @Override
    public void onLoad() {
        super.onLoad();
        scheduleForNextServerTick(this::updateGasSubscription);
        getHandlerList().setColor(getPaintingColor());
    }

    @Override
    public void onUnload() {
        super.onUnload();

        if (autoIOSubs != null) {
            autoIOSubs.unsubscribe();
            autoIOSubs = null;
        }
    }

    @Override
    public void onMachineDestroyed() {
        super.onMachineDestroyed();
        flushInventory();
    }

    @Override
    public void onMainNodeStateChanged(IGridNodeListener.State reason) {
        IGridConnectedMachine.super.onMainNodeStateChanged(reason);
        updateGasSubscription();
    }

    @Override
    public void onRotated(Direction oldFacing, Direction newFacing) {
        super.onRotated(oldFacing, newFacing);
        getMainNode().setExposedOnSides(EnumSet.of(newFacing));
    }

    @Override
    public void setWorkingEnabled(boolean workingEnabled) {
        super.setWorkingEnabled(workingEnabled);
        updateGasSubscription();
    }

    protected boolean shouldSubscribe() {
        return isWorkingEnabled() && isOnline();
    }

    protected void updateGasSubscription() {
        if (shouldSubscribe()) {
            autoIOSubs = subscribeServerTick(autoIOSubs, this::autoIO);
        } else if (autoIOSubs != null) {
            autoIOSubs.unsubscribe();
            autoIOSubs = null;
        }
    }

    protected void autoIO() {
        if (!isWorkingEnabled()) {
            updateGasSubscription();
            return;
        }

        if (!shouldSyncME()) {
            return;
        }

        if (updateMEStatus()) {
            syncME();
            updateGasSubscription();
        }
    }

    protected void syncME() {
        var grid = getMainNode().getGrid();
        if (grid == null) {
            return;
        }

        var network = grid.getStorageService().getInventory();
        gasHandler.syncFromME(network, actionSource);
    }

    protected void flushInventory() {
        var grid = getMainNode().getGrid();
        gasHandler.flushToME(
            grid == null ? null : grid.getStorageService().getInventory(),
            actionSource
        );
    }

    @Override
    public Widget createUIWidget() {
        var group = new WidgetGroup(0, 0, 170, 85);

        group.addWidget(
            new LabelWidget(
                3,
                0,
                () -> this.isOnline ? "gtceu.gui.me_network.online" : "gtceu.gui.me_network.offline"
            )
        );

        // group.addWidget(new AEGasConfigWidget(3, 12, this.gasHandler));

        return group;
    }

    @Override
    public final InteractionResult onDataStickShiftUse(Player player, ItemStack dataStick) {
        if (!isRemote()) {
            var tag = new CompoundTag();
            tag.put("MEGasInputHatch", writeConfigToTag());
            dataStick.setTag(tag);
            player.sendSystemMessage(Component.translatable("gtceu.machine.me.import_copy_settings"));
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public final InteractionResult onDataStickUse(Player player, ItemStack dataStick) {
        var tag = dataStick.getTag();

        if (tag == null || !tag.contains("MEGasInputHatch")) {
            return InteractionResult.PASS;
        }

        if (!isRemote()) {
            readConfigFromTag(tag.getCompound("MEGasInputHatch"));
            updateGasSubscription();
            player.sendSystemMessage(Component.translatable("gtceu.machine.me.import_paste_settings"));
        }

        return InteractionResult.sidedSuccess(isRemote());
    }

    protected CompoundTag writeConfigToTag() {
        var tag = new CompoundTag();
        var configStacks = new CompoundTag();
        tag.put("ConfigStacks", configStacks);

        for (int i = 0; i < CONFIG_SIZE; i++) {
            GenericStack config = gasHandler.getInventory()[i].getConfig();
            if (config != null) {
                configStacks.put(Integer.toString(i), GenericStack.writeTag(config));
            }
        }

        tag.putByte(
            "GhostCircuit", (byte) IntCircuitBehaviour.getCircuitConfiguration(
                circuitInventory.getStackInSlot(0)
            )
        );

        return tag;
    }

    protected void readConfigFromTag(CompoundTag tag) {
        if (tag.contains("ConfigStacks")) {
            var configStacks = tag.getCompound("ConfigStacks");

            for (int i = 0; i < CONFIG_SIZE; i++) {
                String key = Integer.toString(i);

                if (configStacks.contains(key)) {
                    gasHandler.getInventory()[i].setConfig(GenericStack.readTag(configStacks.getCompound(key)));
                } else {
                    gasHandler.getInventory()[i].setConfig(null);
                }

                gasHandler.getInventory()[i].setStock(null);
            }
        }

        if (tag.contains("GhostCircuit")) {
            circuitInventory.setStackInSlot(0, IntCircuitBehaviour.stack(tag.getByte("GhostCircuit")));
        }
    }
}
