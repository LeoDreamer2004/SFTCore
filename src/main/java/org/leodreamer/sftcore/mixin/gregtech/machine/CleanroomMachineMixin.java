package org.leodreamer.sftcore.mixin.gregtech.machine;

import org.leodreamer.sftcore.common.advancement.SFTCriteriaTriggers;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.recipe.RecipeLogic;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.CleanroomMachine;
import com.gregtechceu.gtceu.common.machine.trait.CleanroomProviderTrait;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = CleanroomMachine.class, remap = false)
public abstract class CleanroomMachineMixin extends WorkableElectricMultiblockMachine {

    @Shadow
    private List<Integer> bounds;

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
        return this.bounds.size() > 5 &&
            this.bounds.get(1) == CleanroomMachine.MAX_DEPTH &&
            this.bounds.get(2) == CleanroomMachine.MAX_RADIUS &&
            this.bounds.get(3) == CleanroomMachine.MAX_RADIUS &&
            this.bounds.get(4) == CleanroomMachine.MAX_RADIUS &&
            this.bounds.get(5) == CleanroomMachine.MAX_RADIUS;
    }
}
