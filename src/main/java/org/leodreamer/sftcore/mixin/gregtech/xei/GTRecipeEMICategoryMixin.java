package org.leodreamer.sftcore.mixin.gregtech.xei;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.integration.emi.SFTFastGTEmiRecipe;

import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.category.GTRecipeCategory;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.integration.emi.recipe.GTRecipeEMICategory;

import dev.emi.emi.api.EmiRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;

@Mixin(value = GTRecipeEMICategory.class, remap = false)
public abstract class GTRecipeEMICategoryMixin {

    /**
     * @author LeoDreamer
     * @reason Replaces GTM's eager `new GTEmiRecipe(...)` path for optimization.
     */
    @Overwrite
    public static void registerDisplays(EmiRegistry registry) {
        long allStart = System.nanoTime();

        var subCategories = new ArrayList<GTRecipeCategory>();
        int mainRecipes = 0;
        int subRecipes = 0;

        for (var category : GTRegistries.RECIPE_CATEGORIES) {
            if (!category.shouldRegisterDisplays()) {
                continue;
            }

            var type = category.getRecipeType();

            if (category == type.getCategory()) {
                type.buildRepresentativeRecipes();

                int added = sftcore$registerCategoryRecipes(registry, type, category);
                mainRecipes += added;
            } else {
                subCategories.add(category);
            }
        }

        for (var subCategory : subCategories) {
            if (!subCategory.shouldRegisterDisplays()) {
                continue;
            }
            var type = subCategory.getRecipeType();
            int added = sftcore$registerCategoryRecipes(registry, type, subCategory);
            subRecipes += added;
        }

        SFTCore.LOGGER.info(
            "[SFTCore/EMI] Fast GTM EMI displays registered: {} main + {} sub recipes in {} ms",
            mainRecipes,
            subRecipes,
            sftcore$ms(allStart)
        );
    }

    @Unique
    private static int sftcore$registerCategoryRecipes(
        EmiRegistry registry,
        GTRecipeType type,
        GTRecipeCategory category
    ) {
        var emiCategory = GTRecipeEMICategory.CATEGORIES.apply(category);

        int count = 0;
        for (var recipe : type.getRecipesInCategory(category)) {
            registry.addRecipe(new SFTFastGTEmiRecipe(recipe, emiCategory));
            count++;
        }

        return count;
    }

    @Unique
    private static long sftcore$ms(long startNano) {
        return (System.nanoTime() - startNano) / 1_000_000L;
    }
}
