package org.leodreamer.sftcore.mixin.emi;

import com.gregtechceu.gtceu.core.mixins.IngredientAccessor;
import com.gregtechceu.gtceu.core.mixins.TagValueAccessor;

import net.minecraft.world.item.crafting.Ingredient;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.TagEmiIngredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EmiIngredient.class, remap = false)
public interface EmiIngredientMixin {

    @Inject(
        method = "of(Lnet/minecraft/world/item/crafting/Ingredient;)Ldev/emi/emi/api/stack/EmiIngredient;",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void sftcore$ofTag(
        Ingredient ingredient,
        CallbackInfoReturnable<EmiIngredient> cir
    ) {
        var result = sftcore$ofTag(ingredient, 1);
        if (result != null) {
            cir.setReturnValue(result);
        }
    }

    @Inject(
        method = "of(Lnet/minecraft/world/item/crafting/Ingredient;J)Ldev/emi/emi/api/stack/EmiIngredient;",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void sftcore$ofTag(
        Ingredient ingredient,
        long amount,
        CallbackInfoReturnable<EmiIngredient> cir
    ) {
        var result = sftcore$ofTag(ingredient, amount);
        if (result != null) {
            cir.setReturnValue(result);
        }
    }

    @Unique
    private static EmiIngredient sftcore$ofTag(Ingredient ingredient, long amount) {
        if (ingredient == null || !ingredient.isVanilla()) {
            return null;
        }
        var values = ((IngredientAccessor) ingredient).getValues();
        if (values.length != 1 || !(values[0] instanceof Ingredient.TagValue tagValue)) {
            return null;
        }

        var tag = ((TagValueAccessor) tagValue).getTag();
        var result = new TagEmiIngredient(tag, amount);
        if (result.getEmiStacks().isEmpty()) {
            return EmiStack.EMPTY;
        }
        if (result.getEmiStacks().size() == 1) {
            return result.getEmiStacks().get(0).copy().setAmount(amount);
        }
        return result;
    }
}
