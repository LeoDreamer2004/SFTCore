package org.leodreamer.sftcore.mixin.gregtech.machine;

import org.leodreamer.sftcore.common.data.lang.MixinTooltips;
import org.leodreamer.sftcore.integration.ae2.feature.IPromptProvider;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.integration.ae2.machine.MEBusPartMachine;
import com.gregtechceu.gtceu.integration.ae2.machine.MEPatternBufferPartMachine;

import net.minecraft.network.chat.Component;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(MEPatternBufferPartMachine.class)
public abstract class MEPatternBufferPartMachineMixin extends MEBusPartMachine implements IPromptProvider {

    // custom name now acts as the prompt, instead of the name shown in the pattern group!
    @Shadow(remap = false)
    private String customName;

    public MEPatternBufferPartMachineMixin(IMachineBlockEntity holder, IO io, Object... args) {
        super(holder, io, args);
    }

    @Override
    @Unique
    public String sftcore$getPrompt() {
        return customName;
    }

    @Override
    @Unique
    public void sftcore$setPrompt(String prompt) {
        customName = prompt;
    }

    @Redirect(
        method = "getTerminalGroup",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/String;isEmpty()Z"
        ),
        remap = false
    )
    private boolean sftcore$passCustomName(String instance) {
        return true;
    }

    /**
     * More Friendly Display for ME Pattern Buffer in Pattern Access Terminal
     * Hacky in tooltips, as the last one should contain the machine/controllers' position
     * See {@link org.leodreamer.sftcore.integration.ae2.logic.GTTransferLogic}
     */
    @ModifyArg(
        method = "getTerminalGroup",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/api/implementations/blockentities/PatternContainerGroup;<init>(Lappeng/api/stacks/AEItemKey;Lnet/minecraft/network/chat/Component;Ljava/util/List;)V",
            ordinal = 1
        ),
        index = 2,
        remap = false
    )
    private List<Component> sftcore$formedWithoutCustomNameTooltip(List<Component> original) {
        var pos = getControllers().first().self().getPos();
        return List.of(
            Component.translatable(MixinTooltips.PART_FROM)
                .append(Component.translatable(getDefinition().getDescriptionId())),
            Component.translatable(MixinTooltips.MACHINE_POS, pos.getX(), pos.getY(), pos.getZ())
        );
    }
}
