package org.leodreamer.sftcore.mixin.emi;

import dev.emi.emi.api.recipe.EmiPlayerInventory;
import dev.emi.emi.bom.MaterialNode;
import dev.emi.emi.bom.TreeCost;
import org.leodreamer.sftcore.integration.emi.EmiCraftingProgress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TreeCost.class, remap = false)
public abstract class TreeCostMixin {
    @Inject(method = "calculateProgress", at = @At("HEAD"))
    private void sftcore$injectCraftingProgressInventory(
        MaterialNode node,
        long batches,
        EmiPlayerInventory inventory,
        CallbackInfo ci
    ) {
        EmiCraftingProgress.patchInventory(inventory);
    }
}
