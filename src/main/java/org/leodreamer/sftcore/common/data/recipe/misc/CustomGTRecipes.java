package org.leodreamer.sftcore.common.data.recipe.misc;

import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.simibubi.create.AllItems;
import mekanism.common.registries.MekanismFluids;
import mekanism.common.registries.MekanismItems;
import mekanism.generators.common.registries.GeneratorsFluids;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;
import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.common.data.SFTItems;
import org.leodreamer.sftcore.common.data.SFTMachines;
import org.leodreamer.sftcore.common.data.SFTMaterials;
import org.leodreamer.sftcore.common.data.SFTRecipes;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.*;
import static com.gregtechceu.gtceu.common.data.GTItems.FIELD_GENERATOR_IV;
import static com.gregtechceu.gtceu.common.data.GTItems.ROBOT_ARM_IV;
import static com.gregtechceu.gtceu.common.data.GTMachines.AUTO_MAINTENANCE_HATCH;
import static com.gregtechceu.gtceu.common.data.GTMachines.CONFIGURABLE_MAINTENANCE_HATCH;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.*;
import static org.leodreamer.sftcore.common.data.recipe.SFTRecipeTypes.FISHBIG_MAKER_RECIPES;
import static org.leodreamer.sftcore.common.data.recipe.SFTRecipeTypes.HURRY_UP_RECIPES;

public final class CustomGTRecipes {
    public static void init(Consumer<FinishedRecipe> provider) {
        CHEMICAL_BATH_RECIPES.recipeBuilder(SFTCore.id("uu_matter"))
                .outputItems(SFTItems.UU_MATTER)
                .inputItems(Items.COBBLESTONE, 2)
                .inputFluids(Lava.getFluid(100))
                .duration(20)
                .EUt(VA[ULV])
                .save(provider);

        var torcherino = SFTRecipes.getItemById("torcherino", "torcherino");
        if (torcherino != null) {
            HURRY_UP_RECIPES.recipeBuilder(SFTCore.id("hurry_up"))
                    .outputItems(torcherino, 2)
                    .inputItems(MekanismItems.ANTIMATTER_PELLET)
                    .inputItems(Items.TORCH, 64)
                    .inputFluids(AquaRegia.getFluid(8000))
                    .duration(1200)
                    .EUt(VA[HV])
                    .save(provider);

            HURRY_UP_RECIPES.recipeBuilder(SFTCore.id("time_bottle"))
                    .outputItems(SFTItems.TIME_BOTTLE)
                    .inputItems(torcherino, 9)
                    .inputItems(GTBlocks.INDUSTRIAL_TNT, 64)
                    .inputFluids(Polyethylene.getFluid(L * 32))
                    .inputFluids(SFTMaterials.FilteredPolonium.getFluid(1000))
                    .duration(2000)
                    .EUt(VA[EV])
                    .save(provider);
        }

        var netherStarBlock = SFTRecipes.getItemById("extendedcrafting", "nether_star_block");

        if (netherStarBlock != null) {
            FISHBIG_MAKER_RECIPES.recipeBuilder(SFTCore.id("fishbig"))
                    .outputItems(EPPItemAndBlock.FISHBIG.asItem())
                    .inputItems(MekanismItems.SUPERMASSIVE_QIO_DRIVE)
                    .inputItems(Items.LIGHT_BLUE_WOOL, 64)
                    .inputItems(AllItems.PRECISION_MECHANISM.get(), 64)
                    .inputItems(AllItems.BAR_OF_CHOCOLATE, 64)
                    .inputItems(netherStarBlock, 64)
                    .inputFluids(GeneratorsFluids.FUSION_FUEL.getFluidStack(8000))
                    .inputFluids(MekanismFluids.SULFURIC_ACID.getFluidStack(8000))
                    .inputFluids(MekanismFluids.ETHENE.getFluidStack(8000))
                    .duration(1000)
                    .EUt(VA[LV])
                    .save(provider);
        }

        FISHBIG_MAKER_RECIPES.recipeBuilder(SFTCore.id("ore_generator"))
                .outputItems(SFTMachines.ORE_REPLICATOR.asStack())
                .notConsumable(EPPItemAndBlock.FISHBIG.asItem())
                .inputItems(MekanismItems.ULTIMATE_CONTROL_CIRCUIT, 4)
                .inputItems(Items.SMOOTH_STONE)
                .inputFluids(GeneratorsFluids.FUSION_FUEL.getFluidStack(4000))
                .duration(100)
                .EUt(VA[LV])
                .save(provider);

        ASSEMBLER_RECIPES.recipeBuilder(SFTCore.id("ore_generator"))
                .outputItems(SFTMachines.ORE_REPLICATOR.asStack())
                .notConsumable(EPPItemAndBlock.FISHBIG.asItem())
                .inputItems(MekanismItems.ULTIMATE_CONTROL_CIRCUIT, 4)
                .inputItems(Items.SMOOTH_STONE)
                .inputFluids(GeneratorsFluids.FUSION_FUEL.getFluidStack(4000))
                .duration(40)
                .EUt(VA[LV])
                .save(provider);

        ASSEMBLY_LINE_RECIPES.recipeBuilder(SFTCore.id("configurable_auto_maintenance_hatch"))
                .outputItems(SFTMachines.AUTO_CONFIGURABLE_MAINTENANCE_HATCH)
                .inputItems(frameGt, HastelloyX)
                .inputItems(AUTO_MAINTENANCE_HATCH)
                .inputItems(CONFIGURABLE_MAINTENANCE_HATCH, 4)
                .inputItems(ROBOT_ARM_IV, 8)
                .inputItems(FIELD_GENERATOR_IV, 4)
                .inputItems(plateDouble, VanadiumGallium, 8)
                .inputItems(bolt, Ruridit, 16)
                .inputItems(plate, TungstenSteel, 16)
                .inputItems(foil, TungstenSteel, 64)
                .inputFluids(Polybenzimidazole.getFluid(L * 8))
                .inputFluids(Lubricant.getFluid(4000))
                .scannerResearch(b -> b
                        .researchStack(AUTO_MAINTENANCE_HATCH.asStack())
                        .duration(800)
                        .EUt(VA[IV]))
                .duration(300)
                .EUt(VA[LuV])
                .save(provider);
    }
}
