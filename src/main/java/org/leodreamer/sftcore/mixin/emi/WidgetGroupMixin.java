package org.leodreamer.sftcore.mixin.emi;

import org.leodreamer.sftcore.integration.emi.gui.GTEmiRecipeBackground;
import org.leodreamer.sftcore.mixin.gregtech.xei.GTEmiRecipeAccessor;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.screen.WidgetGroup;
import dev.emi.emi.widget.RecipeBackground;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = WidgetGroup.class, remap = false)
public abstract class WidgetGroupMixin {

    @Shadow
    @Final
    public List<Widget> widgets;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void sftcore$replaceBackgroundForGT(
        EmiRecipe recipe, int x, int y, int width, int height, CallbackInfo ci
    ) {
        if (recipe instanceof GTEmiRecipeAccessor gtEmiRecipe) {
            widgets.replaceAll(widget -> {
                if (widget instanceof RecipeBackground) {
                    return new GTEmiRecipeBackground(-5, -2, width + 10, height + 4, gtEmiRecipe);
                }
                return widget;
            });
        }
    }
}
