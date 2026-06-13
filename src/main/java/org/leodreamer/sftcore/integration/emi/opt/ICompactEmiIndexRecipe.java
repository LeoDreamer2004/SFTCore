package org.leodreamer.sftcore.integration.emi.opt;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;

import java.util.List;

/**
 * Create an index for recipe to accelerate EMI baking
 */
public interface ICompactEmiIndexRecipe {

    List<EmiIngredient> getIndexInputs();

    List<EmiIngredient> getIndexCatalysts();

    List<EmiStack> getIndexOutputs();
}
