package org.leodreamer.sftcore.common.item.cepattern;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class CEPatternLogic {

    private CEPatternLogic() {}

    public static List<CERecipeStep> resolveSteps(Level level, List<ResourceLocation> recipeIds) {
        if (recipeIds.isEmpty()) {
            return List.of();
        }
        var recipes = new ArrayList<CERecipeStep>(recipeIds.size());
        for (var id : recipeIds) {
            var recipe = getCERecipe(level, id);
            if (recipe.isEmpty()) {
                return List.of();
            }
            recipes.add(recipe.get());
        }
        return recipes;
    }

    public static boolean canEncode(Level level, ResourceLocation id) {
        return getCERecipe(level, id).isPresent();
    }

    public static Optional<CERecipeStep> getCERecipe(Level level, ResourceLocation id) {
        var recipe = level.getRecipeManager().byKey(id);
        return recipe.flatMap(CERecipeStep::fromRecipe);
    }
}
