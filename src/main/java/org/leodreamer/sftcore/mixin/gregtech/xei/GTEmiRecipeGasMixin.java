package org.leodreamer.sftcore.mixin.gregtech.xei;

import org.leodreamer.sftcore.api.recipe.capability.GasRecipeCapability;
import org.leodreamer.sftcore.integration.emi.recipe.IGTEmiRecipe;

import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.integration.recipeviewer.emi.recipe.GTEmiRecipe;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.jemi.JemiUtil;
import mekanism.client.jei.MekanismJEI;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = GTEmiRecipe.class, remap = false)
public abstract class GTEmiRecipeGasMixin implements IGTEmiRecipe {

    @Final
    @Shadow
    GTRecipe recipe;

    @Override
    public GTRecipe sftcore$recipe() {
        return recipe;
    }

    @Inject(method = "getInputs", at = @At("RETURN"))
    private void sftcore$appendGasInputs(CallbackInfoReturnable<List<EmiIngredient>> cir) {
        var inputs = cir.getReturnValue();
        recipe.getInputContents(GasRecipeCapability.CAP).stream()
            .map(content -> sftcore$createGasStack(recipe, content))
            .filter(stack -> !stack.isEmpty())
            .forEach(inputs::add);
    }

    @Inject(method = "getOutputs", at = @At("RETURN"))
    private void sftcore$appendGasOutputs(CallbackInfoReturnable<List<EmiStack>> cir) {
        var outputs = cir.getReturnValue();
        recipe.getOutputContents(GasRecipeCapability.CAP).stream()
            .map(content -> sftcore$createGasStack(recipe, content))
            .filter(stack -> !stack.isEmpty())
            .forEach(outputs::add);
    }

    @Unique
    private static EmiStack sftcore$createGasStack(GTRecipe recipe, Content content) {
        var gas = GasRecipeCapability.CAP.of(content.content());
        if (gas == null || gas.isEmpty()) {
            return EmiStack.EMPTY;
        }

        var stack = JemiUtil.getStack(MekanismJEI.TYPE_GAS, gas.copy());
        stack.setAmount(gas.getAmount());

        float chance = (float) content.chance() / content.maxChance();
        stack.setChance(chance);
        return stack;
    }
}
