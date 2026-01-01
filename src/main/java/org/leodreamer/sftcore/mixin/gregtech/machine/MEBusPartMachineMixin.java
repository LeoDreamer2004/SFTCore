package org.leodreamer.sftcore.mixin.gregtech.machine;

import org.leodreamer.sftcore.api.feature.IWirelessAEMachine;
import org.leodreamer.sftcore.api.machine.trait.WirelessGridHolder;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.common.machine.multiblock.part.ItemBusPartMachine;
import com.gregtechceu.gtceu.integration.ae2.machine.MEBusPartMachine;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MEBusPartMachine.class)
public abstract class MEBusPartMachineMixin extends ItemBusPartMachine implements IWirelessAEMachine {

    @Unique
    private WirelessGridHolder sftcore$wirelessHolder;

    public MEBusPartMachineMixin(IMachineBlockEntity holder, int tier, IO io, Object... args) {
        super(holder, tier, io, args);
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
