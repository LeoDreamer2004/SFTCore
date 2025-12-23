package org.leodreamer.sftcore.api.recipe.remove;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

public interface RecipeFilter {

    boolean test(ResourceLocation id, Recipe<?> recipe);

    default RecipeFilter and(RecipeFilter other) {
        return new AndFilter(this, other);
    }
}
