package org.leodreamer.sftcore.integration.emi;

import org.leodreamer.sftcore.integration.ae2.feature.ISendToGTMachine;

import com.gregtechceu.gtceu.integration.emi.recipe.GTEmiRecipe;

import appeng.menu.me.items.PatternEncodingTermMenu;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;

public class SFTEmiPatternHandler {

    public static <
        T extends PatternEncodingTermMenu> void handleEmiRecipe(EmiRecipe recipe, EmiCraftContext<T> context) {
        if (recipe instanceof GTEmiRecipe gtRecipe) {
            var menu = (ISendToGTMachine) context.getScreenHandler();
            var type = ((IGTEmiRecipe) gtRecipe).sftcore$recipe().recipeType;
            menu.sftcore$setGTType(type);
        }
    }
}
