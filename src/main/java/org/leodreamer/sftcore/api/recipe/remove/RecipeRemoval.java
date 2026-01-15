package org.leodreamer.sftcore.api.recipe.remove;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.common.data.recipe.SFTRecipeRemovals;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

public class RecipeRemoval implements RecipeFilter {

    public static final RecipeRemoval INSTANCE = build();

    private RecipeFilter filter = RecipeFilters.EMPTY;

    private static RecipeRemoval build() {
        var removal = new RecipeRemoval();
        SFTRecipeRemovals.init(
            (f) -> removal.filter = removal.filter.or(
                f.and(RecipeFilters.mod(SFTCore.MOD_ID).negate())
            )
        );
        return removal;
    }

    @Override
    public boolean test(ResourceLocation id, Recipe<?> recipe) {
        return filter.test(id, recipe);
    }
}
