package org.leodreamer.sftcore.common.data.recipe.misc;

import com.gregtechceu.gtceu.api.data.chemical.material.stack.MaterialEntry;
import com.gregtechceu.gtceu.config.ConfigHolder;
import net.minecraft.data.recipes.FinishedRecipe;
import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.common.data.recipe.utils.SFTVanillaRecipeHelper;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.GTValues.MV;
import static com.gregtechceu.gtceu.api.GTValues.VA;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.*;
import static com.gregtechceu.gtceu.common.data.GTItems.ELECTRIC_MOTOR_MV;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Aluminium;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ASSEMBLER_RECIPES;
import static org.leodreamer.sftcore.common.data.SFTBlocks.MULTI_FUNCTIONAL_CASING;

public final class BlockRecipes {

    public static void init(Consumer<FinishedRecipe> provider) {
        SFTVanillaRecipeHelper.addShapedRecipe("multi_functional_casing_crafting")
                .pattern("BhB", "BAB", "BCB")
                .arg('A', new MaterialEntry(frameGt, Aluminium))
                .arg('B', new MaterialEntry(plateDouble, Aluminium))
                .arg('C', ELECTRIC_MOTOR_MV)
                .output(MULTI_FUNCTIONAL_CASING.asStack(3))
                .save(provider);

        ASSEMBLER_RECIPES.recipeBuilder(SFTCore.id("multi_functional_casing_assembler"))
                .outputItems(MULTI_FUNCTIONAL_CASING, 3)
                .inputItems(plateDouble, Aluminium, 6)
                .inputItems(frameGt, Aluminium, 1)
                .inputItems(ELECTRIC_MOTOR_MV)
                .circuitMeta(6)
                .duration(30)
                .EUt(VA[MV])
                .save(provider);
    }
}
