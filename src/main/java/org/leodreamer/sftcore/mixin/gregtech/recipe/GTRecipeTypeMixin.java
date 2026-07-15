package org.leodreamer.sftcore.mixin.gregtech.recipe;

import org.leodreamer.sftcore.api.feature.IGTRecipeTypeGas;
import org.leodreamer.sftcore.api.gui.gas.GasRecipeUI;
import org.leodreamer.sftcore.api.recipe.capability.GasRecipeCapability;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.gui.GTRecipeTypeUILayout;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.UnaryOperator;

@Mixin(value = GTRecipeType.class, remap = false)
public abstract class GTRecipeTypeMixin implements IGTRecipeTypeGas {

    @Shadow
    public abstract GTRecipeType setMaxIOSize(
        int maxItemInputs,
        int maxItemOutputs,
        int maxFluidInputs,
        int maxFluidOutputs
    );

    @Shadow
    public abstract GTRecipeType setMaxSize(IO io, RecipeCapability<?> cap, int max);

    @Shadow
    public abstract GTRecipeType UI(UnaryOperator<GTRecipeTypeUILayout.Builder> builder);

    @Override
    public GTRecipeType sftcore$self() {
        return (GTRecipeType) (Object) this;
    }

    @Override
    public IGTRecipeTypeGas sftcore$setMaxGasIOSize(int maxGasInputs, int maxGasOutputs) {
        setMaxSize(IO.IN, GasRecipeCapability.CAP, maxGasInputs);
        setMaxSize(IO.OUT, GasRecipeCapability.CAP, maxGasOutputs);
        return this;
    }

    @Override
    public IGTRecipeTypeGas sftcore$setMaxIOSize(
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

    @Override
    public IGTRecipeTypeGas sftcore$gasUI(UnaryOperator<GTRecipeTypeUILayout.Builder> builder) {
        UI(uiBuilder -> builder.apply(GasRecipeUI.apply(uiBuilder)));
        return this;
    }
}
