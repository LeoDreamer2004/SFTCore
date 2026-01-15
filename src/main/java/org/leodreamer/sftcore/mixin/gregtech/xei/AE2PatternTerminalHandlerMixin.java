package org.leodreamer.sftcore.mixin.gregtech.xei;

import org.leodreamer.sftcore.integration.emi.SFTEmiPatternHandler;

import com.gregtechceu.gtceu.integration.emi.recipe.Ae2PatternTerminalHandler;

import appeng.menu.me.items.PatternEncodingTermMenu;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * See <a href="https://github.com/GregTechCEu/GregTech-Modern/issues/4352">handler issue in GTM</a>
 */
@Mixin(Ae2PatternTerminalHandler.class)
public class AE2PatternTerminalHandlerMixin<T extends PatternEncodingTermMenu> {

    @Inject(method = "craft", at = @At("TAIL"), remap = false)
    private void handleEmiRecipe(
        EmiRecipe recipe,
        EmiCraftContext<T> context,
        CallbackInfoReturnable<Boolean> cir
    ) {
        SFTEmiPatternHandler.handleEmiRecipe(recipe, context);
    }
}
