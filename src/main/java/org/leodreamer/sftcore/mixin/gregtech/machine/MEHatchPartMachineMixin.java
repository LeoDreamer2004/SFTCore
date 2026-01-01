package org.leodreamer.sftcore.mixin.gregtech.machine;

import org.leodreamer.sftcore.api.feature.IWirelessAEMachine;
import org.leodreamer.sftcore.api.machine.trait.WirelessGridHolder;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
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

    public MEHatchPartMachineMixin(
        IMachineBlockEntity holder,
        int tier,
        IO io,
        int initialCapacity,
        int slots,
        Object... args
    ) {
        super(holder, tier, io, initialCapacity, slots, args);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addWirelessHolder(IMachineBlockEntity holder, IO io, Object[] args, CallbackInfo ci) {
        sftcore$wirelessHolder = new WirelessGridHolder(this);
    }

    @Override
    public WirelessGridHolder sftcore$getWirelessHolder() {
        return sftcore$wirelessHolder;
    }
}
