package org.leodreamer.sftcore.mixin.emi;

import org.leodreamer.sftcore.integration.emi.GTEmiRecipeBackground;

import com.gregtechceu.gtceu.integration.emi.recipe.GTEmiRecipe;

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

@Mixin(WidgetGroup.class)
public abstract class WidgetGroupMixin {

    @Shadow(remap = false)
    @Final
    public List<Widget> widgets;

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    private void emi$removeBackgroundForGT(EmiRecipe recipe, int x, int y, int width, int height, CallbackInfo ci) {
        if (recipe instanceof GTEmiRecipe gtEmiRecipe) {
            widgets.replaceAll(
                    widget -> {
                        if (widget instanceof RecipeBackground) {
                            return new GTEmiRecipeBackground(-2, -2, width + 4, height + 4, gtEmiRecipe);
                        }
                        return widget;
                    });
        }
    }
}
