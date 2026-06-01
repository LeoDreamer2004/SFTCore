package org.leodreamer.sftcore.mixin.emi;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.integration.IntegrateMods;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.EmiRecipeSorting;
import dev.emi.emi.data.EmiRecipeCategoryProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Comparator;

@Mixin(value = EmiRecipeCategoryProperties.class, remap = false)
public abstract class EmiRecipeCategoryPropertiesMixin {

    @Inject(method = "getSort", at = @At("HEAD"), cancellable = true)
    private static void sftcore$disableGtSort(
        EmiRecipeCategory category,
        CallbackInfoReturnable<Comparator<EmiRecipe>> cir
    ) {
        String mod = category.getId().getNamespace();
        if (mod.equals(IntegrateMods.GTM) || mod.equals(SFTCore.MOD_ID) || mod.equals(IntegrateMods.GTMM)) {
            cir.setReturnValue(EmiRecipeSorting.none());
        }
    }
}
