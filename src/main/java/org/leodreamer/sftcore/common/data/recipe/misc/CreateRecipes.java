package org.leodreamer.sftcore.common.data.recipe.misc;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.common.data.machine.SFTPartMachines;
import org.leodreamer.sftcore.common.data.recipe.utils.SFTVanillaRecipeHelper;

import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.data.recipe.CustomTags;

import net.minecraft.data.recipes.FinishedRecipe;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;

import java.util.Locale;
import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.GTValues.*;

public final class CreateRecipes {

    public static void init(Consumer<FinishedRecipe> provider) {
        for (int tier : tiersBetween(LV, EV)) {
            SFTVanillaRecipeHelper.addShapedRecipe(VN[tier].toLowerCase(Locale.ROOT) + "_kinetic_input")
                .pattern("CIC", "DUD", "CIC")
                .arg('C', AllItems.PRECISION_MECHANISM)
                .arg(
                    'D',
                    tier == LV ? AllBlocks.SHAFT.asStack() :
                        SFTPartMachines.KINETIC_INPUT_BOX[tier - 1].asStack()
                )
                .arg('I', CustomTags.CIRCUITS_ARRAY[tier - 1])
                .arg('U', AllBlocks.BRASS_CASING)
                .output(SFTPartMachines.KINETIC_INPUT_BOX[tier].asStack())
                .save(provider);
        }

        var lavaCell = AE2Recipes.getInfinityCell('f', "minecraft:lava");

        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(SFTCore.id("creative_motor"))
            .inputItems(AllBlocks.FLUID_TANK.asItem(), 64)
            .inputItems(AllBlocks.FLUID_TANK.asItem(), 64)
            .inputItems(AllBlocks.BLAZE_BURNER.asItem(), 64)
            .inputItems(AllBlocks.STEAM_ENGINE.asItem(), 64)
            .inputItems(AllBlocks.MECHANICAL_ARM.asItem(), 8)
            .inputItems(lavaCell)
            .outputItems(AllBlocks.CREATIVE_MOTOR)
            .duration(1500)
            .EUt(VA[MV])
            .save(provider);
    }
}
