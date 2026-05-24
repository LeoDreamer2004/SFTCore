package org.leodreamer.sftcore.mixin.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.registry.EmiRecipeFiller;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.leodreamer.sftcore.integration.emi.EmiCraftingProgress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EmiRecipeFiller.class, remap = false)
public abstract class EmiRecipeFillerMixin {
    @Inject(method = "performFill", at = @At("HEAD"))
    private static <T extends AbstractContainerMenu> void sftcore$prepareCraftingProgressMark(
        EmiRecipe recipe,
        AbstractContainerScreen<T> screen,
        EmiCraftContext.Type type,
        EmiCraftContext.Destination destination,
        int amount,
        CallbackInfoReturnable<Boolean> cir
    ) {
        if (type == EmiCraftContext.Type.CRAFTABLE) {
            EmiCraftingProgress.prepareMark(recipe, amount);
        }
    }

    @Inject(method = "performFill", at = @At("RETURN"))
    private static <T extends AbstractContainerMenu> void sftcore$commitCraftingProgressMark(
        EmiRecipe recipe,
        AbstractContainerScreen<T> screen,
        EmiCraftContext.Type type,
        EmiCraftContext.Destination destination,
        int amount,
        CallbackInfoReturnable<Boolean> cir
    ) {
        EmiCraftingProgress.commitPreparedMark(cir.getReturnValueZ());
    }
}
