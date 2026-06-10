package org.leodreamer.sftcore.mixin.create;

import org.leodreamer.sftcore.api.kinetics.KineticPartHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = KineticBlockEntity.class, remap = false)
public abstract class KineticBlockEntityMixin extends SmartBlockEntity {

    public KineticBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "onSpeedChanged", at = @At("TAIL"))
    private void sftcore$refreshAdjacentKineticParts(float previousSpeed, CallbackInfo ci) {
        KineticPartHelper.refreshConsumersAround(getLevel(), getBlockPos());
    }
}
