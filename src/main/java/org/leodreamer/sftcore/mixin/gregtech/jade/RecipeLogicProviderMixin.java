package org.leodreamer.sftcore.mixin.gregtech.jade;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.recipe.RecipeLogic;
import com.gregtechceu.gtceu.integration.jade.provider.RecipeLogicProvider;
import com.gregtechceu.gtceu.utils.GTUtil;

import net.minecraft.nbt.CompoundTag;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeLogicProvider.class)
public class RecipeLogicProviderMixin {

    @Inject(
        method = "write(Lcom/gregtechceu/gtceu/api/machine/trait/recipe/RecipeLogic;)Lnet/minecraft/nbt/CompoundTag;",
        at = @At("TAIL"), remap = false
    )
    private void fixVoltageInfoForWirelessHatches(
        RecipeLogic capability, CallbackInfoReturnable<CompoundTag> cir
    ) {
        if (capability.getMachine() instanceof WorkableElectricMultiblockMachine) {
            var data = cir.getReturnValue();
            var recipe = data.getCompound("Recipe");
            long eut = recipe.getLong("EUt");
            long voltage = GTValues.VEX[GTUtil.getFloorTierByVoltage(eut)];
            recipe.putLong("voltage", voltage);
        }
    }
}
