package org.leodreamer.sftcore.api.feature;

import com.gregtechceu.gtceu.api.recipe.GTRecipeType;

public interface IGTRecipeTypeGas {

    GTRecipeType sftcore$setMaxGasIOSize(int maxGasInputs, int maxGasOutputs);

    GTRecipeType sftcore$setMaxIOSize(
        int maxItemInputs,
        int maxItemOutputs,
        int maxFluidInputs,
        int maxFluidOutputs,
        int maxGasInputs,
        int maxGasOutputs
    );
}
