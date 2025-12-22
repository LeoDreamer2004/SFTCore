package org.leodreamer.sftcore.mixin.ae2.misc;

import appeng.integration.modules.emi.EmiEncodePatternHandler;
import appeng.menu.me.items.PatternEncodingTermMenu;
import com.gregtechceu.gtceu.integration.emi.recipe.GTEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import org.leodreamer.sftcore.integration.ae2.feature.ISendToGTMachine;
import org.leodreamer.sftcore.integration.emi.IGTEmiRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EmiEncodePatternHandler.class)
public class EmiEncodePatternHandlerMixin<T extends PatternEncodingTermMenu> {
    @Inject(method = "craft", at = @At("HEAD"), remap = false)
    private void rememberGTRecipeType(EmiRecipe recipe, EmiCraftContext<T> context, CallbackInfoReturnable<Boolean> cir) {
        if (recipe instanceof GTEmiRecipe gtRecipe) {
            var menu = (ISendToGTMachine) context.getScreenHandler();
            var type = ((IGTEmiRecipe) gtRecipe).sftcore$recipe().recipeType;
            menu.sftcore$setGTType(type);
        }
    }
}
