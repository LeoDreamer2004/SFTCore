package org.leodreamer.sftcore.mixin.emi;

import org.leodreamer.sftcore.common.data.lang.MixinTooltips;
import org.leodreamer.sftcore.integration.emi.EmiRecipeAutocraft;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.screen.EmiScreenBase;
import dev.emi.emi.screen.EmiScreenManager;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EmiScreenManager.class)
public abstract class EmiScreenManagerMixin {

    @Shadow(remap = false)
    private static Minecraft client;

    @Shadow(remap = false)
    public static int getDebugTextX() {
        return 0;
    }

    @Inject(method = "keyPressed", at = @At("TAIL"), remap = false)
    private static void enableAutoFill(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (keyCode == GLFW.GLFW_KEY_TAB) {
            client.tell(EmiRecipeAutocraft::autoFillAvailableRecipes);
        }
    }

    @Inject(method = "render", at = @At("TAIL"), remap = false)
    private static void addAutocraftTooltips(
        EmiDrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci
    ) {
        if (EmiRecipeAutocraft.findFocus() != null) {
            context.drawTextWithShadow(
                Component.translatable(MixinTooltips.EMI_AUTOCRAFT),
                getDebugTextX(), EmiScreenBase.getCurrent().screen().height - 16
            );
        }
    }
}
