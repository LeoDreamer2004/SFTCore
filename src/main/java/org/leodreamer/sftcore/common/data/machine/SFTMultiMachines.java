package org.leodreamer.sftcore.common.data.machine;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.api.machine.multiblock.WorkableKineticMultiblockMachine;
import org.leodreamer.sftcore.api.pattern.MultiBlockFileReader;
import org.leodreamer.sftcore.common.data.machine.ui.SFTMachineDisplays;
import org.leodreamer.sftcore.common.data.recipe.SFTRecipeModifiers;
import org.leodreamer.sftcore.common.data.recipe.SFTRecipeTypes;
import org.leodreamer.sftcore.common.machine.multiblock.CommonFactoryMachine;
import org.leodreamer.sftcore.common.machine.multiblock.SFTPartAbility;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.CoilWorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.common.data.*;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;

import appeng.core.definitions.AEBlocks;
import com.simibubi.create.AllBlocks;
import mekanism.common.registries.MekanismBlocks;
import mekanism.generators.common.registries.GeneratorsBlocks;

import static com.gregtechceu.gtceu.api.pattern.Predicates.*;
import static com.gregtechceu.gtceu.common.data.GCYMBlocks.*;
import static com.gregtechceu.gtceu.common.data.GTBlocks.*;
import static com.gregtechceu.gtceu.common.data.GTRecipeModifiers.*;
import static org.leodreamer.sftcore.SFTCore.REGISTRATE;
import static org.leodreamer.sftcore.common.data.SFTBlocks.MULTI_FUNCTIONAL_CASING;
import static org.leodreamer.sftcore.common.data.recipe.SFTRecipeModifiers.*;

public final class SFTMultiMachines {

    public static void init() {}

    public static final MachineDefinition FISHBIG_MAKER = REGISTRATE
        .multiblock("fishbig_maker", WorkableKineticMultiblockMachine::new)
        .rotationState(RotationState.ALL)
        .recipeType(SFTRecipeTypes.FISHBIG_MAKER_RECIPES)
        .appearanceBlock(CASING_STEEL_SOLID)
        .pattern(
            definition -> MultiBlockFileReader.start(definition)
                .where('U', controller(blocks(definition.get())))
                .where(
                    'A',
                    blocks(AllBlocks.RAILWAY_CASING.get())
                        .or(autoAbilities(definition.getRecipeTypes()))
                        .or(
                            abilities(SFTPartAbility.INPUT_KINETIC)
                                .setMaxGlobalLimited(2)
                                .setMinGlobalLimited(1)
                        )
                )
                .where('B', blocks(MekanismBlocks.INDUCTION_CASING.getBlock()))
                .where('C', blocks(MekanismBlocks.TELEPORTER_FRAME.getBlock()))
                .where('E', blocks(MekanismBlocks.DYNAMIC_TANK.getBlock()))
                .where('F', blocks(GeneratorsBlocks.FUSION_REACTOR_FRAME.getBlock()))
                .where('H', blocks(MekanismBlocks.REFINED_GLOWSTONE_BLOCK.getBlock()))
                .build()
        )
        .workableCasingModel(
            SFTCore.id("block/casings/solid/create_railway_casing"),
            GTCEu.id("block/multiblock/gcym/large_mixer")
        )
        .register();

    public static final MachineDefinition CERTUS_QUARTZ_CHARGER = REGISTRATE
        .multiblock("certus_quartz_charger", WorkableElectricMultiblockMachine::new)
        .rotationState(RotationState.NON_Y_AXIS)
        .tooltips(
            builder -> builder
                .tip("Release the power of Certus Quartz.")
                .parallelizable()
        )
        .appearanceBlock(CASING_STEEL_SOLID)
        .recipeType(SFTRecipeTypes.CERTUS_QUARTZ_CHARGE_RECIPES)
        .recipeModifiers(PARALLEL_HATCH, OC_NON_PERFECT)
        .pattern(
            definition -> MultiBlockFileReader.start(definition)
                .where('K', controller(blocks(definition.get())))
                .where('X', blocks(AEBlocks.QUARTZ_VIBRANT_GLASS.block()))
                .where('S', blocks(AEBlocks.CHARGER.block()))
                .where(
                    'C',
                    blocks(CASING_STEEL_SOLID.get())
                        .setMinGlobalLimited(10)
                        .or(autoAbilities(definition.getRecipeTypes()))
                        .or(autoAbilities(true, false, true))
                )
                .where('O', abilities(PartAbility.MUFFLER))
                .build()
        )
        .workableCasingModel(
            GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
            GTCEu.id("block/multiblock/gcym/large_mixer")
        )
        .register();

    public static final MachineDefinition LARGE_INSCRIBER = REGISTRATE
        .multiblock("large_inscriber", WorkableElectricMultiblockMachine::new)
        .rotationState(RotationState.NON_Y_AXIS)
        .tooltips(
            builder -> builder
                .tip("A large inscriber for advanced circuits.")
                .parallelizable()
        )
        .recipeType(SFTRecipeTypes.LARGE_INSCRIBER)
        .recipeModifiers(PARALLEL_HATCH, OC_NON_PERFECT)
        .appearanceBlock(CASING_STEEL_SOLID)
        .pattern(
            definition -> MultiBlockFileReader.start(definition)
                .where('K', controller(blocks(definition.get())))
                .where('X', blocks(AEBlocks.QUARTZ_VIBRANT_GLASS.block()))
                .where('S', blocks(AEBlocks.INSCRIBER.block()))
                .where('Z', blocks(MACHINE_CASING_MV.get()))
                .where(
                    'C',
                    blocks(CASING_STEEL_SOLID.get())
                        .setMinGlobalLimited(10)
                        .or(autoAbilities(definition.getRecipeTypes()))
                        .or(autoAbilities(true, false, true))
                )
                .where('O', abilities(PartAbility.MUFFLER))
                .build()
        )
        .workableCasingModel(
            GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
            GTCEu.id("block/multiblock/gcym/large_mixer")
        )
        .register();

    public static final MachineDefinition LARGE_MEKANISM_NUCLEAR_REACTOR = REGISTRATE
        .multiblock("large_mekanism_nuclear_reactor", WorkableElectricMultiblockMachine::new)
        .rotationState(RotationState.NON_Y_AXIS)
        .generator(true)
        .recipeType(SFTRecipeTypes.MEKANISM_NUCLEAR_REACTION_RECIPES)
        .recipeModifiers(OC_PERFECT_SUBTICK, BATCH_MODE)
        .tooltips(
            builder -> builder
                .tip("Extract energy from fuel thoroughly.")
                .perfectOverlock()
                .allowLaser()
        )
        .appearanceBlock(GeneratorsBlocks.FUSION_REACTOR_FRAME::getBlock)
        .pattern(
            definition -> MultiBlockFileReader.start(definition)
                .where('L', controller(blocks(definition.get())))
                .where(
                    'A',
                    blocks(GeneratorsBlocks.FUSION_REACTOR_FRAME.getBlock())
                        .or(autoAbilities(definition.getRecipeTypes()))
                        .or(abilities(PartAbility.OUTPUT_LASER).setMaxGlobalLimited(1))
                )
                .where('C', blocks(FIREBOX_STEEL.get()))
                .where('B', blocks(GeneratorsBlocks.REACTOR_GLASS.getBlock()))
                .where('E', blocks(GeneratorsBlocks.FISSION_FUEL_ASSEMBLY.getBlock()))
                .where('F', frames(GTMaterials.BlackSteel))
                .where('G', blocks(COIL_CUPRONICKEL.get()))
                .where('H', blocks(LAMPS.get(DyeColor.GREEN).get()))
                .where('D', blocks(CASING_INVAR_HEATPROOF.get()))
                .build()
        )
        .workableCasingModel(
            SFTCore.id("block/casings/solid/mek_reactor_frame"),
            GTCEu.id("block/multiblock/generator/large_plasma_turbine")
        )
        .register();

    public static final MachineDefinition COMMON_MEKANISM_PROCESS_FACTORY = REGISTRATE
        .multiblock("common_mekanism_process_factory", WorkableElectricMultiblockMachine::new)
        .rotationState(RotationState.ALL)
        .tooltips(
            builder -> builder
                .parallelizable()
                .halfPerfectOverlock()
        )
        .recipeType(SFTRecipeTypes.MEKANISM_PROCESSING_RECIPES)
        .recipeModifiers(PARALLEL_HATCH, OC_HALF_PERFECT, BATCH_MODE)
        .appearanceBlock(MekanismBlocks.SPS_CASING::getBlock)
        .pattern(
            definition -> MultiBlockFileReader.start(definition)
                .where('L', controller(blocks(definition.get())))
                .where(
                    'A',
                    blocks(MekanismBlocks.SPS_CASING.getBlock())
                        .or(autoAbilities(definition.getRecipeTypes()))
                        .or(autoAbilities(true, false, true))
                )
                .where('B', blocks(MekanismBlocks.SPS_CASING.getBlock()))
                .where('C', blocks(MekanismBlocks.SUPERHEATING_ELEMENT.getBlock()))
                .where('E', blocks(GeneratorsBlocks.REACTOR_GLASS.getBlock()))
                .where('D', blocks(GeneratorsBlocks.ELECTROMAGNETIC_COIL.getBlock()))
                .build()
        )
        .workableCasingModel(
            SFTCore.id("block/casings/solid/mek_sps_casing"),
            GTCEu.id("block/multiblock/fusion_reactor")
        )
        .register();

    public static final MachineDefinition COMMON_FACTORY = REGISTRATE
        .multiblock("common_factory", CommonFactoryMachine::new)
        .rotationState(RotationState.ALL)
        .recipeTypes(CommonFactoryMachine.AVAILABLE_RECIPES)
        .tooltips(
            builder -> builder
                .intro(
                    "- §7The simple machine in the§r machine adjustment hatch§7 limits the recipe type and voltage.§r",
                    "- §7The voltage of energy hatch and machine must match, though it is allowed to use two energy hatch.§r",
                    "- §7For every 1 level above §6Cupronickel§7, the machine gets 4 extra parallels§r"
                )
                .energyMultiplier(0.9)
                .timeMultiplier(0.8)
                .structureComeFrom("GregTech Leisure")
        )
        .recipeModifiers(
            SFTRecipeModifiers::commonFactoryParallel,
            new SimpleMultiplierModifier(0.9, 0.8),
            BATCH_MODE,
            OC_NON_PERFECT
        )
        .appearanceBlock(MULTI_FUNCTIONAL_CASING)
        .pattern(
            definition -> MultiBlockFileReader.start(definition)
                .where('S', controller(blocks(definition.get())))
                .where('C', heatingCoils())
                .where(
                    'A',
                    blocks(MULTI_FUNCTIONAL_CASING.get())
                        .setMinGlobalLimited(12)
                        .or(autoAbilities(CommonFactoryMachine.AVAILABLE_RECIPES))
                        .or(abilities(SFTPartAbility.MACHINE_ADJUSTMENT).setExactLimit(1))
                        .or(abilities(PartAbility.MAINTENANCE).setExactLimit(1))
                )
                .build()
        )
        .workableCasingModel(
            SFTCore.id("block/casings/solid/multi_functional_casing"),
            GTCEu.id("block/multiblock/gcym/large_mixer")
        )
        .register();

    public static final MachineDefinition DESULFURIZER = REGISTRATE
        .multiblock("desulfurizer", WorkableElectricMultiblockMachine::new)
        .tooltips(
            builder -> builder
                .tip("Desulfurize oil efficiently.")
                .parallelizable()
                .halfPerfectOverlock()
        )
        .rotationState(RotationState.ALL)
        .recipeType(SFTRecipeTypes.DESULFURIZE_RECIPES)
        .recipeModifiers(PARALLEL_HATCH, OC_HALF_PERFECT, BATCH_MODE)
        .appearanceBlock(CASING_STAINLESS_TURBINE)
        .pattern(
            definition -> MultiBlockFileReader.start(definition)
                .where('I', controller(blocks(definition.get())))
                .where(
                    'A',
                    blocks(CASING_STAINLESS_TURBINE.get())
                        .setMinGlobalLimited(10)
                        .or(autoAbilities(definition.getRecipeTypes()))
                        .or(autoAbilities(true, false, true))
                )
                .where('B', frames(GTMaterials.StainlessSteel))
                .where('C', blocks(CASING_INVAR_HEATPROOF.get()))
                .where('D', blocks(COIL_CUPRONICKEL.get()))
                .where('E', blocks(CASING_STAINLESS_TURBINE.get()))
                .where('F', blocks(CASING_STAINLESS_CLEAN.get()))
                .where('G', blocks(CASING_TITANIUM_PIPE.get()))
                .build()
        )
        .workableCasingModel(
            GTCEu.id("block/casings/mechanic/machine_casing_turbine_stainless_steel"),
            GTCEu.id("block/multiblock/large_chemical_reactor")
        )
        .register();

    public static final MachineDefinition HURRY_UP = REGISTRATE
        .multiblock("hurry_up", WorkableElectricMultiblockMachine::new)
        .rotationState(RotationState.ALL)
        .recipeType(SFTRecipeTypes.HURRY_UP_RECIPES)
        .recipeModifiers(OC_NON_PERFECT)
        .appearanceBlock(TREATED_WOOD_PLANK)
        .pattern(
            definition -> MultiBlockFileReader.start(definition)
                .where('B', controller(blocks(definition.get())))
                .where(
                    'A',
                    blocks(TREATED_WOOD_PLANK.get())
                        .setMinGlobalLimited(20)
                        .or(autoAbilities(definition.getRecipeTypes()))
                )
                .where('C', blocks(PLASTCRETE.get()))
                .where('D', blocks(CASING_ALUMINIUM_FROSTPROOF.get()))
                .build()
        )
        .workableCasingModel(
            GTCEu.id("block/treated_wood_planks"), GTCEu.id("block/multiblock/gcym/large_mixer")
        )
        .register();

    public static final MachineDefinition GREENHOUSE = REGISTRATE
        .multiblock("greenhouse", WorkableElectricMultiblockMachine::new)
        .rotationState(RotationState.NON_Y_AXIS)
        .tooltips(builder -> builder.tip("Hope our plants can grow well."))
        .recipeType(SFTRecipeTypes.GREENHOUSE_RECIPES)
        .recipeModifiers(OC_NON_PERFECT, BATCH_MODE)
        .appearanceBlock(CASING_STEEL_SOLID)
        .pattern(
            definition -> MultiBlockFileReader.start(definition)
                .where("H", controller(blocks(definition.get())))
                .where(
                    "A",
                    blocks(CASING_STEEL_SOLID.get())
                        .setMinGlobalLimited(25)
                        .or(autoAbilities(definition.getRecipeTypes()))
                        .or(autoAbilities(true, false, false))
                )
                .where("B", blocks(CASING_TEMPERED_GLASS.get()))
                .where("C", blocks(CASING_STEEL_SOLID.get()))
                .where("D", blocks(Blocks.MUD))
                .where("E", blocks(CASING_GRATE.get()))
                .where("F", fluids(Fluids.WATER))
                .where("G", blocks(LAMPS.get(DyeColor.WHITE).get()))
                .build()
        )
        .workableCasingModel(
            GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
            GTCEu.id("block/multiblock/gcym/large_mixer")
        )
        .register();

    public static final MachineDefinition LARGE_GREENHOUSE = REGISTRATE
        .multiblock("large_greenhouse", WorkableElectricMultiblockMachine::new)
        .rotationState(RotationState.NON_Y_AXIS)
        .recipeType(SFTRecipeTypes.GREENHOUSE_RECIPES)
        .recipeModifiers(PARALLEL_HATCH, GCYM_MACHINE_REDUCE, OC_HALF_PERFECT, BATCH_MODE)
        .tooltips(
            builder -> builder
                .parallelizable()
                .availableTypes(SFTRecipeTypes.GREENHOUSE_RECIPES)
                .gcymReduce()
                .halfPerfectOverlock()
        )
        .appearanceBlock(CASING_STAINLESS_CLEAN)
        .pattern(
            definition -> MultiBlockFileReader.start(definition)
                .where('I', controller(blocks(definition.get())))
                .where(
                    'A',
                    blocks(CASING_STAINLESS_CLEAN.get())
                        .or(autoAbilities(definition.getRecipeTypes()))
                        .or(autoAbilities(true, false, true))
                )
                .where('B', blocks(PLASTCRETE.get()))
                .where('C', blocks(CLEANROOM_GLASS.get()))
                .where('D', blocks(CASING_STAINLESS_CLEAN.get()))
                .where('E', blocks(Blocks.MOSS_BLOCK))
                .where('F', blocks(FILTER_CASING.get()))
                .where('G', blocks(LAMPS.get(DyeColor.WHITE).get()))
                .where('H', frames(GTMaterials.StainlessSteel))
                .build()
        )
        .workableCasingModel(
            GTCEu.id("block/casings/solid/machine_casing_clean_stainless_steel"),
            GTCEu.id("block/multiblock/gcym/large_mixer")
        )
        .register();

    public static final MachineDefinition OIL_DRILLING_RIG = REGISTRATE
        .multiblock("oil_drilling_rig", WorkableElectricMultiblockMachine::new)
        .rotationState(RotationState.NON_Y_AXIS)
        .tooltips(
            builder -> builder
                .tip("Oh, It's not so environmental friendly...")
                .parallelizable()
        )
        .recipeType(SFTRecipeTypes.OIL_DRILLING_RECIPES)
        .recipeModifiers(PARALLEL_HATCH, OC_NON_PERFECT, BATCH_MODE)
        .appearanceBlock(CASING_STEEL_SOLID)
        .pattern(
            definition -> MultiBlockFileReader.start(definition)
                .where('C', controller(blocks(definition.get())))
                .where(
                    'A',
                    blocks(CASING_STEEL_SOLID.get())
                        .or(autoAbilities(definition.getRecipeTypes()))
                        .or(autoAbilities(true, false, true))
                )
                .where('B', frames(GTMaterials.Steel))
                .build()
        )
        .workableCasingModel(
            GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
            GTCEu.id("block/multiblock/gcym/large_mixer")
        )
        .register();

    public static final MachineDefinition LARGE_GAS_COLLECTOR = REGISTRATE
        .multiblock("large_gas_collector", WorkableElectricMultiblockMachine::new)
        .rotationState(RotationState.NON_Y_AXIS)
        .recipeType(SFTRecipeTypes.LARGE_GAS_COLLECTOR_RECIPES)
        .recipeModifiers(SFTRecipeModifiers::infiniteParallel, OC_NON_PERFECT, BATCH_MODE)
        .appearanceBlock(CASING_STRESS_PROOF)
        .tooltips(
            builder -> builder
                .tip("Collect gas from the anywhere.")
                .intro("-§7 Has (nearly)§c§l infinite§7 parallels")
        )
        .pattern(
            definition -> MultiBlockFileReader.start(definition)
                .where('H', controller(blocks(definition.get())))
                .where('A', blocks(CASING_STRESS_PROOF.get()))
                .where('B', blocks(CASING_CORROSION_PROOF.get()))
                .where('C', frames(GTMaterials.StainlessSteel))
                .where('D', blocks(HERMETIC_CASING_IV.get()))
                .where('E', blocks(CASING_STEEL_PIPE.get()))
                .where('F', blocks(CASING_GRATE.get()))
                .where(
                    'G',
                    blocks(CASING_STRESS_PROOF.get())
                        .or(autoAbilities(definition.getRecipeTypes()))
                        .or(abilities(PartAbility.MAINTENANCE).setExactLimit(1))
                )
                .build()
        )
        .workableCasingModel(
            GTCEu.id("block/casings/gcym/stress_proof_casing"),
            GTCEu.id("block/multiblock/gcym/large_mixer")
        )
        .register();

    public static final MachineDefinition SEMICONDUCTOR_BLAST_FURNACE = REGISTRATE
        .multiblock("semiconductor_blast_furnace", CoilWorkableElectricMultiblockMachine::new)
        .rotationState(RotationState.ALL)
        .recipeType(SFTRecipeTypes.SEMICONDUCTOR_BLAST_RECIPES)
        .recipeModifiers(PARALLEL_HATCH, GTRecipeModifiers::ebfOverclock, BATCH_MODE)
        .appearanceBlock(CASING_HIGH_TEMPERATURE_SMELTING)
        .tooltips(
            builder -> builder
                .tip("Expert in producing semiconductor.")
                .parallelizable()
                .ebf()
        )
        .pattern(
            definition -> MultiBlockFileReader.start(definition)
                .where('G', controller(blocks(definition.get())))
                .where(
                    'A',
                    blocks(CASING_HIGH_TEMPERATURE_SMELTING.get())
                        .or(autoAbilities(definition.getRecipeTypes()))
                        .or(autoAbilities(true, false, true))
                )
                .where('B', blocks(CASING_HIGH_TEMPERATURE_SMELTING.get()))
                .where('C', blocks(HEAT_VENT.get()))
                .where('D', heatingCoils())
                .where('E', blocks(CASING_TITANIUM_PIPE.get()))
                .where('F', abilities(PartAbility.MUFFLER))
                .build()
        )
        .recoverAsh()
        .workableCasingModel(
            GTCEu.id("block/casings/gcym/high_temperature_smelting_casing"),
            GTCEu.id("block/multiblock/gcym/large_mixer")
        )
        .additionalDisplay(SFTMachineDisplays.ebfCoilDisplay)
        .register();

    public static final MachineDefinition LARGE_CRACKER = REGISTRATE
        .multiblock("large_cracker", CoilWorkableElectricMultiblockMachine::new)
        .rotationState(RotationState.ALL)
        .recipeType(GTRecipeTypes.CRACKING_RECIPES)
        .recipeModifiers(
            PARALLEL_HATCH,
            BATCH_MODE,
            SFTRecipeModifiers::largeCrackerOverlock,
            OC_HALF_PERFECT,
            GCYM_MACHINE_REDUCE
        )
        .tooltips(
            builder -> builder
                .parallelizable()
                .availableTypes(GTRecipeTypes.CRACKING_RECIPES)
                .intro(
                    "- §7For every 1 level above §6Cupronickel§7, recipe energy and time consumption are both reduced by 10%"
                )
                .gcymReduce()
                .halfPerfectOverlock()
        )
        .appearanceBlock(CASING_STAINLESS_CLEAN)
        .pattern(
            definition -> MultiBlockFileReader.start(definition)
                .where('H', controller(blocks(definition.get())))
                .where(
                    'A',
                    blocks(CASING_STAINLESS_CLEAN.get())
                        .or(autoAbilities(definition.getRecipeTypes()))
                        .or(autoAbilities(true, false, true))
                )
                .where('B', blocks(CASING_STAINLESS_TURBINE.get()))
                .where('C', frames(GTMaterials.StainlessSteel))
                .where('D', blocks(CASING_STAINLESS_CLEAN.get()))
                .where('E', heatingCoils())
                .where('F', abilities(PartAbility.MUFFLER))
                .where('G', blocks(CASING_TEMPERED_GLASS.get()))
                .build()
        )
        .recoverAsh()
        .workableCasingModel(
            GTCEu.id("block/casings/solid/machine_casing_clean_stainless_steel"),
            GTCEu.id("block/multiblock/cracking_unit")
        )
        .register();

    public static final MachineDefinition CHEMICAL_FACTORY = REGISTRATE
        .multiblock("chemical_factory", CoilWorkableElectricMultiblockMachine::new)
        .rotationState(RotationState.ALL)
        .recipeType(GTRecipeTypes.LARGE_CHEMICAL_RECIPES)
        .recipeModifiers(
            PARALLEL_HATCH,
            OC_PERFECT_SUBTICK,
            DEFAULT_ENVIRONMENT_REQUIREMENT,
            BATCH_MODE,
            MEGA_COIL_MACHINE_REDUCE
        )
        .tooltips(
            builder -> builder
                .parallelizable()
                .availableTypes(GTRecipeTypes.LARGE_CHEMICAL_RECIPES)
                .megaReduceWithCoil()
                .perfectOverlock()
                .allowLaser()
        )
        .appearanceBlock(CASING_PTFE_INERT)
        .pattern(
            definition -> MultiBlockFileReader.start(definition)
                .where('N', controller(blocks(definition.getBlock())))
                .where(
                    'A',
                    blocks(CASING_PTFE_INERT.get())
                        .or(autoAbilities(definition.getRecipeTypes()))
                        .or(autoAbilities(true, false, true))
                        .or(abilities(PartAbility.INPUT_LASER).setMaxGlobalLimited(1))
                )
                .where('C', blocks(CASING_LAMINATED_GLASS.get()))
                .where('B', blocks(CASING_POLYTETRAFLUOROETHYLENE_PIPE.get()))
                .where('D', heatingCoils())
                .build()
        )
        .additionalDisplay(SFTMachineDisplays.simpleCoilDisplay)
        .workableCasingModel(
            GTCEu.id("block/casings/solid/machine_casing_inert_ptfe"),
            GTCEu.id("block/multiblock/large_chemical_reactor")
        )
        .register();

    public static final MachineDefinition MEGA_ALLOY_BLAST_SMELTER = REGISTRATE
        .multiblock("mega_alloy_blast_smelter", CoilWorkableElectricMultiblockMachine::new)
        .rotationState(RotationState.ALL)
        .recipeModifiers(
            PARALLEL_HATCH,
            BATCH_MODE,
            GTRecipeModifiers::ebfOverclock,
            GCYM_MACHINE_REDUCE,
            MEGA_COIL_MACHINE_REDUCE
        )
        .recipeType(GCYMRecipeTypes.ALLOY_BLAST_RECIPES)
        .tooltips(
            builder -> builder
                .parallelizable()
                .availableTypes(GCYMRecipeTypes.ALLOY_BLAST_RECIPES)
                .ebf()
                .gcymReduce()
                .megaReduceWithCoil()
                .allowLaser()
        )
        .appearanceBlock(GCYMBlocks.CASING_HIGH_TEMPERATURE_SMELTING)
        .pattern(
            definition -> MultiBlockFileReader.start(definition)
                .where('O', controller(blocks(definition.get())))
                .where(
                    'A',
                    blocks(CASING_HIGH_TEMPERATURE_SMELTING.get())
                        .or(autoAbilities(definition.getRecipeTypes()))
                        .or(autoAbilities(true, false, true))
                        .or(abilities(PartAbility.INPUT_LASER).setMaxGlobalLimited(1))
                )
                .where('B', blocks(CASING_HIGH_TEMPERATURE_SMELTING.get()))
                .where('C', blocks(HEAT_VENT.get()))
                .where('D', blocks(FIREBOX_TUNGSTENSTEEL.get()))
                .where('E', blocks(CASING_TUNGSTENSTEEL_PIPE.get()))
                .where('F', blocks(CASING_TITANIUM_PIPE.get()))
                .where('G', blocks(FIREBOX_STEEL.get()))
                .where('H', blocks(CASING_EXTREME_ENGINE_INTAKE.get()))
                .where('I', blocks(FUSION_GLASS.get()))
                .where('J', heatingCoils())
                .where('K', air())
                .where('L', blocks(FUSION_CASING.get()))
                .where('M', frames(GTMaterials.NaquadahAlloy))
                .where('N', blocks(SUPERCONDUCTING_COIL.get()))
                .where('P', abilities(PartAbility.MUFFLER))
                .build()
        )
        .recoverAsh()
        .additionalDisplay(SFTMachineDisplays.ebfCoilDisplay)
        .workableCasingModel(
            GTCEu.id("block/casings/gcym/high_temperature_smelting_casing"),
            GTCEu.id("block/multiblock/gcym/mega_blast_furnace")
        )
        .register();

    public static final MachineDefinition ORE_PROCESSOR = REGISTRATE
        .multiblock("ore_processor", WorkableElectricMultiblockMachine::new)
        .rotationState(RotationState.NON_Y_AXIS)
        .recipeType(SFTRecipeTypes.ORE_PROCESSING)
        .recipeModifiers(SFTRecipeModifiers::infiniteParallel, OC_NON_PERFECT, BATCH_MODE)
        .appearanceBlock(CASING_STAINLESS_TURBINE)
        .tooltips(
            builder -> builder
                .tip("Break Everything. Reconstruct Everything.")
                .intro("-§7 Has (nearly)§c§l infinite§7 parallels")
                .allowLaser()
        )
        .pattern(
            definition -> MultiBlockFileReader.start(definition)
                .where('P', controller(blocks(definition.get())))
                .where('A', blocks(CASING_LAMINATED_GLASS.get()))
                .where(
                    'B', blocks(CASING_STAINLESS_TURBINE.get())
                        .or(autoAbilities(definition.getRecipeTypes()))
                        .or(autoAbilities(true, false, false))
                        .or(abilities(PartAbility.INPUT_LASER).setMaxGlobalLimited(1))
                )
                .where('C', blocks(CASING_NONCONDUCTING.get()))
                .where('D', blocks(CASING_TEMPERED_GLASS.get()))
                .where('E', blocks(CASING_TUNGSTENSTEEL_GEARBOX.get()))
                .where('F', frames(GTMaterials.TungstenCarbide))
                .where('G', blocks(FIREBOX_TUNGSTENSTEEL.get()))
                .where('H', blocks(CRUSHING_WHEELS.get()))
                .where('I', blocks(CASING_TUNGSTENSTEEL_PIPE.get()))
                .where('J', blocks(CASING_VIBRATION_SAFE.get()))
                .where('K', blocks(HEAT_VENT.get()))
                .where('L', blocks(CASING_STEEL_PIPE.get()))
                .where('M', abilities(PartAbility.MUFFLER))
                .where('N', blocks(HERMETIC_CASING_IV.get()))
                .where('O', blocks(CASING_WATERTIGHT.get()))
                .build()
        )
        .workableCasingModel(
            GTCEu.id("block/casings/mechanic/machine_casing_turbine_stainless_steel"),
            GTCEu.id("block/multiblock/gcym/large_mixer")
        )
        .register();
}
