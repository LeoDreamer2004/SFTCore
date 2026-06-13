package org.leodreamer.sftcore.integration.emi.gui;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.recipe.EmiRecipe;

public final class MechanicalPatternEmiViewer {

    private MechanicalPatternEmiViewer() {}

    @OnlyIn(Dist.CLIENT)
    public static void openRecipe(ResourceLocation id) {
        var manager = EmiApi.getRecipeManager();
        var recipe = manager.getRecipe(id);
        if (recipe == null) {
            recipe = manager.getRecipes().stream()
                .filter(emiRecipe -> matchesRecipe(id, emiRecipe))
                .findFirst()
                .orElse(null);
        }
        if (recipe != null) {
            EmiApi.displayRecipe(recipe);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static boolean matchesRecipe(ResourceLocation id, EmiRecipe recipe) {
        if (id.equals(recipe.getId())) {
            return true;
        }
        var backingRecipe = recipe.getBackingRecipe();
        return backingRecipe != null && id.equals(backingRecipe.getId());
    }
}
