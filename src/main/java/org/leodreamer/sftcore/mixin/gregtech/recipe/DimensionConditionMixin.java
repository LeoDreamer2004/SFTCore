package org.leodreamer.sftcore.mixin.gregtech.recipe;

import org.leodreamer.sftcore.common.data.SFTDimensions;

import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.common.recipe.condition.DimensionCondition;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = DimensionCondition.class, remap = false)
public class DimensionConditionMixin {

    @Shadow
    private ResourceKey<Level> dimension;

    @Inject(method = "testCondition", at = @At("HEAD"), cancellable = true)
    private void enableVoid(GTRecipe recipe, RecipeLogic recipeLogic, CallbackInfoReturnable<Boolean> cir) {
        if ("overworld".equals(dimension.location().getPath())) {
            Level level = recipeLogic.getMachine().getLevel();
            if (level != null && level.dimension() == SFTDimensions.VOID_DIMENSION) {
                cir.cancel();
                cir.setReturnValue(true);
            }
        }
    }
}
