package org.leodreamer.sftcore.api.recipe.remove;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeRemoval {
    public static final RecipeRemoval INSTANCE = new RecipeRemoval();
    private final List<RecipeFilter> filters = new ArrayList<>();

    public void add(RecipeFilter filter) {
        filters.add(filter);
    }

    public boolean remove(ResourceLocation id, Recipe<?> recipe) {
        for (var filter : filters) {
            if (filter.test(id, recipe)) {
                return true;
            }
        }
        return false;
    }
}
