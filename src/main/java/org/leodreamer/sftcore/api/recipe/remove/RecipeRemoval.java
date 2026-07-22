package org.leodreamer.sftcore.api.recipe.remove;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.common.data.recipe.SFTRecipeRemovals;
import org.leodreamer.sftcore.util.ReflectUtils;

import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class RecipeRemoval {

    private static final RegistryAccess REGISTRY_ACCESS = RegistryAccess
        .fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
    public static final RecipeRemoval INSTANCE = new RecipeRemoval();

    private final Set<Item> outputs = new HashSet<>();
    private final Set<Item> inputs = new HashSet<>();
    private final Map<String, Set<Item>> modOutputs = new HashMap<>();

    private RecipeRemoval() {
        SFTRecipeRemovals.init(this);
    }

    public boolean test(ResourceLocation id, Recipe<?> recipe) {
        if (id.getNamespace().equals(SFTCore.MOD_ID)) {
            return false;
        }

        var scopedOutputs = modOutputs.get(id.getNamespace());

        // special test for GT
        if (recipe instanceof GTRecipe gtRecipe) {
            for (var stack : RecipeHelper.getOutputItems(gtRecipe)) {
                var item = stack.getItem();
                if (outputs.contains(item) || scopedOutputs != null && scopedOutputs.contains(item)) {
                    return true;
                }
            }
            for (var stack : RecipeHelper.getInputItems(gtRecipe)) {
                var item = stack.getItem();
                if (inputs.contains(item)) {
                    return true;
                }
            }
            return false;
        }

        var output = recipe.getResultItem(REGISTRY_ACCESS).getItem();
        if (outputs.contains(output) || scopedOutputs != null && scopedOutputs.contains(output)) {
            return true;
        }
        if (inputs.isEmpty()) {
            return false;
        }
        for (var ingredient : recipe.getIngredients()) {
            var values = ReflectUtils.getFieldValue(ingredient, "values", Ingredient.Value[].class);
            for (var value : values) {
                if (value instanceof Ingredient.ItemValue itemValue) {
                    var item = ReflectUtils.getFieldValue(itemValue, "item", ItemStack.class).getItem();
                    if (inputs.contains(item)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void output(ItemLike item) {
        outputs.add(item.asItem());
    }

    public void output(String mod, ItemLike item) {
        modOutputs.computeIfAbsent(mod, ignored -> new HashSet<>()).add(item.asItem());
    }

    public void input(ItemLike item) {
        inputs.add(item.asItem());
    }
}
