package org.leodreamer.sftcore.mixin.gregtech.xei;

import org.leodreamer.sftcore.integration.emi.IGTEmiRecipe;
import org.leodreamer.sftcore.integration.emi.SFTJemiGasBridge;

import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.integration.emi.recipe.GTEmiRecipe;

import net.minecraft.client.gui.GuiGraphics;

import com.lowdragmc.lowdraglib.gui.ingredient.IRecipeIngredientSlot;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.jemi.JemiStack;
import mekanism.api.chemical.gas.GasStack;
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
            value = "NEW",
            target = "Ldev/emi/emi/api/widget/SlotWidget;"
        )
    )
    private SlotWidget sftcore$createInvisibleGasSlot(
        EmiIngredient stack,
        int x,
        int y
    ) {
        for (var emiStack : stack.getEmiStacks()) {
            if (emiStack instanceof JemiStack<?> jemiStack && jemiStack.ingredient instanceof GasStack) {
                return new SlotWidget(stack, x, y) {

                    @Override
                    public void drawStack(GuiGraphics draw, int mouseX, int mouseY, float delta) {
                        // Do not draw the foreground stack, just the background
                    }
                };
            }
        }

        return new SlotWidget(stack, x, y);
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
