package org.leodreamer.sftcore.mixin.gregtech.xei;

import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.integration.emi.recipe.GTEmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.jemi.JemiUtil;
import mekanism.api.chemical.gas.GasStack;
import mekanism.client.jei.MekanismJEI;
import org.leodreamer.sftcore.integration.emi.IGTEmiRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = GTEmiRecipe.class, remap = false)
public class GTEmiRecipeMixin implements IGTEmiRecipe {
    @Shadow
    @Final
    GTRecipe recipe;

    @Override
    public GTRecipe sftcore$recipe() {
        return recipe;
    }

    @Redirect(
        method = "addWidgets",
        at = @At(
            value = "INVOKE",
            target = "Ldev/emi/emi/api/stack/EmiIngredient;of(Ljava/util/List;)Ldev/emi/emi/api/stack/EmiIngredient;"
        )
    )
    private EmiIngredient sftcore$wrapGasIngredientsWithJemi(List<?> ingredients) {
        boolean hasGas = false;
        var converted = new ArrayList<EmiIngredient>();

        for (var ingredient : ingredients) {
            if (ingredient instanceof GasStack gasStack) {
                hasGas = true;

                var stack = sftcore$toJemiGasStack(gasStack);
                if (!stack.isEmpty()) {
                    converted.add(stack);
                }
            } else if (ingredient instanceof EmiIngredient emiIngredient) {
                converted.add(emiIngredient);
            }
        }

        if (!hasGas) {
            // noinspection unchecked
            return EmiIngredient.of((List <? extends EmiIngredient>) ingredients);
        }

        if (converted.isEmpty()) {
            return EmiStack.EMPTY;
        }

        return EmiIngredient.of(converted);
    }

    @Unique
    private static EmiStack sftcore$toJemiGasStack(GasStack gasStack) {
        if (gasStack == null || gasStack.isEmpty()) {
            return EmiStack.EMPTY;
        }

        try {
            var copy = gasStack.copy();

            var stack = JemiUtil.getStack(MekanismJEI.TYPE_GAS, copy);

            if (!stack.isEmpty()) {
                stack.setAmount(copy.getAmount());
            }

            return stack;
        } catch (Throwable ignored) {
            return EmiStack.EMPTY;
        }
    }
}
