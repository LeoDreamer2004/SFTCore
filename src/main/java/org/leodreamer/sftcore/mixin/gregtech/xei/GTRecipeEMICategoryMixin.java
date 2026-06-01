package org.leodreamer.sftcore.mixin.gregtech.xei;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.integration.emi.SFTFastGTEmiRecipe;
import org.leodreamer.sftcore.util.RLUtils;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.category.GTRecipeCategory;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.integration.emi.recipe.GTRecipeEMICategory;

import net.minecraft.world.item.ItemStack;

import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.EmiStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

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

    /**
     * @author LeoDreamer
     * @reason Check same semantics as GTM, but dedupes workstation registration before EMI bake.
     */
    @Overwrite
    public static void registerWorkStations(EmiRegistry registry) {
        long start = System.nanoTime();

        var seen = new HashSet<String>();
        int added = 0;
        int skipped = 0;

        for (
            var machine : GTRegistries.MACHINES.values()
                .stream()
                .sorted(sftcore$sortDefinition())
                .toList()
        ) {

            var stack = machine.asStack();
            var emiStack = EmiStack.of(stack);
            String stackKey = sftcore$stackKey(stack);

            for (var type : machine.getRecipeTypes()) {
                for (var category : type.getCategories()) {
                    if (!category.isXEIVisible() && !GTCEu.isDev()) {
                        continue;
                    }

                    var emiCategory = sftcore$machineCategory(category);
                    String key = emiCategory.getId() + "|" + stackKey;

                    if (seen.add(key)) {
                        registry.addWorkstation(emiCategory, emiStack);
                        added++;
                    } else {
                        skipped++;
                    }
                }
            }
        }

        SFTCore.LOGGER.info(
            "[SFTCore/EMI] GTM workstations registered: {} added, {} duplicate skipped in {} ms",
            added,
            skipped,
            sftcore$ms(start)
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
    private static Comparator<MachineDefinition> sftcore$sortDefinition() {
        return (a, b) -> {
            boolean isAMulti = a instanceof MultiblockMachineDefinition;
            boolean isBMulti = b instanceof MultiblockMachineDefinition;

            if (isAMulti && !isBMulti) {
                return 1;
            } else if (!isAMulti && isBMulti) {
                return -1;
            }

            return a.getTier() - b.getTier();
        };
    }

    @Unique
    private static EmiRecipeCategory sftcore$machineCategory(GTRecipeCategory category) {
        if (category == GTRecipeTypes.FURNACE_RECIPES.getCategory()) {
            return VanillaEmiRecipeCategories.SMELTING;
        }

        return GTRecipeEMICategory.CATEGORIES.apply(category);
    }

    @Unique
    private static String sftcore$stackKey(ItemStack stack) {
        String item = RLUtils.getItemRL(stack.getItem()).toString();
        var tag = stack.getTag();
        if (tag == null) return item;
        return item + "#" + tag;
    }

    @Unique
    private static long sftcore$ms(long startNano) {
        return (System.nanoTime() - startNano) / 1_000_000L;
    }
}
