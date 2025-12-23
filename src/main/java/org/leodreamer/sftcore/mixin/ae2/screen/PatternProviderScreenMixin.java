package org.leodreamer.sftcore.mixin.ae2.screen;

import org.leodreamer.sftcore.common.data.lang.MixinTooltips;
import org.leodreamer.sftcore.integration.ae2.feature.IPromptProviderMenu;
import org.leodreamer.sftcore.integration.ae2.feature.IReceivePrompt;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.implementations.PatternProviderScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.AETextField;
import appeng.menu.implementations.PatternProviderMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PatternProviderScreen.class)
public class PatternProviderScreenMixin<C extends PatternProviderMenu> extends AEBaseScreen<C>
                                       implements IReceivePrompt {

    @Unique
    private AETextField sftcore$prompt;

    public PatternProviderScreenMixin(
                                      C menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addPromptField(
                                PatternProviderMenu menu,
                                Inventory playerInventory,
                                Component title,
                                ScreenStyle style,
                                CallbackInfo ci) {
        sftcore$prompt = widgets.addTextField("prompt");
        sftcore$prompt.setPlaceholder(Component.translatable(MixinTooltips.PATTERN_ENCODER_PROMPT));

        var provider = ((IPromptProviderMenu) menu);
        sftcore$prompt.setValue(provider.sftcore$getPrompt());
        sftcore$prompt.setResponder(provider::sftcore$setPrompt);
    }

    @Unique
    @Override
    public void sftcore$onReceive(String prompt) {
        sftcore$prompt.setValue(prompt);
    }

    @Override
    public void drawBG(
                       GuiGraphics guiGraphics,
                       int offsetX,
                       int offsetY,
                       int mouseX,
                       int mouseY,
                       float partialTicks) {
        super.drawBG(guiGraphics, offsetX, offsetY, mouseX, mouseY, partialTicks);
        sftcore$prompt.render(guiGraphics, mouseX, mouseY, partialTicks);
    }
}
