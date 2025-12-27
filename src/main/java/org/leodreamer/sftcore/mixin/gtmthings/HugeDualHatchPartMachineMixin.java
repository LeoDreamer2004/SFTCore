package org.leodreamer.sftcore.mixin.gtmthings;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;

import com.hepdd.gtmthings.common.block.machine.multiblock.part.HugeBusPartMachine;
import com.hepdd.gtmthings.common.block.machine.multiblock.part.HugeDualHatchPartMachine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HugeDualHatchPartMachine.class)
public class HugeDualHatchPartMachineMixin extends HugeBusPartMachine {

    public HugeDualHatchPartMachineMixin(IMachineBlockEntity holder, int tier, IO io, Object... args) {
        super(holder, tier, io, 9, args);
    }

    @Inject(method = "getTankInventorySize", at = @At("TAIL"), cancellable = true, remap = false)
    private void setTankInventorySize(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(getTier());
    }
}
