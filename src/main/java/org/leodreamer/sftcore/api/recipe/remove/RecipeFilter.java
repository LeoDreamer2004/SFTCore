package org.leodreamer.sftcore.api.recipe.remove;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

import org.jetbrains.annotations.NotNull;

import java.util.function.BiPredicate;

@FunctionalInterface
public interface RecipeFilter extends BiPredicate<ResourceLocation, Recipe<?>> {

    default RecipeFilter and(@NotNull RecipeFilter other) {
        return (id, recipe) -> this.test(id, recipe) && other.test(id, recipe);
    }

    default @NotNull RecipeFilter negate() {
        return (t, u) -> !this.test(t, u);
    }

    default RecipeFilter or(@NotNull RecipeFilter other) {
        return (t, u) -> this.test(t, u) || other.test(t, u);
    }
}
