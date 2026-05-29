package org.leodreamer.sftcore.mixin.gregtech.recipe;

import org.leodreamer.sftcore.api.feature.IGTRecipeTypeGas;
import org.leodreamer.sftcore.api.recipe.capability.GasRecipeCapability;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = GTRecipeType.class, remap = false)
public abstract class GTRecipeTypeMixin implements IGTRecipeTypeGas {

    @Shadow
    public abstract GTRecipeType setMaxIOSize(
        int maxInputs,
        int maxOutputs,
        int maxFluidInputs,
        int maxFluidOutputs
    );

    @Shadow
    public abstract GTRecipeType setMaxSize(IO io, RecipeCapability<?> cap, int max);

    @Override
    public GTRecipeType sftcore$setMaxGasIOSize(int maxGasInputs, int maxGasOutputs) {
        setMaxSize(IO.IN, GasRecipeCapability.CAP, maxGasInputs);
        setMaxSize(IO.OUT, GasRecipeCapability.CAP, maxGasOutputs);
        return (GTRecipeType) (Object) this;
    }

    @Override
    public GTRecipeType sftcore$setMaxIOSize(
        int maxItemInputs,
        int maxItemOutputs,
        int maxFluidInputs,
        int maxFluidOutputs,
        int maxGasInputs,
        int maxGasOutputs
    ) {
        setMaxIOSize(maxItemInputs, maxItemOutputs, maxFluidInputs, maxFluidOutputs);
        return sftcore$setMaxGasIOSize(maxGasInputs, maxGasOutputs);
    }
}
