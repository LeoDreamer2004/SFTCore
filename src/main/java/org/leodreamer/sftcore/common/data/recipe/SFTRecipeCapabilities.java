package org.leodreamer.sftcore.common.data.recipe;

import org.leodreamer.sftcore.api.recipe.capability.GasRecipeCapability;
import org.leodreamer.sftcore.api.recipe.capability.StressRecipeCapability;

import com.gregtechceu.gtceu.api.registry.GTRegistries;

public class SFTRecipeCapabilities {

    public static void init() {
        GTRegistries.RECIPE_CAPABILITIES.register(
            StressRecipeCapability.CAP.name, StressRecipeCapability.CAP
        );
        GTRegistries.RECIPE_CAPABILITIES.register(GasRecipeCapability.CAP.name, GasRecipeCapability.CAP);
    }
}
