package org.leodreamer.sftcore.mixin.emi;

import org.leodreamer.sftcore.integration.emi.SFTJemiGasBridge;

import com.lowdragmc.lowdraglib.emi.ModularEmiRecipe;
import com.lowdragmc.lowdraglib.gui.ingredient.IRecipeIngredientSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(value = ModularEmiRecipe.class, remap = false)
public abstract class ModularEmiRecipeMixin {

    @Redirect(
        method = "<init>",
        at = @At(
            value = "INVOKE",
            target = "Lcom/lowdragmc/lowdraglib/gui/ingredient/IRecipeIngredientSlot;getXEIIngredients()Ljava/util/List;"
        )
    )
    private List<Object> sftcore$wrapGasIngredientsForRecipeIndex(IRecipeIngredientSlot slot) {
        return slot.getXEIIngredients().stream()
            .map(SFTJemiGasBridge::wrapGasIngredient)
            .map(Object.class::cast).toList();
    }
}
