package org.leodreamer.sftcore.mixin.gregtech.xei;

import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.integration.emi.recipe.GTEmiRecipe;
import com.lowdragmc.lowdraglib.gui.ingredient.IRecipeIngredientSlot;
import org.leodreamer.sftcore.integration.emi.IGTEmiRecipe;
import org.leodreamer.sftcore.integration.emi.SFTJemiGasBridge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(value = GTEmiRecipe.class, remap = false)
public class GTEmiRecipeMixin implements IGTEmiRecipe {
    @Shadow
    @Final
    GTRecipe recipe;

    @Override
    public GTRecipe sftcore$recipe() {
        return recipe;
    }

    @Redirect(
        method = "addWidgets",
        at = @At(
            value = "INVOKE",
            target = "Lcom/lowdragmc/lowdraglib/gui/ingredient/IRecipeIngredientSlot;getXEIIngredients()Ljava/util/List;"
        )
    )
    private List<Object> sftcore$wrapGasIngredientsWithJemi(IRecipeIngredientSlot slot) {
        return SFTJemiGasBridge.wrapGasIngredients(slot.getXEIIngredients());
    }
}
