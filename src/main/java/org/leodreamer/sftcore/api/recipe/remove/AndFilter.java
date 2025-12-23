package org.leodreamer.sftcore.api.recipe.remove;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

public record AndFilter(RecipeFilter filter1, RecipeFilter filter2) implements RecipeFilter {

    @Override
    public boolean test(ResourceLocation id, Recipe<?> recipe) {
        return filter1.test(id, recipe) && filter2.test(id, recipe);
    }
}
