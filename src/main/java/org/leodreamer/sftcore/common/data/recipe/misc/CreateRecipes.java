package org.leodreamer.sftcore.common.data.recipe.misc;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.common.data.machine.SFTPartMachines;
import org.leodreamer.sftcore.common.data.recipe.utils.SFTVanillaRecipeHelper;

import com.gregtechceu.gtceu.data.recipe.CustomTags;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;

import java.util.Locale;
import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.*;

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

        ASSEMBLER_RECIPES.recipeBuilder(SFTCore.id("creative_motor"))
            .outputItems(AllBlocks.CREATIVE_MOTOR.asItem())
            .inputItems(AllBlocks.FLUID_TANK.asItem(), 64)
            .inputItems(AllBlocks.FLUID_TANK.asItem(), 64)
            .inputItems(AllBlocks.BLAZE_BURNER.asItem(), 64)
            .inputItems(AllBlocks.STEAM_ENGINE.asItem(), 64)
            .inputItems(AllBlocks.MECHANICAL_ARM.asItem(), 8)
            .inputItems(lavaCell)
            .duration(1500)
            .EUt(VA[MV])
            .save(provider);

        ALLOY_SMELTER_RECIPES.recipeBuilder(SFTCore.id("andesite_alloy_iron_gt"))
            .outputItems(AllItems.ANDESITE_ALLOY)
            .inputItems(nugget, Iron)
            .inputItems(Items.ANDESITE)
            .duration(20)
            .EUt(VA[LV])
            .save(provider);

        ALLOY_SMELTER_RECIPES.recipeBuilder(SFTCore.id("andesite_alloy_zinc_gt"))
            .outputItems(AllItems.ANDESITE_ALLOY)
            .inputItems(nugget, Zinc)
            .inputItems(Items.ANDESITE)
            .duration(20)
            .EUt(VA[LV])
            .save(provider);

        ASSEMBLER_RECIPES.recipeBuilder(SFTCore.id("andesite_casing_gt"))
            .outputItems(AllBlocks.ANDESITE_CASING.asItem())
            .inputItems(AllItems.ANDESITE_ALLOY)
            .inputItems(ItemTags.LOGS)
            .circuitMeta(1)
            .duration(20)
            .EUt(VA[LV])
            .save(provider);

        ASSEMBLER_RECIPES.recipeBuilder(SFTCore.id("copper_casing_gt"))
            .outputItems(AllBlocks.COPPER_CASING.asItem())
            .inputItems(ingot, Copper)
            .inputItems(ItemTags.LOGS)
            .circuitMeta(1)
            .duration(20)
            .EUt(VA[LV])
            .save(provider);

        ASSEMBLER_RECIPES.recipeBuilder(SFTCore.id("brass_casing_gt"))
            .outputItems(AllBlocks.BRASS_CASING.asItem())
            .inputItems(ingot, Brass)
            .inputItems(ItemTags.LOGS)
            .circuitMeta(1)
            .duration(20)
            .EUt(VA[LV])
            .save(provider);

        ASSEMBLER_RECIPES.recipeBuilder(SFTCore.id("railway_casing_gt"))
            .outputItems(AllBlocks.RAILWAY_CASING.asItem())
            .inputItems(plate, Obsidian)
            .inputItems(AllBlocks.BRASS_CASING.asItem())
            .circuitMeta(1)
            .duration(20)
            .EUt(VA[LV])
            .save(provider);
    }
}
