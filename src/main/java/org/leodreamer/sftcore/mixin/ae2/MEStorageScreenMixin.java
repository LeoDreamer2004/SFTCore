package org.leodreamer.sftcore.mixin.ae2;

import appeng.api.stacks.AEItemKey;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.me.common.MEStorageScreen;
import appeng.client.gui.me.common.Repo;
import appeng.client.gui.style.ScreenStyle;
import appeng.core.definitions.AEItems;
import appeng.menu.SlotSemantics;
import appeng.menu.me.common.GridInventoryEntry;
import appeng.menu.me.common.MEStorageMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MEStorageScreen.class)
public abstract class MEStorageScreenMixin<C extends MEStorageMenu>
        extends AEBaseScreen<C> {

    @Shadow(remap = false)
    @Final
    protected Repo repo;

    @Shadow(remap = false)
    protected abstract void handleGridInventoryEntryMouseClick(@Nullable GridInventoryEntry entry, int mouseButton, ClickType clickType);

    private MEStorageScreenMixin(C menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Inject(method = "slotClicked", at = @At("HEAD"), cancellable = true)
    private void letBlankPatternCraftable(Slot slot, int slotIdx, int mouseButton, ClickType clickType, CallbackInfo ci) {
        if (slot != null && slot.hasItem() && clickType == ClickType.CLONE
                && menu.getSlotSemantic(slot) == SlotSemantics.BLANK_PATTERN) {
            var item = AEItems.BLANK_PATTERN.asItem();
            for (var entry : repo.getAllEntries()) {
                var what = entry.getWhat();
                if (what instanceof AEItemKey itemWhat && itemWhat.getItem() == item) {
                    handleGridInventoryEntryMouseClick(entry, mouseButton, clickType);
                    break;
                }
            }
            ci.cancel();
        }
    }
}
