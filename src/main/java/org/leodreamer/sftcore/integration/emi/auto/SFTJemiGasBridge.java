package org.leodreamer.sftcore.integration.emi.auto;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.jemi.JemiUtil;
import mekanism.api.chemical.gas.GasStack;
import mekanism.client.jei.MekanismJEI;

public class SFTJemiGasBridge {

    /**
     * Replace the raw {@link GasStack} to {@link EmiIngredient} for xei capability
     */
    public static EmiIngredient wrapGasIngredient(Object ingredient) {
        if (ingredient instanceof GasStack gasStack) {
            return toJemiGasStack(gasStack);
        } else {
            return (EmiIngredient) ingredient;
        }
    }

    private static EmiStack toJemiGasStack(GasStack gasStack) {
        if (gasStack == null || gasStack.isEmpty()) {
            return EmiStack.EMPTY;
        }

        var copy = gasStack.copy();

        var stack = JemiUtil.getStack(MekanismJEI.TYPE_GAS, copy);
        if (!stack.isEmpty()) {
            stack.setAmount(copy.getAmount());
        }

        return stack;
    }
}
