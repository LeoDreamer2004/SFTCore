package org.leodreamer.sftcore.common.machine.trait;

import org.leodreamer.sftcore.api.machine.trait.IKineticMachine;
import org.leodreamer.sftcore.api.recipe.capability.StressRecipeCapability;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.trait.ICapabilityTrait;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableRecipeHandlerTrait;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;

import net.minecraft.util.Mth;

import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class NotifiableStressTrait extends NotifiableRecipeHandlerTrait<Float>
    implements ICapabilityTrait {

    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
        NotifiableStressTrait.class, NotifiableRecipeHandlerTrait.MANAGED_FIELD_HOLDER
    );

    @Getter
    @Setter
    private long timeStamp;
    @Getter
    public final IO handlerIO;
    @Getter
    public final IO capabilityIO;
    private float available, lastSpeed;

    public NotifiableStressTrait(MetaMachine machine, IO handlerIO, IO capabilityIO) {
        super(machine);
        this.handlerIO = handlerIO;
        this.capabilityIO = capabilityIO;
        this.lastSpeed = 0;
    }

    @Override
    public void onMachineLoad() {
        super.onMachineLoad();
        if (machine instanceof IKineticMachine kineticMachine) {
            machine.subscribeServerTick(
                () -> {
                    var speed = kineticMachine.getKineticHolder().getSpeed();
                    if (speed != lastSpeed) {
                        lastSpeed = speed;
                        notifyListeners();
                    }
                }
            );
        }
    }

    @Override
    public List<Float> handleRecipeInner(IO io, GTRecipe recipe, List<Float> left, boolean simulate) {
        if (machine instanceof IKineticMachine kineticMachine) {
            float sum = left.stream().reduce(0f, Float::sum);
            var kineticDefinition = kineticMachine.getKineticDefinition();
            if (io == IO.IN && !kineticDefinition.isSource()) {
                float capacity = Mth.abs(kineticMachine.getKineticHolder().getSpeed()) * kineticDefinition.torque;
                if (capacity > 0) {
                    sum = sum - capacity;
                }
            } else if (io == IO.OUT && kineticDefinition.isSource()) {
                if (simulate) {
                    available = kineticMachine.getKineticHolder().scheduleWorking(sum, true);
                }
                sum = sum - available;
            }
            return sum <= 0 ? null : Collections.singletonList(sum);
        }
        return left;
    }

    @Override
    public @NotNull List<Object> getContents() {
        return List.of(available);
    }

    @Override
    public double getTotalContentAmount() {
        return available;
    }

    public void preWorking() {
        if (machine instanceof IKineticMachine kineticMachine) {
            var kineticDefinition = kineticMachine.getKineticDefinition();
            if (available > 0 && kineticDefinition.isSource()) {
                kineticMachine.getKineticHolder().scheduleWorking(available, false);
            }
        }
    }

    public void postWorking() {
        if (machine instanceof IKineticMachine kineticMachine) {
            var kineticDefinition = kineticMachine.getKineticDefinition();
            if (kineticDefinition.isSource()) {
                kineticMachine.getKineticHolder().stopWorking();
            }
        }
    }

    @Override
    public RecipeCapability<Float> getCapability() {
        return StressRecipeCapability.CAP;
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }
}
