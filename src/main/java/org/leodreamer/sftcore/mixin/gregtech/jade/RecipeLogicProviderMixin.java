package org.leodreamer.sftcore.mixin.gregtech.jade;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.integration.jade.provider.RecipeLogicProvider;
import com.gregtechceu.gtceu.utils.GTUtil;

import net.minecraft.nbt.CompoundTag;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RecipeLogicProvider.class)
public class RecipeLogicProviderMixin {

    @Inject(
        method = "write(Lnet/minecraft/nbt/CompoundTag;Lcom/gregtechceu/gtceu/api/machine/trait/RecipeLogic;)V",
        at = @At("TAIL"), remap = false
    )
    private void fixVoltageInfoForWirelessHatches(CompoundTag data, RecipeLogic capability, CallbackInfo ci) {
        if (capability.machine instanceof WorkableElectricMultiblockMachine) {
            var recipe = data.getCompound("Recipe");
            long eut = recipe.getLong("EUt");
            long voltage = GTValues.VEX[GTUtil.getFloorTierByVoltage(eut)];
            recipe.putLong("voltage", voltage);
        }
    }
}
