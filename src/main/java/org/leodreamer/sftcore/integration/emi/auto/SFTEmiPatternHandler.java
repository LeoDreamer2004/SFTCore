package org.leodreamer.sftcore.integration.emi.auto;

import org.leodreamer.sftcore.integration.ae2.feature.ISendToGTMachine;
import org.leodreamer.sftcore.integration.ae2.feature.IVirtualCatalystEncoding;
import org.leodreamer.sftcore.integration.emi.recipe.IGTEmiRecipe;
import org.leodreamer.sftcore.util.ReflectUtils;

import com.gregtechceu.gtceu.api.recipe.ingredient.IntCircuitIngredient;
import com.gregtechceu.gtceu.common.data.GTRecipeCapabilities;

import net.minecraft.world.item.ItemStack;

import appeng.menu.me.items.PatternEncodingTermMenu;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;

import java.util.ArrayList;
import java.util.List;

public class SFTEmiPatternHandler {

    public static <
        T extends PatternEncodingTermMenu> void handleEmiRecipe(EmiRecipe recipe, EmiCraftContext<T> context) {
        var virtualEncoding = (IVirtualCatalystEncoding) context.getScreenHandler();
        if (recipe instanceof IGTEmiRecipe gtEmiRecipe) {
            var menu = (ISendToGTMachine) context.getScreenHandler();
            var gtRecipe = gtEmiRecipe.sftcore$recipe();
            int circuit = 0;
            var catalysts = new ArrayList<ItemStack>();
            for (var ingredient : gtRecipe.getInputContents(GTRecipeCapabilities.ITEM)) {
                if (ingredient.content() instanceof IntCircuitIngredient ci) {
                    circuit = ReflectUtils.getFieldValue(ci, "configuration", Integer.class);
                }
                if (ingredient.chance() == 0) {
                    var stacks = GTRecipeCapabilities.ITEM.of(ingredient.content()).getItems();
                    if (stacks.length > 0) catalysts.add(stacks[0].copyWithCount(1));
                }
            }
            var type = gtRecipe.recipeType;
            menu.sftcore$setGTRecipeInfo(new ISendToGTMachine.RecipeInfo(type, circuit));
            virtualEncoding.sftcore$setVirtualCatalysts(catalysts);
        } else {
            virtualEncoding.sftcore$setVirtualCatalysts(List.of());
        }
    }

}
