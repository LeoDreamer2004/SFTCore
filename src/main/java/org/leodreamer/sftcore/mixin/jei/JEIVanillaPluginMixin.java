package org.leodreamer.sftcore.mixin.jei;

import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.library.plugins.vanilla.VanillaPlugin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = VanillaPlugin.class, remap = false)
public abstract class JEIVanillaPluginMixin {

    // We have EMI here so skip the JEI vanilla loading
    @Inject(method = "registerRecipes", at = @At("HEAD"), cancellable = true)
    private void sftcore$skipVanillaRecipes(IRecipeRegistration registration, CallbackInfo ci) {
        ci.cancel();
    }
}
