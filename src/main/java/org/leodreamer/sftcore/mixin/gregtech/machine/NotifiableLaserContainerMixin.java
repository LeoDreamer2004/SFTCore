package org.leodreamer.sftcore.mixin.gregtech.machine;

import org.leodreamer.sftcore.common.advancement.SFTCriteriaTriggers;

import com.gregtechceu.gtceu.api.machine.trait.NotifiableEnergyContainer;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableLaserContainer;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.ActiveTransformerMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.part.LaserHatchPartMachine;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = NotifiableLaserContainer.class, remap = false)
public abstract class NotifiableLaserContainerMixin extends NotifiableEnergyContainer {

    @Unique
    private long sftcore$energyBeforeLaserTick;

    public NotifiableLaserContainerMixin(
        long maxCapacity,
        long maxInputVoltage,
        long maxInputAmperage,
        long maxOutputVoltage,
        long maxOutputAmperage
    ) {
        super(maxCapacity, maxInputVoltage, maxInputAmperage, maxOutputVoltage, maxOutputAmperage);
    }

    @Inject(method = "serverTick", at = @At("HEAD"), remap = false)
    private void sftcore$captureEnergyBeforeLaserTick(CallbackInfo ci) {
        sftcore$energyBeforeLaserTick = getEnergyStored();
    }

    @Inject(method = "serverTick", at = @At("RETURN"), remap = false)
    private void sftcore$triggerWhenLaserEmitted(CallbackInfo ci) {
        // check if it actually transferred EU
        if (sftcore$energyBeforeLaserTick <= getEnergyStored()) {
            return;
        }

        if (getMachine() instanceof LaserHatchPartMachine hatch) {
            for (var controller : hatch.getControllers()) {
                if (controller instanceof ActiveTransformerMachine transformer && transformer.isFormed()) {
                    SFTCriteriaTriggers.ACTIVE_TRANSFORMER_LASER.trigger(transformer);
                    return;
                }
            }
        }
    }
}
