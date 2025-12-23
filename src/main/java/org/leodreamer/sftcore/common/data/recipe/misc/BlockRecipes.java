package org.leodreamer.sftcore.common.data.recipe.misc;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.common.data.machine.SFTPartMachines;
import org.leodreamer.sftcore.common.data.recipe.utils.SFTVanillaRecipeHelper;

import com.gregtechceu.gtceu.api.data.chemical.material.stack.MaterialEntry;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.frameGt;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.plateDouble;
import static com.gregtechceu.gtceu.common.data.GTItems.ELECTRIC_MOTOR_MV;
import static com.gregtechceu.gtceu.common.data.GTItems.ROBOT_ARM_MV;
import static com.gregtechceu.gtceu.common.data.GTMachines.HULL;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Aluminium;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ASSEMBLER_RECIPES;
import static org.leodreamer.sftcore.common.data.SFTBlocks.MULTI_FUNCTIONAL_CASING;

public final class BlockRecipes {

    public static void init(Consumer<FinishedRecipe> provider) {
        SFTVanillaRecipeHelper.addShapedRecipe("multi_functional_casing_crafting")
            .pattern("BhB", "BAB", "BCB")
            .arg('A', new MaterialEntry(frameGt, Aluminium))
            .arg('B', new MaterialEntry(plateDouble, Aluminium))
            .arg('C', ROBOT_ARM_MV)
            .output(MULTI_FUNCTIONAL_CASING.asStack(3))
            .save(provider);

        ASSEMBLER_RECIPES
            .recipeBuilder(SFTCore.id("multi_functional_casing_assembler"))
            .outputItems(MULTI_FUNCTIONAL_CASING, 3)
            .inputItems(plateDouble, Aluminium, 6)
            .inputItems(frameGt, Aluminium, 1)
            .inputItems(ROBOT_ARM_MV)
            .circuitMeta(6)
            .duration(90)
            .EUt(VA[MV])
            .save(provider);

        SFTVanillaRecipeHelper.addShapedRecipe("machine_adjustment_hatch")
            .pattern(" A ", " B ", " C ")
            .arg('A', Items.CHEST)
            .arg('B', HULL[LV])
            .arg('C', ELECTRIC_MOTOR_MV)
            .output(SFTPartMachines.MACHINE_ADJUSTMENT.asStack())
            .save(provider);
    }
}
