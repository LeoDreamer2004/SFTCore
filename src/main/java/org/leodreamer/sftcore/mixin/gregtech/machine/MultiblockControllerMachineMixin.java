package org.leodreamer.sftcore.mixin.gregtech.machine;

import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import org.leodreamer.sftcore.common.advancement.SFTCriteriaTriggers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MultiblockControllerMachine.class, remap = false)
public abstract class MultiblockControllerMachineMixin implements IMultiController {

    @Inject(method = "onStructureFormed", at = @At("RETURN"), remap = false)
    private void sftcore$triggerAdvancementWhenStructureFormed(CallbackInfo ci) {
        if (isFormed()) {
            SFTCriteriaTriggers.FORMED_GT_MULTIBLOCK.trigger(this);
        }
    }
}
