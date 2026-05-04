package org.leodreamer.sftcore.mixin.gregtech.machine;

import org.leodreamer.sftcore.common.advancement.SFTCriteriaTriggers;

import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MultiblockControllerMachine.class, remap = false)
public abstract class MultiblockControllerMachineMixin {

    @Shadow
    protected boolean isFormed;

    @Unique
    private boolean sftcore$wasFormedBeforeStructureFormed;

    @Inject(method = "onStructureFormed", at = @At("HEAD"), remap = false)
    private void sftcore$captureOldFormedState(CallbackInfo ci) {
        this.sftcore$wasFormedBeforeStructureFormed = isFormed;
    }

    @Inject(method = "onStructureFormed", at = @At("RETURN"), remap = false)
    private void sftcore$triggerAdvancementWhenStructureFormed(CallbackInfo ci) {
        if (!sftcore$wasFormedBeforeStructureFormed && isFormed) {
            SFTCriteriaTriggers.FORMED_GT_MULTIBLOCK.trigger((MultiblockControllerMachine) (Object) this);
        }
    }
}
