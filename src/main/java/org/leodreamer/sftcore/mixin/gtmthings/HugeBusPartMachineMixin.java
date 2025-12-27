package org.leodreamer.sftcore.mixin.gtmthings;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.IHasCircuitSlot;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.hepdd.gtmthings.common.block.machine.multiblock.part.HugeBusPartMachine;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HugeBusPartMachine.class)
public class HugeBusPartMachineMixin extends TieredIOPartMachine implements IHasCircuitSlot {

    @Shadow(remap = false)
    @Final
    protected NotifiableItemStackHandler circuitInventory;

    public HugeBusPartMachineMixin(IMachineBlockEntity holder, int tier, IO io) {
        super(holder, tier, io);
    }

    @Override
    @Unique
    public NotifiableItemStackHandler getCircuitInventory() {
        return circuitInventory;
    }

    @Inject(method = "getInventorySize", at = @At
            ("HEAD"), cancellable = true, remap = false)
    private void setInventorySize(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue((getTier() + 1) * 2);
    }
}
