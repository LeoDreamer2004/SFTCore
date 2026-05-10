package org.leodreamer.sftcore.mixin.gregtech.machine;

import org.leodreamer.sftcore.common.advancement.SFTCriteriaTriggers;

import com.gregtechceu.gtceu.common.machine.multiblock.generator.LargeCombustionEngineMachine;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LargeCombustionEngineMachine.class, remap = false)
public abstract class LargeCombustionEngineMachineMixin {

    @Shadow
    private boolean isOxygenBoosted;

    @Unique
    private boolean sftcore$wasOxygenBoosted;

    @Inject(method = "onWorking", at = @At("RETURN"))
    private void sftcore$triggerWhenOxygenBoosted(CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()) {
            this.sftcore$wasOxygenBoosted = this.isOxygenBoosted;
            return;
        }

        if (!this.sftcore$wasOxygenBoosted && this.isOxygenBoosted) {
            SFTCriteriaTriggers.OXYGEN_BOOSTED_COMBUSTION.trigger(
                (LargeCombustionEngineMachine) (Object) this
            );
        }

        this.sftcore$wasOxygenBoosted = this.isOxygenBoosted;
    }
}
