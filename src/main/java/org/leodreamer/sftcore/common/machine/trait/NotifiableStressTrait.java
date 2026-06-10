package org.leodreamer.sftcore.common.machine.trait;

import org.leodreamer.sftcore.api.recipe.capability.StressRecipeCapability;
import org.leodreamer.sftcore.common.machine.multiblock.part.KineticInputPartMachine;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.machine.trait.ICapabilityTrait;
import com.gregtechceu.gtceu.api.machine.trait.MachineTraitType;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableRecipeHandlerTrait;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;

import net.minecraft.MethodsReturnNonnullByDefault;

import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class NotifiableStressTrait extends NotifiableRecipeHandlerTrait<Float> implements ICapabilityTrait {

    public static final MachineTraitType<NotifiableStressTrait> TYPE = new MachineTraitType<>(
        NotifiableStressTrait.class
    );

    private final KineticInputPartMachine machine;
    private final IO handlerIO;
    private final IO capabilityIO;

    public NotifiableStressTrait(KineticInputPartMachine machine, IO handlerIO, IO capabilityIO) {
        this.machine = machine;
        this.handlerIO = handlerIO;
        this.capabilityIO = capabilityIO;
    }

    @Override
    public IO getHandlerIO() {
        return handlerIO;
    }

    @Override
    public IO getCapabilityIO() {
        return capabilityIO;
    }

    @Override
    public @Nullable List<Float> handleRecipeInner(
        IO io,
        GTRecipe recipe,
        List<Float> left,
        boolean simulate
    ) {
        if (left.isEmpty()) {
            return null;
        }

        if (io != IO.IN) {
            return left;
        }

        float required = left.stream().reduce(0.0F, Float::sum);
        float available = machine.getAvailableStress();

        if (available >= required) {
            return null;
        }

        return Collections.singletonList(required - available);
    }

    @Override
    public List<Object> getContents() {
        return List.of(machine.getAvailableStress());
    }

    @Override
    public double getTotalContentAmount() {
        return machine.getAvailableStress();
    }

    @Override
    public RecipeCapability<Float> getCapability() {
        return StressRecipeCapability.CAP;
    }

    @Override
    public boolean shouldSearchContent() {
        return false;
    }

    @Override
    public MachineTraitType<?> getTraitType() {
        return TYPE;
    }

    public void notifyStressChanged() {
        notifyListeners();
    }
}
