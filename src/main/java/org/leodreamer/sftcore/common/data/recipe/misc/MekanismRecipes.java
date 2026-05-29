package org.leodreamer.sftcore.common.data.recipe.misc;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.common.data.recipe.builder.SFTRecipeBuilder;
import org.leodreamer.sftcore.common.data.recipe.utils.SFTVanillaRecipeHelper;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;

import com.simibubi.create.AllItems;
import mekanism.common.registries.MekanismFluids;
import mekanism.common.registries.MekanismGases;
import mekanism.common.registries.MekanismItems;
import mekanism.common.tags.MekanismTags;
import mekanism.generators.common.registries.GeneratorsBlocks;
import mekanism.generators.common.registries.GeneratorsGases;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.CHEMICAL_RECIPES;
import static org.leodreamer.sftcore.common.data.recipe.SFTRecipeTypes.MEKANISM_NUCLEAR_REACTION_RECIPES;
import static org.leodreamer.sftcore.common.data.recipe.SFTRecipeTypes.MEKANISM_PROCESSING_RECIPES;

public final class MekanismRecipes {

    public static void init(Consumer<FinishedRecipe> provider) {
        SFTVanillaRecipeHelper.addShapedRecipe("turbine_casing")
            .pattern(" S ", "STS", " S ")
            .arg('S', MekanismTags.Items.INGOTS_STEEL)
            .arg('T', AllItems.STURDY_SHEET)
            .output(GeneratorsBlocks.TURBINE_CASING, 4)
            .save(provider);

        MEKANISM_PROCESSING_RECIPES
            .recipeBuilder(SFTCore.id("infuse/steel"))
            .outputItems(MekanismItems.STEEL_INGOT, 4)
            .inputItems(ingot, Iron, 4)
            .inputItems(gem, Coal)
            .circuitMeta(1)
            .duration(60)
            .EUt(VA[LV])
            .save(provider);

        MEKANISM_PROCESSING_RECIPES
            .recipeBuilder(SFTCore.id("infuse/basic_control_circuit"))
            .outputItems(MekanismItems.BASIC_CONTROL_CIRCUIT, 4)
            .inputItems(ingot, Osmium, 4)
            .inputItems(dust, Redstone)
            .circuitMeta(1)
            .duration(30)
            .EUt(VA[LV])
            .save(provider);

        MEKANISM_PROCESSING_RECIPES
            .recipeBuilder(SFTCore.id("infuse/alloy_infused"))
            .outputItems(MekanismItems.INFUSED_ALLOY, 8)
            .inputItems(ingot, Iron, 8)
            .inputItems(dust, Redstone)
            .circuitMeta(1)
            .duration(60)
            .EUt(VA[LV])
            .save(provider);

        MEKANISM_PROCESSING_RECIPES
            .recipeBuilder(SFTCore.id("infuse/alloy_reinforced"))
            .outputItems(MekanismItems.REINFORCED_ALLOY, 4)
            .inputItems(MekanismItems.INFUSED_ALLOY, 4)
            .inputItems(gem, Diamond)
            .circuitMeta(1)
            .duration(30)
            .EUt(VA[LV])
            .save(provider);

        MEKANISM_PROCESSING_RECIPES
            .recipeBuilder(SFTCore.id("infuse/dust_refined_obsidian"))
            .outputItems(MekanismItems.REFINED_OBSIDIAN_DUST, 8)
            .inputItems(dust, Obsidian, 8)
            .inputItems(gem, Diamond)
            .circuitMeta(1)
            .duration(60)
            .EUt(VA[MV])
            .save(provider);

        MEKANISM_PROCESSING_RECIPES
            .recipeBuilder(SFTCore.id("infuse/alloy_atomic"))
            .outputItems(MekanismItems.ATOMIC_ALLOY, 2)
            .inputItems(MekanismItems.REINFORCED_ALLOY, 2)
            .inputItems(MekanismItems.REFINED_OBSIDIAN_DUST)
            .circuitMeta(1)
            .duration(15)
            .EUt(VA[MV])
            .save(provider);

        MEKANISM_PROCESSING_RECIPES
            .recipeBuilder(SFTCore.id("infuse/slime_ball_1"))
            .outputItems(Items.SLIME_BALL, 4)
            .inputItems(block, Clay)
            .inputItems(MekanismItems.BIO_FUEL, 8)
            .circuitMeta(1)
            .duration(15)
            .EUt(VA[LV])
            .save(provider);

        MEKANISM_PROCESSING_RECIPES
            .recipeBuilder(SFTCore.id("infuse/slime_ball_2"))
            .outputItems(Items.SLIME_BALL)
            .inputItems(ingot, Clay)
            .inputItems(MekanismItems.BIO_FUEL, 2)
            .circuitMeta(1)
            .duration(15)
            .EUt(VA[LV])
            .save(provider);

        MEKANISM_PROCESSING_RECIPES
            .recipeBuilder(SFTCore.id("infuse/bronze_1"))
            .outputItems(MekanismItems.BRONZE_INGOT, 32)
            .inputItems(ingot, Tin)
            .inputItems(ingot, Copper, 24)
            .circuitMeta(1)
            .duration(60)
            .EUt(VA[LV])
            .save(provider);

        MEKANISM_PROCESSING_RECIPES
            .recipeBuilder(SFTCore.id("infuse/bronze_2"))
            .outputItems(MekanismItems.BRONZE_INGOT, 32)
            .inputItems(dust, Tin)
            .inputItems(ingot, Copper, 24)
            .circuitMeta(1)
            .duration(60)
            .EUt(VA[LV])
            .save(provider);

        MEKANISM_PROCESSING_RECIPES
            .recipeBuilder(SFTCore.id("infuse/bronze_3"))
            .outputItems(MekanismItems.BRONZE_DUST, 32)
            .inputItems(ingot, Tin)
            .inputItems(dust, Copper, 24)
            .circuitMeta(1)
            .duration(60)
            .EUt(VA[LV])
            .save(provider);

        MEKANISM_PROCESSING_RECIPES
            .recipeBuilder(SFTCore.id("infuse/bronze_4"))
            .outputItems(MekanismItems.BRONZE_INGOT, 32)
            .inputItems(dust, Tin)
            .inputItems(dust, Copper, 24)
            .circuitMeta(1)
            .duration(60)
            .EUt(VA[LV])
            .save(provider);

        MEKANISM_PROCESSING_RECIPES
            .recipeBuilder(SFTCore.id("compress/refined_obsidian"))
            .outputItems(MekanismItems.REFINED_OBSIDIAN_INGOT, 2)
            .inputItems(MekanismItems.REFINED_OBSIDIAN_DUST, 2)
            .inputItems(ingot, Osmium)
            .circuitMeta(2)
            .duration(30)
            .EUt(VA[MV])
            .save(provider);

        MEKANISM_PROCESSING_RECIPES
            .recipeBuilder(SFTCore.id("compress/refined_glowstone"))
            .outputItems(MekanismItems.REFINED_GLOWSTONE_INGOT, 2)
            .inputItems(dust, Glowstone, 2)
            .inputItems(ingot, Osmium)
            .circuitMeta(2)
            .duration(30)
            .EUt(VA[MV])
            .save(provider);

        MEKANISM_PROCESSING_RECIPES
            .recipeBuilder(SFTCore.id("enrich/obsidian"))
            .outputItems(MekanismItems.OBSIDIAN_DUST, 4)
            .inputItems(Blocks.OBSIDIAN.asItem())
            .circuitMeta(3)
            .duration(15)
            .EUt(VA[LV])
            .save(provider);

        MEKANISM_PROCESSING_RECIPES
            .recipeBuilder(SFTCore.id("enrich/flint"))
            .outputItems(Items.GRAVEL)
            .inputItems(gem, Flint)
            .circuitMeta(3)
            .duration(15)
            .EUt(VA[LV])
            .save(provider);

        SFTRecipeBuilder.of("pellet/polonium", MEKANISM_PROCESSING_RECIPES)
            .outputItems(MekanismItems.POLONIUM_PELLET)
            .inputGas(MekanismGases.POLONIUM, 1000)
            .circuitMeta(4)
            .duration(120)
            .EUt(VA[HV])
            .save(provider);

        SFTRecipeBuilder.of("pellet/plutonium", MEKANISM_PROCESSING_RECIPES)
            .outputItems(MekanismItems.PLUTONIUM_PELLET)
            .inputGas(MekanismGases.PLUTONIUM, 1000)
            .circuitMeta(4)
            .duration(120)
            .EUt(VA[HV])
            .save(provider);

        SFTRecipeBuilder.of("pellet/antimatter", MEKANISM_PROCESSING_RECIPES)
            .outputItems(MekanismItems.ANTIMATTER_PELLET)
            .inputGas(MekanismGases.POLONIUM, 32000)
            .circuitMeta(5)
            .duration(300)
            .EUt(VA[HV])
            .save(provider);

        MEKANISM_PROCESSING_RECIPES
            .recipeBuilder(SFTCore.id("crush/gunpowder"))
            .outputItems(Items.GUNPOWDER)
            .inputItems(gem, Flint)
            .circuitMeta(6)
            .duration(15)
            .EUt(VA[LV])
            .save(provider);

        SFTRecipeBuilder.of("chemical/fusion_fuel", MEKANISM_PROCESSING_RECIPES)
            .outputGas(GeneratorsGases.FUSION_FUEL, 2000)
            .inputGas(GeneratorsGases.DEUTERIUM, 1000)
            .inputGas(GeneratorsGases.TRITIUM, 1000)
            .circuitMeta(6)
            .duration(600)
            .EUt(VA[MV])
            .save(provider);

        SFTRecipeBuilder.of("chemical/energetic_fissile_fuel", MEKANISM_PROCESSING_RECIPES)
            .outputGas(MekanismGases.FISSILE_FUEL, 8000)
            .inputFluids(UraniumHexafluoride.getFluid(8000))
            .notConsumable(MekanismItems.ANTIMATTER_PELLET)
            .circuitMeta(6)
            .duration(300)
            .EUt(VA[MV])
            .save(provider);

        var bioFuel = bioFuel(provider);

        bioFuel.accept(Items.MOSS_BLOCK, 5);
        bioFuel.accept(Items.MELON_SLICE, 4);
        bioFuel.accept(Items.SUGAR_CANE, 4);
        bioFuel.accept(Items.CACTUS, 4);
        bioFuel.accept(ItemTags.FLOWERS, 5);
        bioFuel.accept(ItemTags.LEAVES, 2);
        bioFuel.accept(ItemTags.SAPLINGS, 2);
        bioFuel.accept(ItemTags.WART_BLOCKS, 7);
        bioFuel.accept(Tags.Items.SEEDS, 2);
        bioFuel.accept(Tags.Items.CROPS, 5);

        CHEMICAL_RECIPES
            .recipeBuilder(SFTCore.id("uranium_hexafluoride"))
            .outputFluids(MekanismFluids.URANIUM_HEXAFLUORIDE.getFluidStack(2000))
            .inputItems(MekanismItems.FLUORITE_GEM)
            .inputItems(ingot, Uranium238, 2)
            .inputFluids(Oxygen.getFluid(200))
            .inputFluids(SulfuricAcid.getFluid(100))
            .duration(300)
            .EUt(VA[MV])
            .save(provider);

        SFTRecipeBuilder.of("fusion", MEKANISM_NUCLEAR_REACTION_RECIPES)
            .inputGas(GeneratorsGases.FUSION_FUEL, 1)
            .duration(4000)
            .EUt(-V[MV])
            .save(provider);

        SFTRecipeBuilder.of("fission_1", MEKANISM_NUCLEAR_REACTION_RECIPES)
            .inputGas(MekanismGases.FISSILE_FUEL, 700)
            .outputGas(MekanismGases.PLUTONIUM, 100)
            .circuitMeta(1)
            .duration(111000)
            .EUt(-V[MV])
            .save(provider);

        SFTRecipeBuilder.of("fission_1_fast", MEKANISM_NUCLEAR_REACTION_RECIPES)
            .inputGas(MekanismGases.FISSILE_FUEL, 7000)
            .outputGas(MekanismGases.PLUTONIUM, 3000)
            .circuitMeta(4)
            .duration(9700)
            .EUt(-V[MV])
            .save(provider);

        SFTRecipeBuilder.of("fission_2", MEKANISM_NUCLEAR_REACTION_RECIPES)
            .inputGas(MekanismGases.FISSILE_FUEL, 700)
            .outputGas(MekanismGases.POLONIUM, 100)
            .circuitMeta(2)
            .duration(111000)
            .EUt(-V[MV])
            .save(provider);

        SFTRecipeBuilder.of("fission_2_fast", MEKANISM_NUCLEAR_REACTION_RECIPES)
            .inputGas(MekanismGases.FISSILE_FUEL, 7000)
            .outputGas(MekanismGases.POLONIUM, 3000)
            .circuitMeta(5)
            .duration(9700)
            .EUt(-V[MV])
            .save(provider);

        SFTRecipeBuilder.of("fission_3", MEKANISM_NUCLEAR_REACTION_RECIPES)
            .inputGas(MekanismGases.FISSILE_FUEL, 700)
            .outputGas(MekanismGases.FISSILE_FUEL, 500)
            .circuitMeta(3)
            .duration(111000)
            .EUt(-V[MV])
            .save(provider);
    }

    private static @NotNull BiConsumer<Object, Integer> bioFuel(Consumer<FinishedRecipe> provider) {
        AtomicInteger id = new AtomicInteger(0);

        return (ingredient, amount) -> {
            id.getAndIncrement();
            MEKANISM_PROCESSING_RECIPES
                .recipeBuilder(SFTCore.id("crush/bio_fuel_" + id))
                .outputItems(MekanismItems.BIO_FUEL, amount)
                .inputItems(ingredient)
                .circuitMeta(6)
                .duration(5)
                .EUt(VA[MV])
                .save(provider);
        };
    }
}
