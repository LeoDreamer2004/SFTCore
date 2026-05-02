package org.leodreamer.sftcore.mixin.gregtech.blockentity;

import com.gregtechceu.gtceu.common.blockentity.CableBlockEntity;
import org.leodreamer.sftcore.common.advancement.SFTCriteriaTriggers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CableBlockEntity.class, remap = false)
public abstract class CableBlockEntityMixin {

    @Inject(
        method = "update",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z",
            ordinal = 0
        )
    )
    private void sftcore$triggerAdvancementWhenWireBurned(CallbackInfoReturnable<Boolean> cir) {
        SFTCriteriaTriggers.WIRE_BURNED.trigger((CableBlockEntity) (Object) this);
    }
}
