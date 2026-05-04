package org.leodreamer.sftcore.mixin.gregtech.recipe;

import org.leodreamer.sftcore.Config;

import com.gregtechceu.gtceu.api.recipe.ingredient.EnergyStack;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;

import net.minecraft.data.recipes.FinishedRecipe;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GTRecipeBuilder.class, remap = false)
public abstract class GTRecipeBuilderMixin {

    @Shadow
    public int duration;

    @Shadow
    public abstract EnergyStack EUt();

    @Inject(method = "build", at = @At("HEAD"), remap = false)
    private void modifyDuration(CallbackInfoReturnable<FinishedRecipe> cir) {
        if (EUt().isEmpty()) return;
        // EUt is not empty means this recipe consumes EU
        this.duration = (int) (this.duration * Config.durationMultiplier);
        this.duration = Math.max(this.duration, 1);
    }
}
