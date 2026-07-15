package org.leodreamer.sftcore.common.machine.multiblock.part;

import org.leodreamer.sftcore.api.gui.SFTGuiTextures;
import org.leodreamer.sftcore.integration.ae2.slot.IOAEItemList;
import org.leodreamer.sftcore.integration.ae2.slot.MEInputUpgradeInventory;
import org.leodreamer.sftcore.integration.ae2.utils.SerializableMultiCraftingTracker;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.sync_system.annotations.SaveField;
import com.gregtechceu.gtceu.integration.ae2.machine.MEInputBusPartMachine;

import net.minecraft.MethodsReturnNonnullByDefault;

import appeng.api.config.Actionable;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.networking.crafting.ICraftingRequester;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.core.definitions.AEItems;
import brachy.modularui.factory.PosGuiData;
import brachy.modularui.screen.UISettings;
import brachy.modularui.value.sync.PanelSyncManager;
import brachy.modularui.value.sync.SyncHandlers;
import brachy.modularui.widget.ParentWidget;
import brachy.modularui.widgets.layout.Flow;
import brachy.modularui.widgets.slot.ItemSlot;
import com.google.common.collect.ImmutableSet;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MEAdvancedInputBusPartMachine extends MEInputBusPartMachine implements ICraftingRequester {

    @SaveField
    private final MEInputUpgradeInventory upgradeInventory;

    @SaveField
    public SerializableMultiCraftingTracker craftingTracker;

    public MEAdvancedInputBusPartMachine(BlockEntityCreationInfo holder) {
        super(holder, new IOAEItemList(CONFIG_SIZE));
        this.aeItemHandler = (IOAEItemList) getInventory();
        craftingTracker = new SerializableMultiCraftingTracker(this, CONFIG_SIZE);
        upgradeInventory = attachTrait(new MEInputUpgradeInventory());
    }

    @Override
    public void syncME() {
        super.syncME();
        // Now the fill is completed
        // see if we need to autocraft
        autocraft();
    }

    private void autocraft() {
        if (!upgradeInventory.installed(AEItems.CRAFTING_CARD)) {
            return;
        }
        var grid = getMainNode().getGrid();
        if (grid == null) {
            return;
        }
        for (int idx = 0; idx < aeItemHandler.getInventory().length; idx++) {
            var req = aeItemHandler.getInventory()[idx].requestStack();
            if (req == null || req.amount() <= 0) continue;

            craftingTracker
                .handleCrafting(idx, req.what(), req.amount(), getLevel(), grid.getCraftingService(), actionSource);
        }
    }

    @Override
    public void buildMainUI(
        ParentWidget<?> mainWidget, PosGuiData guiData, PanelSyncManager syncManager,
        UISettings settings
    ) {
        super.buildMainUI(mainWidget, guiData, syncManager, settings);
        var cards = Flow.row().coverChildren().right(4).bottom(4);
        for (int i = 0; i < upgradeInventory.getSlots(); i++) {
            cards.child(
                new ItemSlot()
                    .slot(SyncHandlers.itemSlot(upgradeInventory, i).accessibility(true, true))
                    .background(SFTGuiTextures.CARD_UPDATE)
            );
        }
        mainWidget.child(cards);
    }

    @Override
    public ImmutableSet<ICraftingLink> getRequestedJobs() {
        return craftingTracker.getRequestedJobs();
    }

    @Override
    public long insertCraftedItems(ICraftingLink link, AEKey what, long amount, Actionable mode) {
        long left = amount;
        for (var slot : aeItemHandler.getInventory()) {
            var req = slot.requestStack();
            if (req != null && req.what() == what) {
                long inserted = Math.min(left, req.amount());
                slot.addStack(new GenericStack(req.what(), inserted));
                left -= inserted;
                if (left <= 0) {
                    return amount;
                }
            }
        }
        return amount - left;
    }

    @Override
    public void jobStateChange(ICraftingLink link) {
        craftingTracker.jobStateChange(link);
    }
}
