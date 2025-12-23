package org.leodreamer.sftcore.mixin.gregtech.machine;

import com.gregtechceu.gtceu.api.machine.multiblock.CleanroomType;
import com.gregtechceu.gtceu.common.machine.multiblock.part.CleaningMaintenanceHatchPartMachine;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.gregtechceu.gtceu.api.GTValues.HV;
import static com.gregtechceu.gtceu.api.GTValues.ZPM;

@Mixin(CleaningMaintenanceHatchPartMachine.class)
public class CleaningMaintenanceMixin {

    @Final
    @Shadow(remap = false)
    private CleanroomType cleanroomType;

    @Inject(method = "getTier", at = @At("HEAD"), cancellable = true, remap = false)
    public void setMaintenanceHatchTier(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(cleanroomType == CleanroomType.CLEANROOM ? HV : ZPM);
    }
}
