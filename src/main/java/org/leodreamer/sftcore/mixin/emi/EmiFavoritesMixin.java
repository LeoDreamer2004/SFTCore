package org.leodreamer.sftcore.mixin.emi;

import dev.emi.emi.api.recipe.EmiPlayerInventory;
import dev.emi.emi.runtime.EmiFavorites;
import org.leodreamer.sftcore.integration.emi.EmiCraftingProgress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EmiFavorites.class, remap = false)
public abstract class EmiFavoritesMixin {
    @Inject(method = "updateSynthetic", at = @At("TAIL"))
    private static void sftcore$appendCraftingProgressSynthetic(
        EmiPlayerInventory inv,
        CallbackInfo ci
    ) {
        EmiCraftingProgress.appendSyntheticFavorites();
    }
}
