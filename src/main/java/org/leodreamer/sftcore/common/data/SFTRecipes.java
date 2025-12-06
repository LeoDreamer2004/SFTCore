package org.leodreamer.sftcore.common.data;

import net.minecraft.data.recipes.FinishedRecipe;
import org.leodreamer.sftcore.common.data.recipe.misc.*;

import java.util.function.Consumer;

public class SFTRecipes {
    public static void init(Consumer<FinishedRecipe> provider) {
        VanillaRecipes.init(provider);
        CommonGTRecipes.init(provider);
        CustomGTRecipes.init(provider);
        BlockRecipes.init(provider);
        SemiconductorRecipes.init(provider);
        MekanismRecipes.init(provider);
        AE2Recipes.init(provider);
        ControllerRecipes.init(provider);
        GTMTRecipes.init(provider);
    }
}
