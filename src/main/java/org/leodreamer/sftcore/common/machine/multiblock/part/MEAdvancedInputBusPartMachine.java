package org.leodreamer.sftcore.common.machine.multiblock.part;

import appeng.api.config.Actionable;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.networking.crafting.ICraftingRequester;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.core.definitions.AEItems;
import com.google.common.collect.ImmutableSet;
import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.gui.widget.SlotWidget;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.sync_system.annotations.SaveField;
import com.gregtechceu.gtceu.integration.ae2.machine.MEInputBusPartMachine;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.annotation.ReadOnlyManaged;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import org.leodreamer.sftcore.api.gui.SFTGuiTextures;
import org.leodreamer.sftcore.integration.ae2.slot.IOAEItemList;
import org.leodreamer.sftcore.integration.ae2.slot.MEInputUpgradeInventory;
import org.leodreamer.sftcore.integration.ae2.utils.SerializableMultiCraftingTracker;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MEAdvancedInputBusPartMachine extends MEInputBusPartMachine implements ICraftingRequester {

    @Persisted
    @SaveField
    private final MEInputUpgradeInventory upgradeInventory;

    @Persisted
    @ReadOnlyManaged(
        onDirtyMethod = "onCraftingDirty",
        serializeMethod = "serializeCrafting",
        deserializeMethod = "deserializeCrafting"
    )
    public SerializableMultiCraftingTracker craftingTracker;

    public MEAdvancedInputBusPartMachine(BlockEntityCreationInfo holder) {
        super(holder);
        craftingTracker = new SerializableMultiCraftingTracker(this, CONFIG_SIZE);
        upgradeInventory = attachTrait(new MEInputUpgradeInventory());
    }

    @Override
    protected NotifiableItemStackHandler createInventory() {
        this.aeItemHandler = new IOAEItemList(CONFIG_SIZE);
        return this.aeItemHandler;
    }

    @Override
    public void syncME() {
        super.syncME();
        // Now the fill is completed
        // see if we need to autocraft
        autocraft();
    }

    private void autocraft() {
        if (!upgradeInventory.installed(AEItems.CRAFTING_CARD)) return;

        var grid = getMainNode().getGrid();
        if (grid == null) return;
        for (int idx = 0; idx < aeItemHandler.getInventory().length; idx++) {
            var req = aeItemHandler.getInventory()[idx].requestStack();
            if (req == null || req.amount() <= 0) continue;

            craftingTracker
                .handleCrafting(idx, req.what(), req.amount(), getLevel(), grid.getCraftingService(), actionSource);
        }
    }

    @Override
    public Widget createUIWidget() {
        var modular = (WidgetGroup) super.createUIWidget();
        for (int i = 0; i < upgradeInventory.getSlots(); i++) {
            var slot = new SlotWidget(upgradeInventory, i, 80 + i * 18, 90, true, true);
            slot.setBackgroundTexture(SFTGuiTextures.CARD_UPDATE);
            modular.addWidget(slot);
        }
        return modular;
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

    @SuppressWarnings("unused")
    public boolean onCraftingDirty(SerializableMultiCraftingTracker tracker) {
        return true;
    }

    @SuppressWarnings("unused")
    public CompoundTag serializeCrafting(SerializableMultiCraftingTracker tracker) {
        var tag = new CompoundTag();
        tracker.writeToNBT(tag);
        return tag;
    }

    @SuppressWarnings("unused")
    public SerializableMultiCraftingTracker deserializeCrafting(CompoundTag tag) {
        craftingTracker.readFromNBT(tag);
        return craftingTracker;
    }
}
