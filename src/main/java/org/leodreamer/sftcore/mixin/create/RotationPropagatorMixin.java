package org.leodreamer.sftcore.mixin.create;

import org.leodreamer.sftcore.api.kinetics.KineticPartHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RotationPropagator.class, remap = false)
public abstract class RotationPropagatorMixin {

    @Inject(method = "handleAdded", at = @At("TAIL"))
    private static void sftcore$refreshConsumersWhenKineticAdded(
        Level worldIn,
        BlockPos pos,
        KineticBlockEntity addedTE,
        CallbackInfo ci
    ) {
        KineticPartHelper.refreshConsumersAround(worldIn, pos);
    }

    @Inject(method = "handleRemoved", at = @At("TAIL"))
    private static void sftcore$refreshConsumersWhenKineticRemoved(
        Level worldIn,
        BlockPos pos,
        KineticBlockEntity removedBE,
        CallbackInfo ci
    ) {
        KineticPartHelper.refreshConsumersAround(worldIn, pos);
    }
}
