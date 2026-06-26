package org.leodreamer.sftcore.integration.emi.auto;

import org.leodreamer.sftcore.integration.ae2.feature.ISendToGTMachine;
import org.leodreamer.sftcore.integration.emi.recipe.IGTEmiRecipe;
import org.leodreamer.sftcore.util.ReflectUtils;

import com.gregtechceu.gtceu.api.recipe.ingredient.IntCircuitIngredient;
import com.gregtechceu.gtceu.common.data.GTRecipeCapabilities;

import appeng.menu.me.items.PatternEncodingTermMenu;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;

public class SFTEmiPatternHandler {

    public static <
        T extends PatternEncodingTermMenu> void handleEmiRecipe(EmiRecipe recipe, EmiCraftContext<T> context) {
        if (recipe instanceof IGTEmiRecipe gtEmiRecipe) {
            var menu = (ISendToGTMachine) context.getScreenHandler();
            var gtRecipe = gtEmiRecipe.sftcore$recipe();
            int circuit = 0;
            for (var ingredient : gtRecipe.getInputContents(GTRecipeCapabilities.ITEM)) {
                if (ingredient.content() instanceof IntCircuitIngredient ci) {
                    circuit = ReflectUtils.getFieldValue(ci, "configuration", Integer.class);
                    break;
                }
            }
            var type = gtRecipe.recipeType;
            menu.sftcore$setGTRecipeInfo(new ISendToGTMachine.RecipeInfo(type, circuit));
        }
    }
}
