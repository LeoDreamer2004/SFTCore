package org.leodreamer.sftcore.mixin.gregtech.machine;

import org.leodreamer.sftcore.api.feature.IWirelessAEMachine;
import org.leodreamer.sftcore.api.machine.trait.WirelessGridHolder;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.common.machine.multiblock.part.FluidHatchPartMachine;
import com.gregtechceu.gtceu.integration.ae2.machine.MEHatchPartMachine;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MEHatchPartMachine.class)
public abstract class MEHatchPartMachineMixin extends FluidHatchPartMachine implements IWirelessAEMachine {

    @Unique
    private WirelessGridHolder sftcore$wirelessHolder;

    public MEHatchPartMachineMixin(BlockEntityCreationInfo info, int tier, IO io, int initialCapacity, int slots) {
        super(info, tier, io, initialCapacity, slots);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addWirelessHolder(BlockEntityCreationInfo info, IO io, CallbackInfo ci) {
        sftcore$wirelessHolder = attachTrait(new WirelessGridHolder());
    }

    @Override
    public WirelessGridHolder sftcore$getWirelessHolder() {
        return sftcore$wirelessHolder;
    }
}
