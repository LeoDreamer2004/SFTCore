package org.leodreamer.sftcore.mixin.gregtech.util;

import org.leodreamer.sftcore.common.advancement.SFTCriteriaTriggers;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.utils.GTUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GTUtil.class, remap = false)
public abstract class GTUtilMixin {

    @Inject(method = "doExplosion", at = @At("HEAD"), remap = false)
    private static void sftcore$triggerAdvancementWhenMachineExploded(
        Level level,
        BlockPos pos,
        float explosionPower,
        CallbackInfo ci
    ) {
        if (level.isClientSide) {
            return;
        }

        var machine = MetaMachine.getMachine(level, pos);
        if (machine == null) {
            return;
        }

        SFTCriteriaTriggers.MACHINE_EXPLODED.trigger(machine);
    }
}
