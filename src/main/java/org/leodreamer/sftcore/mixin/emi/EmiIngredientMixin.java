package org.leodreamer.sftcore.mixin.emi;

import com.gregtechceu.gtceu.core.mixins.IngredientAccessor;
import com.gregtechceu.gtceu.core.mixins.TagValueAccessor;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.TagEmiIngredient;
import dev.emi.emi.registry.EmiTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Arrays;

@Mixin(value = EmiIngredient.class, remap = false)
public interface EmiIngredientMixin {

    /**
     * @author leodreamer
     * @reason Preserve vanilla tags instead of eagerly expanding all entries.
     */
    @Overwrite
    static EmiIngredient of(Ingredient ingredient) {
        if (ingredient == null) {
            return EmiStack.EMPTY;
        }
        if (ingredient.isVanilla()) {
            var values = ((IngredientAccessor) ingredient).getValues();
            if (values.length == 1 && values[0] instanceof Ingredient.TagValue) {
                return of(ingredient, 1);
            }
        }
        if (ingredient.isEmpty()) {
            return EmiStack.EMPTY;
        }

        ItemStack[] stacks = ingredient.getItems();
        int amount = 1;
        if (stacks.length != 0) {
            amount = stacks[0].getCount();
            for (int i = 1; i < stacks.length; i++) {
                if (stacks[i].getCount() != amount) {
                    amount = 1;
                    break;
                }
            }
        }
        return of(ingredient, amount);
    }

    /**
     * @author leodreamer
     * @reason Preserve vanilla tags instead of eagerly expanding all entries.
     */
    @Overwrite
    static EmiIngredient of(Ingredient ingredient, long amount) {
        if (ingredient == null) {
            return EmiStack.EMPTY;
        }
        if (ingredient.isVanilla()) {
            var values = ((IngredientAccessor) ingredient).getValues();
            if (values.length == 1 && values[0] instanceof Ingredient.TagValue tagValue) {
                var tag = ((TagValueAccessor) tagValue).getTag();
                var result = new TagEmiIngredient(tag, amount);
                var stacks = result.getEmiStacks();
                if (stacks.isEmpty()) {
                    return EmiStack.EMPTY;
                }
                if (stacks.size() == 1) {
                    return stacks.get(0).copy().setAmount(amount);
                }
                return result;
            }
        }
        if (ingredient.isEmpty()) {
            return EmiStack.EMPTY;
        }
        return EmiTags.getIngredient(
            Item.class,
            Arrays.stream(ingredient.getItems()).map(EmiStack::of).toList(), amount
        );
    }
}
