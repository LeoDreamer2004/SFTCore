package org.leodreamer.sftcore.common.machine.multiblock.part;

import org.leodreamer.sftcore.api.gui.AEGasListGridWidget;
import org.leodreamer.sftcore.common.machine.trait.gas.MEGasOutputHandler;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;
import com.gregtechceu.gtceu.api.sync_system.annotations.SaveField;
import com.gregtechceu.gtceu.api.sync_system.annotations.SyncToClient;
import com.gregtechceu.gtceu.integration.ae2.machine.feature.IGridConnectedMachine;
import com.gregtechceu.gtceu.integration.ae2.machine.trait.GridNodeHolder;
import com.gregtechceu.gtceu.integration.ae2.utils.KeyStorage;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;

import appeng.api.networking.IGridNodeListener;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.security.IActionSource;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MEGasOutputHatchPartMachine extends TieredIOPartMachine implements IGridConnectedMachine {

    @SaveField
    protected final GridNodeHolder nodeHolder;

    @SaveField
    protected final KeyStorage internalBuffer = new KeyStorage();

    @SaveField
    @Getter
    protected final MEGasOutputHandler gasHandler;

    @SyncToClient
    @Getter
    protected boolean isOnline;

    protected final IActionSource actionSource;

    @Nullable
    protected TickableSubscription autoIOSubs;

    public MEGasOutputHatchPartMachine(BlockEntityCreationInfo info) {
        super(info, GTValues.EV, IO.OUT);

        this.nodeHolder = attachTrait(new GridNodeHolder(this));
        this.actionSource = IActionSource.ofMachine(nodeHolder.getMainNode()::getNode);

        this.gasHandler = attachTrait(new MEGasOutputHandler(this.internalBuffer));

        this.internalBuffer.setOnContentsChanged(() -> {
            this.gasHandler.notifyListeners();
            this.updateGasSubscription();
        });
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

        var grid = getMainNode().getGrid();
        if (grid != null && !internalBuffer.isEmpty()) {
            internalBuffer.insertInventory(grid.getStorageService().getInventory(), actionSource);
        }
    }

    @Override
    public void onMainNodeStateChanged(IGridNodeListener.State reason) {
        IGridConnectedMachine.super.onMainNodeStateChanged(reason);
        updateGasSubscription();
    }

    @Override
    public void onNeighborChanged(Block block, net.minecraft.core.BlockPos fromPos, boolean isMoving) {
        super.onNeighborChanged(block, fromPos, isMoving);
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
        return isWorkingEnabled() && isOnline() && !internalBuffer.isEmpty();
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
        if (!shouldSyncME()) {
            return;
        }
        if (updateMEStatus()) {
            var grid = getMainNode().getGrid();

            if (grid != null && !internalBuffer.isEmpty()) {
                internalBuffer.insertInventory(grid.getStorageService().getInventory(), actionSource);
            }

            updateGasSubscription();
        }
    }

    @Override
    public Widget createUIWidget() {
        var group = new WidgetGroup(0, 0, 170, 65);

        group.addWidget(
            new LabelWidget(
                5,
                0,
                () -> this.isOnline ? "gtceu.gui.me_network.online" : "gtceu.gui.me_network.offline"
            )
        );

        group.addWidget(new LabelWidget(5, 10, "gtceu.gui.waiting_list"));
        group.addWidget(new AEGasListGridWidget(5, 20, 3, this.internalBuffer));

        return group;
    }
}
