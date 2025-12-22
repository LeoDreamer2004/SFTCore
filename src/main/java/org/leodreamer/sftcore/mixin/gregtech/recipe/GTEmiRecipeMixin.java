package org.leodreamer.sftcore.mixin.gregtech.recipe;

import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.integration.emi.recipe.GTEmiRecipe;
import org.leodreamer.sftcore.integration.emi.IGTEmiRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GTEmiRecipe.class)
public class GTEmiRecipeMixin implements IGTEmiRecipe {
    @Shadow(remap = false)
    @Final
    GTRecipe recipe;

    @Override
    public GTRecipe sftcore$recipe() {
        return recipe;
    }
}
