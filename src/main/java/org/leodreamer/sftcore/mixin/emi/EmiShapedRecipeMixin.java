package org.leodreamer.sftcore.mixin.emi;

import net.minecraft.world.item.crafting.CraftingRecipe;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.recipe.EmiShapedRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = EmiShapedRecipe.class, remap = false)
public abstract class EmiShapedRecipeMixin {

    // easy version for remainders setting
    @Inject(method = "setRemainders", at = @At("HEAD"), cancellable = true)
    private static void sftcore$setRemainders(
        List<EmiIngredient> input,
        CraftingRecipe recipe,
        CallbackInfo ci
    ) {
        for (var ingredient : input) {
            for (var stack : ingredient.getEmiStacks()) {
                var itemStack = stack.getItemStack();
                if (itemStack.hasCraftingRemainingItem()) {
                    stack.setRemainder(EmiStack.of(itemStack.getCraftingRemainingItem()));
                }
            }
        }
        ci.cancel();
    }
}
