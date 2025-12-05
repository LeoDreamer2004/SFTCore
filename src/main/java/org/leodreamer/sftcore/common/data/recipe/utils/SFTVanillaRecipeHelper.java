package org.leodreamer.sftcore.common.data.recipe.utils;

import com.gregtechceu.gtceu.api.data.chemical.material.stack.MaterialEntry;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.leodreamer.sftcore.SFTCore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

public class SFTVanillaRecipeHelper {
    public static class ShapedRecipeBuilder {
        private final ResourceLocation id;
        private boolean setMaterialInfoData = true;
        private final ArrayList<Object> recipe = new ArrayList<>();
        private ItemStack output;

        public ShapedRecipeBuilder(ResourceLocation id) {
            this.id = id;
        }

        public ShapedRecipeBuilder setMaterialInfoData(boolean setMaterialInfoData) {
            this.setMaterialInfoData = setMaterialInfoData;
            return this;
        }

        public ShapedRecipeBuilder pattern(String... patterns) {
            if (!recipe.isEmpty()) {
                throw new IllegalArgumentException("Please define the pattern before adding any arguments to the recipe.");
            }
            recipe.addAll(Arrays.asList(patterns));
            return this;
        }

        public ShapedRecipeBuilder arg(char sign, ItemLike item) {
            return arg(sign, (Object) item);
        }

        public ShapedRecipeBuilder arg(char sign, MaterialEntry entry) {
            return arg(sign, (Object) entry);
        }

        public ShapedRecipeBuilder arg(char sign, TagKey<Item> tag) {
            return arg(sign, (Object) tag);
        }

        public ShapedRecipeBuilder arg(char sign, MachineDefinition machine) {
            return arg(sign, machine.asStack());
        }

        public ShapedRecipeBuilder arg(char sign, ItemStack stack) {
            return arg(sign, (Object) stack);
        }

        private ShapedRecipeBuilder arg(char sign, Object obj) {
            recipe.add(sign);
            recipe.add(obj);
            return this;
        }

        public ShapedRecipeBuilder output(ItemStack output) {
            this.output = output;
            return this;
        }

        public ShapedRecipeBuilder output(ItemLike item) {
            return output(new ItemStack(item));
        }

        public ShapedRecipeBuilder output(ItemLike item, int amount) {
            return output(new ItemStack(item, amount));
        }

        public void save(Consumer<FinishedRecipe> provider) {
            if (id == null) {
                SFTCore.LOGGER.warn("Recipe ID is not set for {}", output);
                return;
            }
            VanillaRecipeHelper.addShapedRecipe(provider, setMaterialInfoData, id, output, recipe.toArray());
        }
    }

    public static ShapedRecipeBuilder addShapedRecipe(String id) {
        return addShapedRecipe(SFTCore.id(id));
    }

    public static ShapedRecipeBuilder addShapedRecipe(ResourceLocation id) {
        return new ShapedRecipeBuilder(id);
    }
}
