package org.leodreamer.sftcore.api.recipe.remove;

import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

public record InputFilter(ItemLike item) implements RecipeFilter {

    @Override
    public boolean test(ResourceLocation id, Recipe<?> recipe) {
        if (recipe instanceof GTRecipe gtRecipe) {
            for (var input : RecipeHelper.getInputItems(gtRecipe)) {
                if (input.is(item.asItem()))
                    return true;
            }
            return false;
        }
        for (var ingredient : recipe.getIngredients()) {
            for (var stack : ingredient.getItems()) {
                if (stack.is(item.asItem()))
                    return true;
            }
        }
        return false;
    }
}
