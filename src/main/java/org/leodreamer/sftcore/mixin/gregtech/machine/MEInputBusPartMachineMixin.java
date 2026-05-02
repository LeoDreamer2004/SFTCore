package org.leodreamer.sftcore.mixin.gregtech.machine;

import org.leodreamer.sftcore.common.machine.multiblock.part.MEAdvancedInputBusPartMachine;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.integration.ae2.machine.MEInputBusPartMachine;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = MEInputBusPartMachine.class, remap = false)
public class MEInputBusPartMachineMixin {

    @ModifyArg(
        method = "<init>",
        at = @At(
            value = "INVOKE",
            target = "Lcom/gregtechceu/gtceu/integration/ae2/machine/MEBusPartMachine;<init>(Lcom/gregtechceu/gtceu/api/machine/IMachineBlockEntity;Lcom/gregtechceu/gtceu/api/capability/recipe/IO;[Ljava/lang/Object;)V"
        ), index = 1
    )
    private static IO setIOForAdvancedMachine(IMachineBlockEntity holder, IO io, Object... args) {
        if (args.length > 0 && args[0] == MEAdvancedInputBusPartMachine.class) {
            return IO.BOTH;
        }
        return io;
    }
}
