package org.leodreamer.sftcore.api.recipe.capability;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.api.recipe.content.SerializerGasStack;

import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;

import mekanism.api.chemical.gas.GasStack;

@DataGenScanned
public class GasRecipeCapability extends RecipeCapability<GasStack> {

    @RegisterLanguage("gas")
    private static final String NAME = "recipe.capability.gas.name";

    public static final GasRecipeCapability CAP = new GasRecipeCapability();

    protected GasRecipeCapability() {
        super(SFTCore.id("gas"), 0xFF00D7C8, true, 20, SerializerGasStack.INSTANCE);
    }

    @Override
    public GasStack copyInner(GasStack content) {
        return content.copy();
    }

    @Override
    public GasStack copyWithModifier(GasStack content, ContentModifier modifier) {
        if (content.isEmpty()) {
            return GasStack.EMPTY;
        }

        long amount = modifier.apply(content.getAmount());
        return amount <= 0 ? GasStack.EMPTY : new GasStack(content, amount);
    }

    @Override
    public boolean isRecipeSearchFilter() {
        return true;
    }

    @Override
    public boolean shouldBypassDistinct() {
        return false;
    }
}
