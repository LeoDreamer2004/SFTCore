package org.leodreamer.sftcore.mixin.gregtech.machine;

import org.leodreamer.sftcore.common.data.lang.MixinTooltips;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.integration.ae2.machine.MEBusPartMachine;
import com.gregtechceu.gtceu.integration.ae2.machine.MEPatternBufferPartMachine;

import net.minecraft.network.chat.Component;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;

@Mixin(MEPatternBufferPartMachine.class)
public abstract class MEPatternBufferPartMachineMixin extends MEBusPartMachine {

    public MEPatternBufferPartMachineMixin(IMachineBlockEntity holder, IO io, Object... args) {
        super(holder, io, args);
    }

    @ModifyArg(
        method = "getTerminalGroup",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/api/implementations/blockentities/PatternContainerGroup;<init>(Lappeng/api/stacks/AEItemKey;Lnet/minecraft/network/chat/Component;Ljava/util/List;)V"
        ),
        index = 2,
        remap = false
    )
    private List<Component> sftcore$addBufferTooltips(List<Component> original) {
        return List.of(
            Component.translatable(MixinTooltips.PART_FROM)
                .append(Component.translatable(getDefinition().getDescriptionId()))
        );
    }
}
