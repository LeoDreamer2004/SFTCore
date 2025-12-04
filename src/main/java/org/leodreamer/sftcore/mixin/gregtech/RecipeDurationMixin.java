package org.leodreamer.sftcore.mixin.gregtech;

import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GTRecipeBuilder.class)
public class RecipeDurationMixin {

    @Shadow(remap = false)
    public int duration;

    @Inject(method = "duration", at = @At("TAIL"), remap = false)
    private void recipeBuilder$modifyDuration(int duration, CallbackInfoReturnable<GTRecipeBuilder> cir) {
        this.duration /= 3;
    }
}
