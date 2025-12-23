package org.leodreamer.sftcore.mixin.create;

import org.leodreamer.sftcore.api.blockentity.KineticMachineBlockEntity;
import org.leodreamer.sftcore.api.machine.trait.IKineticMachine;

import net.minecraft.core.Direction;

import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RotationPropagator.class)
public abstract class RotationPropagatorMixin {

    @Inject(method = "getAxisModifier", at = @At(value = "RETURN"), remap = false, cancellable = true)
    private static void injectAxisModifier(
                                           KineticBlockEntity block, Direction direction,
                                           CallbackInfoReturnable<Float> cir) {
        if ((block.hasSource() || block.isSource()) &&
                block instanceof KineticMachineBlockEntity kineticMachineBlockEntity) {
            if (kineticMachineBlockEntity.getMetaMachine() instanceof IKineticMachine kineticMachine) {
                cir.setReturnValue(kineticMachine.getRotationSpeedModifier(direction));
            }
        }
    }
}
