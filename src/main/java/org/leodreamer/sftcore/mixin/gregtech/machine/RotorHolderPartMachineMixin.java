package org.leodreamer.sftcore.mixin.gregtech.machine;

import org.leodreamer.sftcore.common.advancement.SFTCriteriaTriggers;

import com.gregtechceu.gtceu.api.machine.feature.multiblock.IWorkableMultiController;
import com.gregtechceu.gtceu.common.machine.multiblock.generator.LargeTurbineMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.part.RotorHolderPartMachine;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = RotorHolderPartMachine.class, remap = false)
public abstract class RotorHolderPartMachineMixin {

    @Shadow
    public abstract int getRotorSpeed();

    @Shadow
    public abstract int getMaxRotorHolderSpeed();

    @Unique
    private boolean sftcore$wasAtFullSpeed;

    @Inject(method = "onWorking", at = @At("TAIL"))
    private void sftcore$triggerLargeTurbineFullSpeed(
        IWorkableMultiController controller,
        CallbackInfoReturnable<Boolean> cir
    ) {
        int maxSpeed = getMaxRotorHolderSpeed();
        boolean isAtFullSpeed = maxSpeed > 0 && getRotorSpeed() >= maxSpeed;

        if (!this.sftcore$wasAtFullSpeed && isAtFullSpeed && controller instanceof LargeTurbineMachine turbine) {
            SFTCriteriaTriggers.LARGE_TURBINE_FULL_SPEED.trigger(turbine);
        }

        this.sftcore$wasAtFullSpeed = isAtFullSpeed;
    }
}
