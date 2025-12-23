package org.leodreamer.sftcore.mixin.carryon;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tschipp.carryon.common.carry.PickupHandler;

import java.util.function.BiFunction;

@Mixin(PickupHandler.class)
public class PickupHandlerMixin {

    @Inject(method = "tryPickUpBlock", at = @At("HEAD"), cancellable = true, remap = false)
    private static void disableGTMultiblockController(
        ServerPlayer player,
        BlockPos pos,
        Level level,
        BiFunction<BlockState, BlockPos, Boolean> pickupCallback,
        CallbackInfoReturnable<Boolean> cir
    ) {
        if (MetaMachine.getMachine(level, pos) instanceof MultiblockControllerMachine) {
            cir.setReturnValue(false);
        }
    }
}
