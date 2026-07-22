package org.leodreamer.sftcore.common.data.recipe;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.api.kinetics.KineticRecipeHelper;
import org.leodreamer.sftcore.common.data.recipe.builder.SFTRecipeBuilder;

import com.gregtechceu.gtceu.api.recipe.gui.RecipeUIModifier;

import brachy.modularui.api.drawable.IDrawable;
import brachy.modularui.api.drawable.Text;
import brachy.modularui.integration.recipeviewer.RecipeSlotRole;
import brachy.modularui.integration.recipeviewer.RecipeViewerSlotWidget;
import brachy.modularui.integration.recipeviewer.entry.item.ItemStackList;
import com.simibubi.create.AllBlocks;

@DataGenScanned
public class SFTRecipeUIModifiers {

    @RegisterLanguage("Requires Total Stress: %ssu")
    private static final String ACQUIRE_STRESS = "sftcore.recipe.input_stress";

    public static final RecipeUIModifier SHAFT_INFO = (recipe, widget) -> {
        widget.child(
            RecipeViewerSlotWidget.create()
                .recipeSlotRole(RecipeSlotRole.RENDER_ONLY)
                .value(ItemStackList.of(AllBlocks.SHAFT.asStack()))
                .background(IDrawable.EMPTY)
                .right(3)
                .bottom(3)
        );
    };

    public static final RecipeUIModifier STRESS_INFO = (recipe, widget) -> {
        if (recipe.data.contains(SFTRecipeBuilder.INPUT_STRESS)) {
            float stress = recipe.data.getFloat(SFTRecipeBuilder.INPUT_STRESS);
            widget.textComponents.child(Text.lang(ACQUIRE_STRESS, KineticRecipeHelper.format(stress)).asWidget());
        }
    };
}
