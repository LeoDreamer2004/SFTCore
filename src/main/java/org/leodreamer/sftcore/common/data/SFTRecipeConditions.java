package org.leodreamer.sftcore.common.data;

import org.leodreamer.sftcore.common.recipe.condition.RPMCondition;

import com.gregtechceu.gtceu.api.recipe.condition.RecipeConditionType;
import com.gregtechceu.gtceu.api.registry.GTRegistries;

public class SFTRecipeConditions {

    public static void init() {}

    public static final RecipeConditionType<RPMCondition> RPM = GTRegistries.RECIPE_CONDITIONS.register(
        "rpm", new RecipeConditionType<>(RPMCondition::new, RPMCondition.CODEC)
    );
}
