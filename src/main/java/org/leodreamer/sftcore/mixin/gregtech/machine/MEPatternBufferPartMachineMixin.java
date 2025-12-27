package org.leodreamer.sftcore.mixin.gregtech.machine;

import appeng.api.implementations.blockentities.PatternContainerGroup;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.integration.ae2.machine.MEBusPartMachine;
import com.gregtechceu.gtceu.integration.ae2.machine.MEPatternBufferPartMachine;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.leodreamer.sftcore.integration.ae2.feature.HackyContainerGroupProxy;
import org.leodreamer.sftcore.integration.ae2.feature.IPromptProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
    public @NotNull String sftcore$getPrompt() {
        return customName;
    }

    @Override
    @Unique
    public void sftcore$setPrompt(@NotNull String prompt) {
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

    @Inject(
            method = "getTerminalGroup",
            at = @At("RETURN"),
            remap = false,
            cancellable = true
    )
    private void recordThePositon(CallbackInfoReturnable<PatternContainerGroup> cir) {
        if (!isFormed()) return;
        var pos = getControllers().first().self().getPos();
        var group = cir.getReturnValue();
        group = HackyContainerGroupProxy.of(group).setBlockPos(pos)
                .recordPartFrom(Component.translatable(getDefinition().getDescriptionId()))
                // the circuit has been set in the name in the original implementation
                // .setCircuit()
                .get();
        cir.setReturnValue(group);
    }
}
