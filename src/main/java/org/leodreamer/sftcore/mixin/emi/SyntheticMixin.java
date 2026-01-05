package org.leodreamer.sftcore.mixin.emi;

import org.leodreamer.sftcore.integration.emi.IGTEmiRecipe;
import org.leodreamer.sftcore.util.GTMachineUtils;

import com.gregtechceu.gtceu.integration.emi.recipe.GTEmiRecipe;

import net.minecraft.client.gui.GuiGraphics;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.runtime.EmiFavorite;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.leodreamer.sftcore.integration.emi.EmiRecipeAutocraft.openedMachine;

@Mixin(EmiFavorite.Synthetic.class)
public class SyntheticMixin extends EmiFavorite {

    public SyntheticMixin(EmiIngredient stack, @Nullable EmiRecipe recipe) {
        super(stack, recipe);
    }

    @Inject(method = "render", at = @At("TAIL"), remap = false)
    public void highlightCurrentMachineAvailable(
        GuiGraphics raw, int x, int y, float delta, int flags, CallbackInfo ci
    ) {
        if (openedMachine != null && recipe instanceof GTEmiRecipe gtRecipe) {
            var recipeType = ((IGTEmiRecipe) gtRecipe).sftcore$recipe().recipeType;
            if (GTMachineUtils.guessRecipe(openedMachine, recipeType).ok()) {
                var context = EmiDrawContext.wrap(raw);
                context.fill(x - 1, y - 1, 18, 18, 0x50FFD700);
            }
        }
    }
}
