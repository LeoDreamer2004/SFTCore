package org.leodreamer.sftcore.mixin.gregtech.recipe;

import org.leodreamer.sftcore.common.advancement.SFTCriteriaTriggers;

import com.gregtechceu.gtceu.api.machine.trait.MachineTrait;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RecipeLogic.class, remap = false)
public abstract class RecipeLogicMixin extends MachineTrait {

    @Shadow
    @Nullable
    protected GTRecipe lastRecipe;

    @Shadow
    @Nullable
    protected GTRecipe lastOriginRecipe;

    @Inject(
        method = "onRecipeFinish",
        at = @At(
            value = "INVOKE",
            target = "Lcom/gregtechceu/gtceu/api/machine/trait/RecipeLogic;handleRecipeIO(Lcom/gregtechceu/gtceu/api/recipe/GTRecipe;Lcom/gregtechceu/gtceu/api/capability/recipe/IO;)Lcom/gregtechceu/gtceu/api/recipe/ActionResult;",
            shift = At.Shift.AFTER
        )
    )
    private void sftcore$triggerAdvancementWhenRecipeExecuted(CallbackInfo ci) {
        var recipe = this.lastOriginRecipe != null ? this.lastOriginRecipe : this.lastRecipe;

        if (recipe == null || recipe.id == null) {
            return;
        }

        SFTCriteriaTriggers.RECIPE_EXECUTED.trigger(getMachine(), recipe.id);
    }
}
