package org.leodreamer.sftcore.mixin.gregtech.machine;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.common.machine.multiblock.part.DualHatchPartMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.part.ItemBusPartMachine;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DualHatchPartMachine.class)
public abstract class DualHatchMixin extends ItemBusPartMachine {

    public DualHatchMixin(BlockEntityCreationInfo info, int tier, IO io) {
        super(info, tier, io);
    }

    @Inject(method = "getInventorySize", at = @At("HEAD"), cancellable = true, remap = false)
    private void sftcore$inventorySize(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue((int) Math.pow((getTier()), 2));
    }

    @Inject(method = "getTankCapacity", at = @At("HEAD"), cancellable = true, remap = false)
    private static void sftcore$getTankCapacity(int initialCapacity, int tier, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue((initialCapacity / 2) * (1 << (tier - 1)));
    }
}
