package org.leodreamer.sftcore.integration.ae2.feature;

import com.gregtechceu.gtceu.api.recipe.GTRecipeType;

public interface ISendToGTMachine {

    record RecipeInfo(GTRecipeType type, int circuit) {}

    void sftcore$setGTRecipeInfo(RecipeInfo recipeInfo);

    void sftcore$sendToGTMachine(int chooseIndex);
}
