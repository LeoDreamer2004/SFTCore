package org.leodreamer.sftcore.mixin.gregtech.xei;

import org.leodreamer.sftcore.integration.emi.auto.SFTEmiPatternHandler;
import org.leodreamer.sftcore.integration.emi.recipe.IGTEmiRecipe;

import com.gregtechceu.gtceu.common.data.GTRecipeCapabilities;
import com.gregtechceu.gtceu.integration.recipeviewer.emi.recipe.Ae2PatternTerminalHandler;

import appeng.api.stacks.GenericStack;
import appeng.menu.me.items.PatternEncodingTermMenu;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

/**
 * See <a href="https://github.com/GregTechCEu/GregTech-Modern/issues/4352">handler issue in GTM</a>
 */
@Mixin(Ae2PatternTerminalHandler.class)
public class AE2PatternTerminalHandlerMixin<T extends PatternEncodingTermMenu> {

    @Inject(method = "ofInputs", at = @At("RETURN"), remap = false, cancellable = true)
    private static void removeItemCatalysts(
        EmiRecipe recipe,
        CallbackInfoReturnable<List<List<GenericStack>>> cir
    ) {
        if (!(recipe instanceof IGTEmiRecipe gtRecipe)) {
            return;
        }

        var itemInputs = gtRecipe.sftcore$recipe().getInputContents(GTRecipeCapabilities.ITEM);
        var inputs = cir.getReturnValue();
        var filtered = new ArrayList<List<GenericStack>>(inputs.size());
        for (int i = 0; i < inputs.size(); i++) {
            if (i >= itemInputs.size() || itemInputs.get(i).chance() != 0) {
                filtered.add(inputs.get(i));
            }
        }
        cir.setReturnValue(filtered);
    }

    @Inject(method = "craft", at = @At("TAIL"), remap = false)
    private void handleEmiRecipe(
        EmiRecipe recipe,
        EmiCraftContext<T> context,
        CallbackInfoReturnable<Boolean> cir
    ) {
        SFTEmiPatternHandler.handleEmiRecipe(recipe, context);
    }
}
