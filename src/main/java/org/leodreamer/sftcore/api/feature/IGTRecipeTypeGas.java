package org.leodreamer.sftcore.api.feature;

import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.gui.GTRecipeTypeUILayout;

import java.util.function.UnaryOperator;

public interface IGTRecipeTypeGas {

    GTRecipeType sftcore$self();

    IGTRecipeTypeGas sftcore$setMaxGasIOSize(int maxGasInputs, int maxGasOutputs);

    IGTRecipeTypeGas sftcore$setMaxIOSize(
        int maxItemInputs,
        int maxItemOutputs,
        int maxFluidInputs,
        int maxFluidOutputs,
        int maxGasInputs,
        int maxGasOutputs
    );

    /**
     * For gas recipes, use this instead of {@link GTRecipeType#UI(UnaryOperator)} to add gas slots to the recipe UI.
     */
    IGTRecipeTypeGas sftcore$gasUI(UnaryOperator<GTRecipeTypeUILayout.Builder> builder);
}
