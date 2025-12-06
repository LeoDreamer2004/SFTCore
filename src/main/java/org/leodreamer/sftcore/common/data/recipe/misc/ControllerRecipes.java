package org.leodreamer.sftcore.common.data.recipe.misc;

import appeng.core.definitions.AEBlocks;
import com.gregtechceu.gtceu.api.data.chemical.material.stack.MaterialEntry;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismItems;
import mekanism.generators.common.registries.GeneratorsBlocks;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;
import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.common.data.machine.SFTMultiMachines;
import org.leodreamer.sftcore.common.data.recipe.utils.SFTVanillaRecipeHelper;
import org.leodreamer.sftcore.integration.IntegrateMods;
import org.leodreamer.sftcore.integration.IntegrateUtils;

import java.util.Objects;
import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.*;
import static com.gregtechceu.gtceu.common.data.GTBlocks.FILTER_CASING;
import static com.gregtechceu.gtceu.common.data.GTItems.*;
import static com.gregtechceu.gtceu.common.data.GTMachines.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;
import static com.gregtechceu.gtceu.common.data.machines.GCYMMachines.*;
import static com.gregtechceu.gtceu.common.data.machines.GTMultiMachines.*;
import static org.leodreamer.sftcore.common.data.SFTBlocks.MULTI_FUNCTIONAL_CASING;
import static org.leodreamer.sftcore.common.data.machine.SFTMultiMachines.*;

public final class ControllerRecipes {
    public static void init(Consumer<FinishedRecipe> provider) {
        SFTVanillaRecipeHelper.addShapedRecipe("mega_blast_furnace")
                .pattern("ABA", "CDC", "EFE")
                .arg('A', new MaterialEntry(spring, Naquadah))
                .arg('B', CustomTags.ZPM_CIRCUITS)
                .arg('C', FIELD_GENERATOR_ZPM)
                .arg('D', ELECTRIC_BLAST_FURNACE)
                .arg('E', new MaterialEntry(plateDense, NaquadahAlloy))
                .arg('F', new MaterialEntry(wireGtQuadruple, UraniumRhodiumDinaquadide))
                .output(MEGA_BLAST_FURNACE.asStack())
                .save(provider);

        SFTVanillaRecipeHelper.addShapedRecipe("mega_vacuum_freezer")
                .pattern("ABA", "CDC", "EFE")
                .arg('A', new MaterialEntry(pipeNormalFluid, NiobiumTitanium))
                .arg('B', CustomTags.ZPM_CIRCUITS)
                .arg('C', FIELD_GENERATOR_ZPM)
                .arg('D', VACUUM_FREEZER)
                .arg('E', new MaterialEntry(plateDense, RhodiumPlatedPalladium))
                .arg('F', new MaterialEntry(wireGtQuadruple, UraniumRhodiumDinaquadide))
                .output(MEGA_VACUUM_FREEZER.asStack())
                .save(provider);

        SFTVanillaRecipeHelper.addShapedRecipe("chemical_factory")
                .pattern("ABA", "CDC", "EFE")
                .arg('A', CustomTags.LuV_CIRCUITS)
                .arg('B', new MaterialEntry(rotor, RhodiumPlatedPalladium))
                .arg('C', new MaterialEntry(pipeLargeFluid, Polybenzimidazole))
                .arg('D', LARGE_CHEMICAL_REACTOR)
                .arg('E', new MaterialEntry(cableGtOctal, NiobiumTitanium))
                .arg('F', new MaterialEntry(plateDouble, RhodiumPlatedPalladium))
                .output(CHEMICAL_FACTORY.asStack())
                .save(provider);

        SFTVanillaRecipeHelper.addShapedRecipe("mega_alloy_blast_smelter")
                .pattern("ABA", "CDC", "ECE")
                .arg('A', new MaterialEntry(rotor, HSSS))
                .arg('B', CustomTags.ZPM_CIRCUITS)
                .arg('C', FIELD_GENERATOR_ZPM)
                .arg('D', BLAST_ALLOY_SMELTER)
                .arg('E', new MaterialEntry(wireGtQuadruple, UraniumRhodiumDinaquadide))
                .output(MEGA_ALLOY_BLAST_SMELTER.asStack())
                .save(provider);

        SFTVanillaRecipeHelper.addShapedRecipe("certus_quartz_charger")
                .pattern("ABA", "CSC", "WCW")
                .arg('A', AEBlocks.QUARTZ_BLOCK)
                .arg('B', ELECTRIC_PISTON_HV)
                .arg('C', CustomTags.HV_CIRCUITS)
                .arg('W', new MaterialEntry(cableGtSingle, Copper))
                .arg('S', HULL[HV])
                .output(CERTUS_QUARTZ_CHARGER.asStack())
                .save(provider);

        SFTVanillaRecipeHelper.addShapedRecipe("desulfurizer")
                .pattern("WBW", "CSC", "WCW")
                .arg('B', ROBOT_ARM_HV)
                .arg('C', ELECTRIC_PUMP_HV)
                .arg('W', CustomTags.EV_CIRCUITS)
                .arg('S', HULL[HV])
                .output(DESULFURIZER.asStack())
                .save(provider);

        SFTVanillaRecipeHelper.addShapedRecipe("fishbig_maker")
                .pattern("RBR", "UPC", "RQR")
                .arg('B', AllItems.EXTENDO_GRIP)
                .arg('P', Items.PUFFERFISH)
                .arg('Q', MekanismBlocks.QUANTUM_ENTANGLOPORTER)
                .arg('C', AEBlocks.CONTROLLER)
                .arg('R', AllBlocks.RAILWAY_CASING.get())
                .arg('U', MekanismBlocks.ULTIMATE_INDUCTION_CELL)
                .output(FISHBIG_MAKER.asStack())
                .save(provider);

        SFTVanillaRecipeHelper.addShapedRecipe("oil_drilling_rig")
                .pattern("CBC", "EDE", "CAC")
                .arg('A', new MaterialEntry(pipeHugeFluid, StainlessSteel))
                .arg('B', ROBOT_ARM_HV)
                .arg('C', CustomTags.EV_CIRCUITS)
                .arg('D', HULL[HV])
                .arg('E', ELECTRIC_MOTOR_HV)
                .output(OIL_DRILLING_RIG.asStack())
                .save(provider);

        SFTVanillaRecipeHelper.addShapedRecipe("semiconductor_blast_furnace")
                .pattern("ABA", "CDC", "FEF")
                .arg('A', SILICON_BOULE)
                .arg('B', ELECTRIC_PUMP_EV)
                .arg('C', ELECTRIC_MOTOR_EV)
                .arg('D', HULL[EV])
                .arg('E', ROBOT_ARM_EV)
                .arg('F', new MaterialEntry(cableGtOctal, Aluminium))
                .output(SEMICONDUCTOR_BLAST_FURNACE.asStack())
                .save(provider);

        SFTVanillaRecipeHelper.addShapedRecipe("greenhouse")
                .pattern("ACA", "CSC", "WHW")
                .arg('A', new MaterialEntry(rotor, Steel))
                .arg('W', new MaterialEntry(cableGtSingle, Copper))
                .arg('C', CustomTags.MV_CIRCUITS)
                .arg('S', HULL[MV])
                .arg('H', MekanismBlocks.SUPERHEATING_ELEMENT)
                .output(GREENHOUSE.asStack())
                .save(provider);

        SFTVanillaRecipeHelper.addShapedRecipe("large_greenhouse")
                .pattern("ACA", "CSC", "WHW")
                .arg('A', new MaterialEntry(rotor, TungstenSteel))
                .arg('W', new MaterialEntry(cableGtQuadruple, Platinum))
                .arg('C', CustomTags.LuV_CIRCUITS)
                .arg('S', GREENHOUSE.asStack())
                .arg('H', FILTER_CASING.asStack())
                .output(LARGE_GREENHOUSE.asStack())
                .save(provider);

        SFTVanillaRecipeHelper.addShapedRecipe("hurry_up")
                .pattern("CBC", "BAB", "CDC")
                .arg('A', HULL[MV])
                .arg('B', CustomTags.HV_CIRCUITS)
                .arg('C', ELECTRIC_PISTON_MV)
                .arg('D', WORLD_ACCELERATOR[LV])
                .output(HURRY_UP.asStack())
                .save(provider);

        SFTVanillaRecipeHelper.addShapedRecipe("large_inscriber")
                .pattern("ACA", "CDC", "WBW")
                .arg('A', AEBlocks.CONTROLLER)
                .arg('B', ROBOT_ARM_HV)
                .arg('C', CustomTags.HV_CIRCUITS)
                .arg('D', HULL[HV])
                .arg('W', new MaterialEntry(cableGtSingle, Gold))
                .output(LARGE_INSCRIBER.asStack())
                .save(provider);

        SFTVanillaRecipeHelper.addShapedRecipe("large_mekanism_nuclear_reactor")
                .pattern("WCW", "CDC", "WBW")
                .arg('D', GeneratorsBlocks.FUSION_REACTOR_CONTROLLER)
                .arg('C', CustomTags.MV_CIRCUITS)
                .arg('B', HULL[LV])
                .arg('W', MekanismItems.ANTIMATTER_PELLET)
                .output(LARGE_MEKANISM_NUCLEAR_REACTOR.asStack())
                .save(provider);

        SFTVanillaRecipeHelper.addShapedRecipe("large_cracker")
                .pattern("ABA", "CDC", "EFE")
                .arg('A', new MaterialEntry(plate, Tungsten))
                .arg('B', new MaterialEntry(pipeHugeFluid, Tungsten))
                .arg('C', CustomTags.IV_CIRCUITS)
                .arg('D', CRACKER)
                .arg('E', ELECTRIC_PISTON_IV)
                .arg('F', new MaterialEntry(cableGtSingle, Platinum))
                .output(LARGE_CRACKER.asStack())
                .save(provider);

        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(SFTCore.id("common_mekanism_process_factory"))
                .inputItems(Objects.requireNonNull(IntegrateUtils.getItemById(IntegrateMods.MEK, "ultimate_infusing_factory")))
                .inputItems(Objects.requireNonNull(IntegrateUtils.getItemById(IntegrateMods.MEK, "ultimate_enriching_factory")))
                .inputItems(Objects.requireNonNull(IntegrateUtils.getItemById(IntegrateMods.MEK, "ultimate_smelting_factory")))
                .inputItems(Objects.requireNonNull(IntegrateUtils.getItemById(IntegrateMods.MEK, "pressurized_reaction_chamber")))
                .inputItems(Objects.requireNonNull(IntegrateUtils.getItemById(IntegrateMods.MEK, "chemical_dissolution_chamber")))
                .inputItems(Objects.requireNonNull(IntegrateUtils.getItemById(IntegrateMods.MEK, "electrolytic_separator")))
                .inputItems(Objects.requireNonNull(IntegrateUtils.getItemById(IntegrateMods.MEK, "solar_neutron_activator")))
                .inputItems(Objects.requireNonNull(IntegrateUtils.getItemById(IntegrateMods.MEK, "isotopic_centrifuge")))
                .inputItems(Objects.requireNonNull(IntegrateUtils.getItemById(IntegrateMods.MEK, "rotary_condensentrator")))
                .inputFluids(Glue.getFluid(8000))
                .outputItems(COMMON_MEKANISM_PROCESS_FACTORY)
                .duration(1800)
                .EUt(VA[LV])
                .save(provider);

        SFTVanillaRecipeHelper.addShapedRecipe("common_factory")
                .pattern("ABA", "CDC", "AEA")
                .arg('A', new MaterialEntry(plateDouble, Aluminium))
                .arg('B', ROBOT_ARM_MV)
                .arg('C', CustomTags.MV_CIRCUITS)
                .arg('D', MULTI_FUNCTIONAL_CASING)
                .arg('E', ELECTRIC_MOTOR_MV)
                .output(SFTMultiMachines.COMMON_FACTORY.asStack())
                .save(provider);

        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(SFTCore.id("large_gas_collector"))
                .outputItems(LARGE_GAS_COLLECTOR.asStack())
                .inputItems(frameGt, TungstenSteel, 4)
                .inputItems(GAS_COLLECTOR[IV].asStack())
                .inputItems(EMITTER_IV, 4)
                .inputItems(ROBOT_ARM_IV, 2)
                .inputItems(cableGtDouble, NiobiumTitanium, 8)
                .inputFluids(Polybenzimidazole.getFluid(16 * L))
                .duration(1200)
                .EUt(VA[IV])
                .save(provider);
    }
}
