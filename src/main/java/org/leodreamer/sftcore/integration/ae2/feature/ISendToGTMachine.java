package org.leodreamer.sftcore.integration.ae2.feature;

import com.gregtechceu.gtceu.api.recipe.GTRecipeType;

public interface ISendToGTMachine {
    void sftcore$setGTType(GTRecipeType type);

    void sftcore$sendToGTMachine(int chooseIndex);
}
