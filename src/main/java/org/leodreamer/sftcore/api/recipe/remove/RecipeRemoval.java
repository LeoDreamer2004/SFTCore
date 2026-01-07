package org.leodreamer.sftcore.api.recipe.remove;

import org.leodreamer.sftcore.common.data.recipe.SFTRecipeRemovals;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeRemoval {

    public static final RecipeRemoval INSTANCE = build();

    private final List<RecipeFilter> filters = new ArrayList<>();

    private static RecipeRemoval build() {
        var removal = new RecipeRemoval();
        SFTRecipeRemovals.init(removal.filters::add);
        return removal;
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
