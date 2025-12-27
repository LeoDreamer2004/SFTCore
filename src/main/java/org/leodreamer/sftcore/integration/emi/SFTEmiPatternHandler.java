package org.leodreamer.sftcore.integration.emi;

import appeng.menu.me.items.PatternEncodingTermMenu;
import com.gregtechceu.gtceu.api.recipe.ingredient.IntCircuitIngredient;
import com.gregtechceu.gtceu.common.data.GTRecipeCapabilities;
import com.gregtechceu.gtceu.integration.emi.recipe.GTEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import org.leodreamer.sftcore.integration.ae2.feature.ISendToGTMachine;
import org.leodreamer.sftcore.util.ReflectUtils;

public class SFTEmiPatternHandler {

    public static <
            T extends PatternEncodingTermMenu> void handleEmiRecipe(EmiRecipe recipe, EmiCraftContext<T> context) {
        if (recipe instanceof GTEmiRecipe gtEmiRecipe) {
            var menu = (ISendToGTMachine) context.getScreenHandler();
            var gtRecipe = ((IGTEmiRecipe) gtEmiRecipe).sftcore$recipe();
            int circuit = 0;
            for (var ingredient : gtRecipe.getInputContents(GTRecipeCapabilities.ITEM)) {
                if (ingredient.content instanceof IntCircuitIngredient ci) {
                    circuit = ReflectUtils.getFieldValue(ci, "configuration", Integer.class);
                    break;
                }
            }
            var type = gtRecipe.recipeType;
            menu.sftcore$setGTRecipeInfo(new ISendToGTMachine.RecipeInfo(type, circuit));
        }
    }
}
