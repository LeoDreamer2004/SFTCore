package org.leodreamer.sftcore.api.recipe.remove;

import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

public record OutputFilter(ItemLike item) implements RecipeFilter {

    private static final RegistryAccess access = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);

    @Override
    public boolean test(ResourceLocation id, Recipe<?> recipe) {
        if (recipe instanceof GTRecipe gtRecipe) {
            for (var output : RecipeHelper.getOutputItems(gtRecipe)) {
                if (output.is(item.asItem())) return true;
            }
            return false;
        }
        return recipe.getResultItem(access).is(item.asItem());
    }
}
