package org.leodreamer.sftcore.api.recipe.remove;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

public record IDFilter(ResourceLocation id) implements RecipeFilter {

    @Override
    public boolean test(ResourceLocation id, Recipe<?> recipe) {
        return this.id == id;
    }
}
