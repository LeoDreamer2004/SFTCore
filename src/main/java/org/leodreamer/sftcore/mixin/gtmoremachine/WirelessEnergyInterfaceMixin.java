package org.leodreamer.sftcore.mixin.gtmoremachine;

import org.leodreamer.sftcore.integration.gtmm.WirelessEnergyInterfaceFEAdapterTrait;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableEnergyContainer;

import cn.qiuye.gtmoremachine.common.machine.electric.WirelessEnergyInterface;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WirelessEnergyInterface.class, remap = false)
public abstract class WirelessEnergyInterfaceMixin extends TieredIOPartMachine {

    @Shadow
    @Final
    public NotifiableEnergyContainer energyContainer;

    public WirelessEnergyInterfaceMixin(BlockEntityCreationInfo info, int tier, IO io) {
        super(info, tier, io);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void sftcore$attachForgeEnergyInput(
        BlockEntityCreationInfo holder,
        CallbackInfo ci
    ) {
        attachTrait(new WirelessEnergyInterfaceFEAdapterTrait(this.energyContainer));
    }
}
