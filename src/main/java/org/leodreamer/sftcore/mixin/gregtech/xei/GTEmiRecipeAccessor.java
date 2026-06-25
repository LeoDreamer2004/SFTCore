package org.leodreamer.sftcore.mixin.gregtech.xei;

import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.integration.recipeviewer.emi.recipe.GTEmiRecipe;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = GTEmiRecipe.class, remap = false)
public interface GTEmiRecipeAccessor {

    @Accessor("recipe")
    GTRecipe getRecipe();
}
