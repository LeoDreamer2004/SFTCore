package org.leodreamer.sftcore.mixin.gregtech.machine;

import org.leodreamer.sftcore.common.advancement.SFTCriteriaTriggers;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.CleanroomMachine;
import com.gregtechceu.gtceu.common.machine.trait.CleanroomProviderTrait;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CleanroomMachine.class, remap = false)
public abstract class CleanroomMachineMixin extends WorkableElectricMultiblockMachine {

    @Shadow
    private int lDist;

    @Shadow
    private int rDist;

    @Shadow
    private int bDist;

    @Shadow
    private int fDist;

    @Shadow
    private int hDist;

    @Unique
    private boolean sftcore$wasCleanBeforeAdjust;

    public CleanroomMachineMixin(BlockEntityCreationInfo info, RecipeLogic recipeLogic) {
        super(info, recipeLogic);
    }

    @Inject(method = "adjustCleanAmount", at = @At("HEAD"), remap = false)
    private void sftcore$captureCleanroomStateBeforeAdjust(int amount, CallbackInfo ci) {
        var trait = getTrait(CleanroomProviderTrait.TYPE);

        this.sftcore$wasCleanBeforeAdjust = trait != null && trait.isActive();
    }

    @Inject(method = "adjustCleanAmount", at = @At("RETURN"), remap = false)
    private void sftcore$triggerWhenMaxCleanroomBecomesClean(int amount, CallbackInfo ci) {
        if (isRemote() || !isFormed()) {
            return;
        }

        if (!sftcore$isMaxSizeCleanroom()) {
            return;
        }

        var trait = getTrait(CleanroomProviderTrait.TYPE);

        if (trait == null || !trait.isActive()) {
            return;
        }

        if (this.sftcore$wasCleanBeforeAdjust) {
            return;
        }

        SFTCriteriaTriggers.MAX_CLEANROOM_CLEAN.trigger((CleanroomMachine) (Object) this);
    }

    @Unique
    private boolean sftcore$isMaxSizeCleanroom() {
        return this.lDist == 7 && this.rDist == 7 && this.bDist == 7 && this.fDist == 7 && this.hDist == 14;
    }
}
