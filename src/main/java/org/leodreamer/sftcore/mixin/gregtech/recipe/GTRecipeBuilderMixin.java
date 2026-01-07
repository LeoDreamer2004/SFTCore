package org.leodreamer.sftcore.mixin.gregtech.recipe;

import org.leodreamer.sftcore.Config;

import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;

import net.minecraft.data.recipes.FinishedRecipe;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GTRecipeBuilder.class)
public class GTRecipeBuilderMixin {

    @Shadow(remap = false)
    public int duration;

    @Inject(method = "build", at = @At("HEAD"), remap = false)
    private void modifyDuration(CallbackInfoReturnable<FinishedRecipe> cir) {
        this.duration = (int) (this.duration * Config.durationMultiplier);
        this.duration = Math.max(this.duration, 1);
    }
}
