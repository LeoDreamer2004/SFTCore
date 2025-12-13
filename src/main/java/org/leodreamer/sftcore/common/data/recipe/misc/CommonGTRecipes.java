package org.leodreamer.sftcore.common.data.recipe.misc;

import appeng.core.definitions.AEParts;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.stack.MaterialEntry;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.machines.GTMultiMachines;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;
import com.hepdd.gtmthings.data.CustomItems;
import com.simibubi.create.AllItems;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismItems;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Item;
import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.common.data.SFTItems;
import org.leodreamer.sftcore.common.data.machine.SFTPartMachines;
import org.leodreamer.sftcore.common.data.recipe.utils.SFTVanillaRecipeHelper;

import java.util.Locale;
import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.*;
import static com.gregtechceu.gtceu.common.data.GTItems.ROBOT_ARM_HV;
import static com.gregtechceu.gtceu.common.data.GTMachines.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ASSEMBLER_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.PACKER_RECIPES;
import static com.gregtechceu.gtceu.data.recipe.GTCraftingComponents.FRAME;
import static com.gregtechceu.gtceu.data.recipe.GTCraftingComponents.PIPE_NORMAL;
import static net.minecraft.world.item.Items.*;
import static org.leodreamer.sftcore.common.data.recipe.SFTRecipeTypes.*;

public final class CommonGTRecipes {
    public static void init(Consumer<FinishedRecipe> provider) {
        transitionStageRecipes(provider);
        desulfurizeRecipes(provider);
        oilDrillingRigRecipes(provider);
        greenhouseRecipes(provider);
        universalCircuitRecipes(provider);
        largeGasCollectorRecipes(provider);
        dualHatchRecipes(provider);
    }

    private static void transitionStageRecipes(Consumer<FinishedRecipe> provider) {
        SFTVanillaRecipeHelper.addShapedRecipe("ulv_input_bus")
                .pattern("ABA", "ACA", "AAA")
                .arg('A', new MaterialEntry(block, Lead))
                .arg('B', MekanismBlocks.ULTIMATE_BIN)
                .arg('C', MekanismItems.ULTIMATE_CONTROL_CIRCUIT)
                .output(ITEM_IMPORT_BUS[ULV].asStack())
                .save(provider);

        SFTVanillaRecipeHelper.addShapedRecipe("ulv_output_bus")
                .pattern("AAA", "ACA", "ABA")
                .arg('A', new MaterialEntry(block, Lead))
                .arg('B', MekanismBlocks.ULTIMATE_BIN)
                .arg('C', MekanismItems.ULTIMATE_CONTROL_CIRCUIT)
                .output(ITEM_EXPORT_BUS[ULV].asStack())
                .save(provider);

        SFTVanillaRecipeHelper.addShapedRecipe("ulv_input_hatch")
                .pattern("ABA", "ACA", "AAA")
                .arg('A', new MaterialEntry(block, Lead))
                .arg('B', MekanismBlocks.ULTIMATE_FLUID_TANK)
                .arg('C', MekanismItems.ULTIMATE_CONTROL_CIRCUIT)
                .output(FLUID_IMPORT_HATCH[ULV].asStack())
                .save(provider);

        SFTVanillaRecipeHelper.addShapedRecipe("ulv_output_hatch")
                .pattern("AAA", "ACA", "ABA")
                .arg('A', new MaterialEntry(block, Lead))
                .arg('B', MekanismBlocks.ULTIMATE_FLUID_TANK)
                .arg('C', MekanismItems.ULTIMATE_CONTROL_CIRCUIT)
                .output(FLUID_EXPORT_HATCH[ULV].asStack())
                .save(provider);

        SFTVanillaRecipeHelper.addShapedRecipe("lv_machine_hull")
                .pattern("   ", "ABA", "CDC")
                .arg('A', new MaterialEntry(plate, Nickel))
                .arg('B', new MaterialEntry(plate, Steel))
                .arg('C', new MaterialEntry(cableGtSingle, Tin))
                .arg('D', GTBlocks.MACHINE_CASING_LV)
                .output(HULL[LV].asStack())
                .save(provider);

        SFTVanillaRecipeHelper.addShapedRecipe("cleaning_maintenance_hatch")
                .pattern("ABA", "CDC", "ABA")
                .arg('A', AUTO_MAINTENANCE_HATCH)
                .arg('B', CustomTags.EV_CIRCUITS)
                .arg('C', ROBOT_ARM_HV)
                .arg('D', HULL[HV])
                .output(CLEANING_MAINTENANCE_HATCH.asStack())
                .save(provider);

        SFTVanillaRecipeHelper.addShapedRecipe("terminal")
                .pattern("AAA", "ABA", "AAA")
                .arg('A', AllItems.PRECISION_MECHANISM)
                .arg('B', AEParts.TERMINAL)
                .output(GTItems.TERMINAL.asStack())
                .save(provider);

        SFTVanillaRecipeHelper.addShapedRecipe("advanced_terminal")
                .pattern("AAA", "CBC", "AAA")
                .arg('A', DIAMOND_BLOCK)
                .arg('B', GTItems.TERMINAL)
                .arg('C', CustomTags.MV_CIRCUITS)
                .output(CustomItems.ADVANCED_TERMINAL.asStack())
                .save(provider);
    }

    private static void desulfurizeRecipes(Consumer<FinishedRecipe> provider) {
        desulfurize(provider, "heavy_fuel", SulfuricHeavyFuel, HeavyFuel);
        desulfurize(provider, "light_fuel", SulfuricLightFuel, LightFuel);
        desulfurize(provider, "naphtha", SulfuricNaphtha, Naphtha);
        desulfurize(provider, "gas", SulfuricGas, RefineryGas);
    }

    private static void desulfurize(Consumer<FinishedRecipe> provider, String id, Material input, Material output) {
        DESULFURIZE_RECIPES.recipeBuilder(SFTCore.id(id))
                .notConsumable(dust, ActivatedCarbon, 8)
                .inputFluids(input.getFluid(32000))
                .outputItems(dust, Sulfur, 32)
                .outputFluids(output.getFluid(40000))
                .duration(900)
                .EUt(VA[HV])

                .save(provider);
    }

    private static void oilDrillingRigRecipes(Consumer<FinishedRecipe> provider) {
        oilDrillingRig(provider, 1, Oil);
        oilDrillingRig(provider, 2, OilHeavy);
        oilDrillingRig(provider, 3, RawOil);
        oilDrillingRig(provider, 4, OilLight);
    }

    private static void oilDrillingRig(Consumer<FinishedRecipe> provider, int circuit, Material output) {
        OIL_DRILLING_RECIPES.recipeBuilder(SFTCore.id("oil_" + circuit))
                .outputFluids(output.getFluid(2000))
                .chancedOutput(dust, Oilsands, 1000, 1000)
                .inputFluids(Lubricant.getFluid(200))
                .circuitMeta(circuit)
                .duration(120)
                .EUt(VA[MV])
                .save(provider);
    }

    private static void greenhouseRecipes(Consumer<FinishedRecipe> provider) {
        greenhouse(provider, "rubber", GTBlocks.RUBBER_SAPLING.get().asItem(), false,
                GTBlocks.RUBBER_LOG.asItem(), 32, GTItems.STICKY_RESIN.asItem(), 8, GTBlocks.RUBBER_SAPLING.asItem(), 4);
        greenhouse(provider, "rubber_boost", GTBlocks.RUBBER_SAPLING.get().asItem(), true,
                GTBlocks.RUBBER_LOG.asItem(), 64, GTItems.STICKY_RESIN.asItem(), 16, GTBlocks.RUBBER_SAPLING.asItem(), 4);
        greenhouse(provider, "oak", OAK_SAPLING, false,
                OAK_LOG, 64, OAK_SAPLING, 4);
        greenhouse(provider, "oak_boost", OAK_SAPLING, true,
                OAK_LOG, 64, OAK_LOG, 64, OAK_SAPLING, 4);
        greenhouse(provider, "birch", BIRCH_SAPLING, false,
                BIRCH_LOG, 64, BIRCH_SAPLING, 4);
        greenhouse(provider, "birch_boost", BIRCH_SAPLING, true,
                BIRCH_LOG, 64, BIRCH_LOG, 64, BIRCH_SAPLING, 4);
        greenhouse(provider, "dark_oak", DARK_OAK_SAPLING, false,
                DARK_OAK_LOG, 64, DARK_OAK_SAPLING, 4);
        greenhouse(provider, "dark_oak_boost", DARK_OAK_SAPLING, true,
                DARK_OAK_LOG, 64, DARK_OAK_LOG, 64, DARK_OAK_SAPLING, 4);
        greenhouse(provider, "spruce", SPRUCE_SAPLING, false,
                SPRUCE_LOG, 64, SPRUCE_SAPLING, 4);
        greenhouse(provider, "spruce_boost", SPRUCE_SAPLING, true,
                SPRUCE_LOG, 64, SPRUCE_LOG, 64, SPRUCE_SAPLING, 4);
        greenhouse(provider, "jungle", JUNGLE_SAPLING, false,
                JUNGLE_LOG, 64, JUNGLE_SAPLING, 4);
        greenhouse(provider, "jungle_boost", JUNGLE_SAPLING, true,
                JUNGLE_LOG, 64, JUNGLE_LOG, 64, JUNGLE_SAPLING, 4);
        greenhouse(provider, "acacia", ACACIA_SAPLING, false,
                ACACIA_LOG, 64, ACACIA_SAPLING, 4);
        greenhouse(provider, "acacia_boost", ACACIA_SAPLING, true,
                ACACIA_LOG, 64, ACACIA_LOG, 64, ACACIA_SAPLING, 4);
        greenhouse(provider, "cherry", CHERRY_SAPLING, false,
                CHERRY_LOG, 64, CHERRY_SAPLING, 4);
        greenhouse(provider, "cherry_boost", CHERRY_SAPLING, true,
                CHERRY_LOG, 64, CHERRY_LOG, 64, CHERRY_SAPLING, 4);
        greenhouse(provider, "mangrove", MANGROVE_PROPAGULE, false,
                MANGROVE_LOG, 64, MANGROVE_PROPAGULE, 4);
        greenhouse(provider, "mangrove_boost", MANGROVE_PROPAGULE, true,
                MANGROVE_LOG, 64, MANGROVE_LOG, 64, MANGROVE_PROPAGULE, 4);

        greenhouse(provider, "sugar_cane", SUGAR_CANE, false,
                SUGAR_CANE, 24);
        greenhouse(provider, "sugar_cane", SUGAR_CANE, true,
                SUGAR_CANE, 48);
        greenhouse(provider, "kelp", KELP, false,
                KELP, 24);
        greenhouse(provider, "kelp_boost", KELP, true,
                KELP, 48);
        greenhouse(provider, "bamboo", BAMBOO, false,
                BAMBOO, 24);
        greenhouse(provider, "bamboo_boost", BAMBOO, true,
                BAMBOO, 48);
        greenhouse(provider, "cactus", CACTUS, false,
                CACTUS, 24);
        greenhouse(provider, "cactus_boost", CACTUS, true,
                CACTUS, 48);
        greenhouse(provider, "wheat", WHEAT_SEEDS, false,
                WHEAT, 16, WHEAT_SEEDS, 4);
        greenhouse(provider, "wheat_boost", WHEAT_SEEDS, true,
                WHEAT, 32, WHEAT_SEEDS, 4);
        greenhouse(provider, "potato", POTATO, false,
                POTATO, 24);
        greenhouse(provider, "potato_boost", POTATO, true,
                POTATO, 48);
        greenhouse(provider, "carrot", CARROT, false,
                CARROT, 24);
        greenhouse(provider, "carrot_boost", CARROT, true,
                CARROT, 48);

        greenhouse(provider, "beetroot", BEETROOT_SEEDS, false,
                BEETROOT, 16, BEETROOT_SEEDS, 4);
        greenhouse(provider, "beetroot_boost", BEETROOT_SEEDS, true,
                BEETROOT, 32, BEETROOT_SEEDS, 4);
        greenhouse(provider, "melon", MELON_SEEDS, false,
                MELON, 16, MELON_SEEDS, 4);
        greenhouse(provider, "melon_boost", MELON_SEEDS, true,
                MELON, 32, MELON_SEEDS, 4);
        greenhouse(provider, "pumpkin", PUMPKIN_SEEDS, false,
                PUMPKIN, 16, PUMPKIN_SEEDS, 4);
        greenhouse(provider, "pumpkin_boost", PUMPKIN_SEEDS, true,
                PUMPKIN, 32, PUMPKIN_SEEDS, 4);

        greenhouse(provider, "nether_wart", NETHER_WART, false,
                NETHER_WART, 12);
        greenhouse(provider, "nether_wart_boost", NETHER_WART, true,
                NETHER_WART, 24);
        greenhouse(provider, "red_mushroom", RED_MUSHROOM, false,
                RED_MUSHROOM, 12);
        greenhouse(provider, "red_mushroom_boost", RED_MUSHROOM, true,
                RED_MUSHROOM, 24);
        greenhouse(provider, "brown_mushroom", BROWN_MUSHROOM, false,
                BROWN_MUSHROOM, 12);
    }

    private static void greenhouse(Consumer<FinishedRecipe> provider, String id, Item input, boolean boost, Object... outputs) {
        if (outputs.length % 2 != 0) {
            SFTCore.LOGGER.error("outputs must be even, like item, amount, item, amount");
            return;
        }

        var builder = GREENHOUSE_RECIPES.recipeBuilder(SFTCore.id(id))
                .circuitMeta(boost ? 2 : 1)
                .notConsumable(input)
                .inputFluids(Water.getFluid(1000))
                .duration(boost ? 300 : 600)
                .EUt(VA[MV]);

        for (int i = 0; i < outputs.length; i += 2) {
            if (outputs[i] instanceof Item item && outputs[i + 1] instanceof Integer amount) {
                builder.outputItems(item, amount);
            } else {
                SFTCore.LOGGER.error("Invalid output format: expected Item and Integer, got {} and {}", outputs[i].getClass(), outputs[i + 1].getClass());
                return;
            }
        }

        if (boost)
            builder.inputItems(BONE_MEAL, 4);
        builder
                .save(provider);
    }

    private static void universalCircuitRecipes(Consumer<FinishedRecipe> provider) {
        for (var voltage : GTValues.tiersBetween(ULV, UHV)) {
            var tag = CustomTags.CIRCUITS_ARRAY[voltage];
            var circuit = SFTItems.UNIVERSAL_CIRCUITS.get(voltage);
            var name = VN[voltage] + "_universal_circuit";

            // crafting
            VanillaRecipeHelper.addShapelessRecipe(provider, SFTCore.id(name + "_by_crafting"), circuit.asStack(), tag);

            // packer
            PACKER_RECIPES.recipeBuilder(SFTCore.id(name + "_by_assembling"))
                    .inputItems(tag)
                    .outputItems(circuit)
                    .EUt(VA[ULV])
                    .duration(60)
                    .save(provider);
        }
    }

    private static void largeGasCollectorRecipes(Consumer<FinishedRecipe> provider) {
        gasCollector(provider, "air", GRASS_BLOCK, Air, LV, false);
        gasCollector(provider, "liquid_air", GRASS_BLOCK, LiquidAir, HV, true);
        gasCollector(provider, "nether_air", NETHERRACK, NetherAir, MV, false);
        gasCollector(provider, "liquid_nether_air", NETHERRACK, LiquidNetherAir, EV, true);
        gasCollector(provider, "ender_air", END_STONE, EnderAir, HV, false);
        gasCollector(provider, "liquid_ender_air", END_STONE, LiquidEnderAir, IV, true);
    }

    private static void gasCollector(Consumer<FinishedRecipe> provider, String id, Item input, Material output, int tier, boolean freeze) {
        var builder = LARGE_GAS_COLLECTOR_RECIPES.recipeBuilder(id)
                .notConsumable(input)
                .outputFluids(output.getFluid(100000))
                .duration(freeze ? 2000 : 1800)
                .EUt(VA[tier]);
        if (freeze)
            builder.notConsumable(GTMultiMachines.VACUUM_FREEZER.asStack());
        else
            builder.circuitMeta(1);
        builder.save(provider);
    }

    private static void dualHatchRecipes(Consumer<FinishedRecipe> provider) {
        Material[] fluidMap = new Material[]{GTMaterials.Glue, GTMaterials.Polyethylene,
                GTMaterials.Polytetrafluoroethylene, GTMaterials.Polybenzimidazole};

        for (var machine : SFTPartMachines.DUAL_IMPORT_HATCH) {
            if (machine == null) continue;
            int tier = machine.getTier();
            int j = Math.min(fluidMap.length - 1, tier / 2);
            for (; j < fluidMap.length; j++) {
                int fluidAmount = GTValues.L * 8 * (tier + 1);
                ASSEMBLER_RECIPES
                        .recipeBuilder(
                                SFTCore.id("dual_import_bus_" + VN[tier].toLowerCase(Locale.ROOT) + "_" + fluidMap[j].getName()))
                        .inputItems(ITEM_IMPORT_BUS[tier])
                        .inputItems(FLUID_IMPORT_HATCH[tier])
                        .inputItems(PIPE_NORMAL.get(tier), 2)
                        .inputItems(FRAME.get(tier), 3)
                        .circuitMeta(1)
                        .inputFluids(fluidMap[j].getFluid(fluidAmount >> j))
                        .outputItems(machine)
                        .duration(300)
                        .EUt(VA[tier])
                        .save(provider);
            }
        }

        for (var machine : SFTPartMachines.DUAL_EXPORT_HATCH) {
            if (machine == null) continue;
            int tier = machine.getTier();
            int j = Math.min(fluidMap.length - 1, tier / 2);
            for (; j < fluidMap.length; j++) {
                int fluidAmount = GTValues.L * 8 * (tier + 1);
                ASSEMBLER_RECIPES
                        .recipeBuilder(
                                SFTCore.id("dual_export_bus_" + VN[tier].toLowerCase(Locale.ROOT) + "_" + fluidMap[j].getName()))
                        .inputItems(ITEM_IMPORT_BUS[tier])
                        .inputItems(FLUID_IMPORT_HATCH[tier])
                        .inputItems(PIPE_NORMAL.get(tier), 2)
                        .inputItems(FRAME.get(tier), 3)
                        .circuitMeta(2)
                        .inputFluids(fluidMap[j].getFluid(fluidAmount >> j))
                        .outputItems(machine)
                        .duration(300)
                        .EUt(VA[tier])
                        .save(provider);
            }
        }
    }
}
