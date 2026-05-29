package org.leodreamer.sftcore.common.data;

import com.gregtechceu.gtceu.api.recipe.lookup.ingredient.MapIngredientTypeManager;
import mekanism.api.chemical.gas.GasStack;
import org.leodreamer.sftcore.api.recipe.content.MapGasStackIngredient;

import java.util.List;

public final class SFTRecipeLookupIngredients {
    private SFTRecipeLookupIngredients() {
    }

    public static void init() {
        MapIngredientTypeManager.registerMapIngredient(
            GasStack.class,
            stack -> List.of(new MapGasStackIngredient(stack.getType()))
        );
    }
}
