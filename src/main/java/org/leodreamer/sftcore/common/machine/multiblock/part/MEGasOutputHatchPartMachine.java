package org.leodreamer.sftcore.common.machine.multiblock.part;

import org.leodreamer.sftcore.common.machine.trait.gas.MEGasOutputHandler;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.IMuiMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;
import com.gregtechceu.gtceu.api.sync_system.annotations.SaveField;
import com.gregtechceu.gtceu.api.sync_system.annotations.SyncToClient;
import com.gregtechceu.gtceu.integration.ae2.gui.AEKeyStorageSyncHandler;
import com.gregtechceu.gtceu.integration.ae2.gui.AEStackDisplayWidget;
import com.gregtechceu.gtceu.integration.ae2.gui.ScrollPreservingGrid;
import com.gregtechceu.gtceu.integration.ae2.machine.feature.IGridConnectedMachine;
import com.gregtechceu.gtceu.integration.ae2.machine.trait.GridNodeHolder;
import com.gregtechceu.gtceu.integration.ae2.utils.KeyStorage;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;

import appeng.api.networking.IGridNodeListener;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.security.IActionSource;
import brachy.modularui.api.drawable.Text;
import brachy.modularui.factory.PosGuiData;
import brachy.modularui.screen.UISettings;
import brachy.modularui.value.sync.BooleanSyncValue;
import brachy.modularui.value.sync.DynamicLinkedSyncHandler;
import brachy.modularui.value.sync.PanelSyncManager;
import brachy.modularui.widget.ParentWidget;
import brachy.modularui.widget.scroll.VerticalScrollData;
import brachy.modularui.widgets.TextWidget;
import brachy.modularui.widgets.dynamic.DynamicWidget;
import brachy.modularui.widgets.layout.Flow;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MEGasOutputHatchPartMachine extends TieredIOPartMachine implements IMuiMachine, IGridConnectedMachine {

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
    public void buildMainUI(
        ParentWidget<?> mainWidget, PosGuiData guiData, PanelSyncManager syncManager,
        UISettings settings
    ) {
        BooleanSyncValue isOnlineValue = new BooleanSyncValue(this::isOnline, this::setOnline);
        syncManager.syncValue("is_online", isOnlineValue);

        var flow = Flow.col().coverChildren();
        flow.child(
            Text.dynamic(
                () -> isOnlineValue.getBoolValue() ?
                    net.minecraft.network.chat.Component.translatable("gtceu.gui.me_network.online") :
                    net.minecraft.network.chat.Component.translatable("gtceu.gui.me_network.offline")
            )
                .asWidget().marginTop(2).marginBottom(4)
        );

        var storageSyncHandler = new AEKeyStorageSyncHandler(internalBuffer);
        syncManager.syncValue("ae_output_display", storageSyncHandler);

        int[] savedScroll = { 0 };
        var dynamicHandler = new DynamicLinkedSyncHandler<>(storageSyncHandler)
            .widgetProvider((sm, value) -> {
                var col = Flow.col().leftRel(0.5f).coverChildrenHeight();
                var list = value.getValue();
                if (list.isEmpty()) {
                    return col.child(new TextWidget<>(Text.lang("gtceu.gui.waiting_list_empty")));
                }
                col.child(new TextWidget<>(Text.lang("gtceu.gui.waiting_list")).margin(0, 2));
                col.child(
                    new ScrollPreservingGrid(savedScroll)
                        .size(167, 80)
                        .scrollable(new VerticalScrollData())
                        .gridOfSizeWidth(9, 1, (x, y, index) -> new AEStackDisplayWidget(list, index))
                );
                return col;
            });

        flow.child(
            new DynamicWidget<>()
                .syncHandler(dynamicHandler)
                .size(167, 80)
        );

        mainWidget.child(flow.center());
    }
}
