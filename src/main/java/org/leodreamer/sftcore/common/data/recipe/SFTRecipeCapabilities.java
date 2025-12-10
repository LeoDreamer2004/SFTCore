package org.leodreamer.sftcore.common.data.recipe;

import com.gregtechceu.gtceu.api.registry.GTRegistries;
import org.leodreamer.sftcore.api.recipe.capability.StressRecipeCapability;

public class SFTRecipeCapabilities {
    public static void init() {
        GTRegistries.RECIPE_CAPABILITIES.register(StressRecipeCapability.CAP.name, StressRecipeCapability.CAP);
    }
}
