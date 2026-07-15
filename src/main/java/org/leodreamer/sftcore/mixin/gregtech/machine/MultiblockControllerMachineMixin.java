package org.leodreamer.sftcore.mixin.gregtech.machine;

import org.leodreamer.sftcore.common.advancement.SFTCriteriaTriggers;

import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.part.MultiblockPartMachine;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MultiblockControllerMachine.class, remap = false)
public abstract class MultiblockControllerMachineMixin {

    @Shadow
    protected boolean isFormed;

    @Unique
    private boolean sftcore$wasFormedBeforeStructureFormed;

    @Unique
    private boolean sftcore$sharedPartTriggeredThisFormation;

    @Inject(method = "formStructure", at = @At("HEAD"))
    private void sftcore$captureOldFormedState(String substructureName, CallbackInfo ci) {
        this.sftcore$wasFormedBeforeStructureFormed = isFormed;
        this.sftcore$sharedPartTriggeredThisFormation = false;
    }

    /**
     * GTM calls part.addedToController(this, substructureName) after collecting all matched parts.
     * Before that call, part.getControllers() still represents the old ownership.
     * <p>
     * If the part already belongs to another formed controller, the new forming
     * controller is sharing a part with an existing multiblock.
     */
    @Redirect(
        method = "formStructure",
        at = @At(
            value = "INVOKE",
            target = "Lcom/gregtechceu/gtceu/api/machine/multiblock/part/MultiblockPartMachine;addedToController(Lcom/gregtechceu/gtceu/api/machine/multiblock/MultiblockControllerMachine;Ljava/lang/String;)V"
        )
    )
    private void sftcore$detectSharedPartBeforeAddingController(
        MultiblockPartMachine part,
        MultiblockControllerMachine controller,
        String substructureName
    ) {
        if (
            !this.sftcore$wasFormedBeforeStructureFormed && !this.sftcore$sharedPartTriggeredThisFormation &&
                sftcore$isAlreadyControlledByAnotherFormedMachine(part, controller)
        ) {
            this.sftcore$sharedPartTriggeredThisFormation = true;
            SFTCriteriaTriggers.SHARED_MULTIBLOCK_PART.trigger(controller);
        }

        part.addedToController(controller, substructureName);
    }

    @Inject(method = "formStructure", at = @At("RETURN"))
    private void sftcore$triggerAdvancementWhenStructureFormed(String substructureName, CallbackInfo ci) {
        if (!sftcore$wasFormedBeforeStructureFormed && isFormed) {
            SFTCriteriaTriggers.FORMED_GT_MULTIBLOCK.trigger((MultiblockControllerMachine) (Object) this);
        }
    }

    @Unique
    private static boolean sftcore$isAlreadyControlledByAnotherFormedMachine(
        MultiblockPartMachine part,
        MultiblockControllerMachine currentController
    ) {
        var currentControllerPos = currentController.getBlockPos();

        for (var existingController : part.getControllers()) {
            if (existingController == currentController) {
                continue;
            }

            if (existingController.getBlockPos().equals(currentControllerPos)) {
                continue;
            }

            if (existingController.isFormed()) {
                return true;
            }
        }

        return false;
    }
}
