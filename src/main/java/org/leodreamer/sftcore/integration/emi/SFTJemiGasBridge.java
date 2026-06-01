package org.leodreamer.sftcore.integration.emi;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.jemi.JemiUtil;
import mekanism.api.chemical.gas.GasStack;
import mekanism.client.jei.MekanismJEI;

import java.util.ArrayList;
import java.util.List;

public class SFTJemiGasBridge {

    /**
     * Replace the raw {@link GasStack} to {@link EmiIngredient} for xei capability
     */
    public static List<Object> wrapGasIngredients(List<Object> raw) {
        boolean hasGas = false;
        var converted = new ArrayList<>(raw.size());

        for (var ingredient : raw) {
            if (ingredient instanceof GasStack gasStack) {
                hasGas = true;

                var stack = toJemiGasStack(gasStack);
                if (!stack.isEmpty()) {
                    converted.add(stack);
                }
            } else if (ingredient instanceof EmiIngredient emiIngredient) {
                converted.add(emiIngredient);
            } else {
                // should not reach here
                return raw;
            }
        }

        return hasGas ? converted : raw;
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
