package org.leodreamer.sftcore.mixin.ae2;

import appeng.api.client.AEKeyRendering;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AmountFormat;
import appeng.client.gui.me.common.MEStorageScreen;
import appeng.client.gui.me.common.StackSizeRenderer;
import appeng.client.gui.me.items.PatternEncodingTermScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.core.definitions.AEItems;
import appeng.menu.SlotSemantics;
import appeng.menu.me.common.MEStorageMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PatternEncodingTermScreen.class)
public class PatternEncodingTermScreenMixin<C extends MEStorageMenu> extends MEStorageScreen<C> {
    public PatternEncodingTermScreenMixin(C menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Inject(method = "renderSlot", at = @At("HEAD"), cancellable = true)
    private void renderBlankPatternSlotWithSmallFont(GuiGraphics guiGraphics, Slot s, CallbackInfo ci) {
        if (menu.getSlotSemantic(s) == SlotSemantics.BLANK_PATTERN && !s.getItem().isEmpty()) {
            AEItemKey what = AEItemKey.of(AEItems.BLANK_PATTERN);
            long storedAmount = s.getItem().getCount();
            AEKeyRendering.drawInGui(minecraft, guiGraphics, s.x, s.y, what);

            boolean useLargeFonts = config.isUseLargeFonts();
            AmountFormat format = useLargeFonts ? AmountFormat.SLOT_LARGE_FONT : AmountFormat.SLOT;
            String text = what.formatAmount(storedAmount, format);
            StackSizeRenderer.renderSizeLabel(guiGraphics, this.font, s.x, s.y, text, useLargeFonts);
            ci.cancel();
        }
    }
}
