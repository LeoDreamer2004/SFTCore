package org.leodreamer.sftcore.mixin.emi;

import org.leodreamer.sftcore.integration.emi.opt.ICompactEmiIndexRecipe;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(targets = "dev.emi.emi.registry.EmiRecipes$Manager", remap = false)
public abstract class EmiRecipesManagerMixin {

    @Redirect(
        method = "<init>(Ljava/util/List;Ljava/util/Map;Ljava/util/List;Z)V",
        at = @At(
            value = "INVOKE",
            target = "Ldev/emi/emi/api/recipe/EmiRecipe;getInputs()Ljava/util/List;"
        )
    )
    private List<EmiIngredient> sftcore$compactInputs(EmiRecipe recipe) {
        if (recipe instanceof ICompactEmiIndexRecipe compact) {
            return compact.getIndexInputs();
        }
        return recipe.getInputs();
    }

    @Redirect(
        method = "<init>(Ljava/util/List;Ljava/util/Map;Ljava/util/List;Z)V",
        at = @At(
            value = "INVOKE",
            target = "Ldev/emi/emi/api/recipe/EmiRecipe;getCatalysts()Ljava/util/List;"
        )
    )
    private List<EmiIngredient> sftcore$compactCatalysts(EmiRecipe recipe) {
        if (recipe instanceof ICompactEmiIndexRecipe compact) {
            return compact.getIndexCatalysts();
        }
        return recipe.getCatalysts();
    }

    @Redirect(
        method = "<init>(Ljava/util/List;Ljava/util/Map;Ljava/util/List;Z)V",
        at = @At(
            value = "INVOKE",
            target = "Ldev/emi/emi/api/recipe/EmiRecipe;getOutputs()Ljava/util/List;"
        )
    )
    private List<EmiStack> sftcore$compactOutputs(EmiRecipe recipe) {
        if (recipe instanceof ICompactEmiIndexRecipe compact) {
            return compact.getIndexOutputs();
        }
        return recipe.getOutputs();
    }
}
