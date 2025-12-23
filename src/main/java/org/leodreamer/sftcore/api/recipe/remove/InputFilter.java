package org.leodreamer.sftcore.api.recipe.remove;

import org.leodreamer.sftcore.util.ReflectUtils;

import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

public record InputFilter(ItemLike item) implements RecipeFilter {

    @Override
    public boolean test(ResourceLocation id, Recipe<?> recipe) {
        var i = item.asItem();
        if (recipe instanceof GTRecipe gtRecipe) {
            for (var input : RecipeHelper.getInputItems(gtRecipe)) {
                if (input.is(i)) return true;
            }
            return false;
        }
        for (var ingredient : recipe.getIngredients()) {
            var values = ReflectUtils.getFieldValue(ingredient, "values", Ingredient.Value[].class);
            for (var value : values) {
                if (value instanceof Ingredient.ItemValue itemValue) {
                    var stack = ReflectUtils.getFieldValue(itemValue, "item", ItemStack.class);
                    if (stack.is(i)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
