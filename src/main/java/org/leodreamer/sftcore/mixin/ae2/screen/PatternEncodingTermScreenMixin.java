package org.leodreamer.sftcore.mixin.ae2.screen;

import appeng.api.client.AEKeyRendering;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AmountFormat;
import appeng.client.gui.me.common.MEStorageScreen;
import appeng.client.gui.me.common.StackSizeRenderer;
import appeng.client.gui.me.items.PatternEncodingTermScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.core.definitions.AEItems;
import appeng.menu.SlotSemantics;
import appeng.menu.me.items.PatternEncodingTermMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.leodreamer.sftcore.integration.ae2.feature.IGTTransferPanel;
import org.leodreamer.sftcore.integration.ae2.gui.GTTransferPanel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PatternEncodingTermScreen.class)
public class PatternEncodingTermScreenMixin<C extends PatternEncodingTermMenu> extends MEStorageScreen<C> implements IGTTransferPanel {

    @Unique
    GTTransferPanel sftcore$gtPanel;

    public PatternEncodingTermScreenMixin(C menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addGTPanel(PatternEncodingTermMenu menu, Inventory playerInventory, Component title, ScreenStyle style, CallbackInfo ci) {
        sftcore$gtPanel = new GTTransferPanel(menu);
        widgets.add("gtPanel", sftcore$gtPanel);
    }

    @Override
    public GTTransferPanel sftcore$gtPanel() {
        return sftcore$gtPanel;
    }

    @Inject(method = "renderSlot", at = @At("HEAD"), cancellable = true)
    private void renderBlankPatternWithSmallFontAndCraftable(GuiGraphics guiGraphics, Slot s, CallbackInfo ci) {
        if (menu.getSlotSemantic(s) == SlotSemantics.BLANK_PATTERN && s.hasItem()) {
            AEItemKey what = AEItemKey.of(AEItems.BLANK_PATTERN);
            long storedAmount = s.getItem().getCount();
            AEKeyRendering.drawInGui(minecraft, guiGraphics, s.x, s.y, what);

            boolean useLargeFonts = config.isUseLargeFonts();
            AmountFormat format = useLargeFonts ? AmountFormat.SLOT_LARGE_FONT : AmountFormat.SLOT;
            String text = what.formatAmount(storedAmount, format);
            StackSizeRenderer.renderSizeLabel(guiGraphics, this.font, s.x, s.y, text, useLargeFonts);

            if (repo.isCraftable(what)) {
                var poseStack = guiGraphics.pose();
                poseStack.pushPose();
                poseStack.translate(0, 0, 100); // Items are rendered with offset of 100, offset text too.
                StackSizeRenderer.renderSizeLabel(guiGraphics, this.font, s.x - 11, s.y - 11, "+", false);
                poseStack.popPose();
            }

            ci.cancel();
        }
    }

}
