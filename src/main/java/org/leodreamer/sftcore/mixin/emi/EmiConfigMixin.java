package org.leodreamer.sftcore.mixin.emi;

import dev.emi.emi.config.EmiConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EmiConfig.class, remap = false)
public abstract class EmiConfigMixin {

    @Inject(method = "loadConfig()V", at = @At("TAIL"))
    private static void sftcore$forceFastSearchDefaults(CallbackInfo ci) {
        EmiConfig.searchTooltipByDefault = false;
        EmiConfig.searchTagsByDefault = false;
    }
}
