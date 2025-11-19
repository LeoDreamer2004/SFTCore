package org.leodreamer.sftcore.common.data.machine;

import appeng.core.definitions.AEBlocks;
import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.CoilWorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.common.data.*;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.simibubi.create.AllBlocks;
import mekanism.common.registries.MekanismBlocks;
import mekanism.generators.common.registries.GeneratorsBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.api.registry.SFTTooltips;
import org.leodreamer.sftcore.common.data.recipe.SFTRecipeModifiers;
import org.leodreamer.sftcore.common.data.recipe.SFTRecipeTypes;
import org.leodreamer.sftcore.common.machine.multiblock.CommonFactoryMachine;
import org.leodreamer.sftcore.common.machine.multiblock.SFTPartAbility;

import static com.gregtechceu.gtceu.api.pattern.Predicates.*;
import static com.gregtechceu.gtceu.common.data.GCYMBlocks.*;
import static com.gregtechceu.gtceu.common.data.GCYMRecipeTypes.ALLOY_BLAST_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTBlocks.*;
import static com.gregtechceu.gtceu.common.data.GTRecipeModifiers.*;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.LARGE_CHEMICAL_RECIPES;
import static org.leodreamer.sftcore.SFTCore.REGISTRATE;
import static org.leodreamer.sftcore.common.data.SFTBlocks.MULTI_FUNCTIONAL_CASING;
import static org.leodreamer.sftcore.common.data.recipe.SFTRecipeModifiers.*;

public final class SFTMultiMachines {
    public static void init() {
    }

    public static final MachineDefinition FISHBIG_MAKER = REGISTRATE.multiblock("fishbig_maker",
                    WorkableElectricMultiblockMachine::new)
            .rotationState(RotationState.ALL)
            .recipeType(SFTRecipeTypes.FISHBIG_MAKER_RECIPES)
            .recipeModifiers(OC_NON_PERFECT)
            .appearanceBlock(CASING_STEEL_SOLID)
            .pattern(definition ->
                    FactoryBlockPattern.start()
                            .aisle("AAAAAAA", "ABCCCBA", "ABC CBA", "ABC CBA", "ABCCCBA", "AAAAAAA")
                            .aisle("AAAAAAA", "E     E", "E     E", "E     E", "E     E", "AFFFFFA")
                            .aisle("AAAAAAA", "E     E", "E     E", "E     E", "E     E", "AFFFFFA")
                            .aisle("AAAAAAA", "E     E", "E     E", "E     E", "E     E", "AFFHFFA")
                            .aisle("AAAAAAA", "E     E", "E     E", "E     E", "E     E", "AFFFFFA")
                            .aisle("AAAAAAA", "E     E", "E     E", "E     E", "E     E", "AFFFFFA")
                            .aisle("AAAUAAA", "ABCCCBA", "ABC CBA", "ABC CBA", "ABCCCBA", "AAAAAAA")
                            .where("U", controller(blocks(definition.get())))
                            .where("A", blocks(AllBlocks.RAILWAY_CASING.get())
                                    .or(autoAbilities(definition.getRecipeTypes())))
                            .where("B", blocks(MekanismBlocks.INDUCTION_CASING.getBlock()))
                            .where("C", blocks(MekanismBlocks.TELEPORTER_FRAME.getBlock()))
                            .where("E", blocks(MekanismBlocks.DYNAMIC_TANK.getBlock()))
                            .where("F", blocks(GeneratorsBlocks.FUSION_REACTOR_FRAME.getBlock()))
                            .where("H", blocks(MekanismBlocks.REFINED_GLOWSTONE_BLOCK.getBlock()))
                            .build())
            .workableCasingModel(SFTCore.id("block/casings/solid/create_railway_casing"),
                    GTCEu.id("block/multiblock/gcym/large_mixer"))
            .register();


    public static final MachineDefinition CERTUS_QUARTZ_CHARGER = REGISTRATE.multiblock("certus_quartz_charger", WorkableElectricMultiblockMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .tooltips(Component.translatable("gtceu.multiblock.parallelizable.tooltip"))
            .appearanceBlock(CASING_STEEL_SOLID)
            .recipeType(SFTRecipeTypes.CERTUS_QUARTZ_CHARGE_RECIPES)
            .recipeModifiers(PARALLEL_HATCH, OC_NON_PERFECT)
            .pattern(definition ->
                    FactoryBlockPattern.start()
                            .aisle("CCC", "CCC", "CCC")
                            .aisle("CCC", "CSC", "COC")
                            .aisle("CKC", "CXC", "CCC")
                            .where("K", controller(blocks(definition.get())))
                            .where("X", blocks(AEBlocks.QUARTZ_VIBRANT_GLASS.block()))
                            .where("S", blocks(AEBlocks.CHARGER.block()))
                            .where("C", blocks(CASING_STEEL_SOLID.get())
                                    .setMinGlobalLimited(10)
                                    .or(autoAbilities(definition.getRecipeTypes()))
                                    .or(autoAbilities(true, false, true)))
                            .where("O", abilities(PartAbility.MUFFLER))
                            .build())
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/multiblock/gcym/large_mixer"))
            .register();

    public static final MachineDefinition LARGE_INSCRIBER = REGISTRATE.multiblock("large_inscriber",
                    WorkableElectricMultiblockMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .tooltips(Component.translatable("gtceu.multiblock.parallelizable.tooltip"))
            .recipeType(SFTRecipeTypes.LARGE_INSCRIBER)
            .recipeModifiers(PARALLEL_HATCH, OC_NON_PERFECT)
            .appearanceBlock(CASING_STEEL_SOLID)
            .pattern(definition ->
                    FactoryBlockPattern.start()
                            .aisle("CCC", "CCC", "CCC", "CCC")
                            .aisle("CCC", "CSC", "CZC", "COC")
                            .aisle("CKC", "CXC", "CXC", "CCC")
                            .where("K", controller(blocks(definition.get())))
                            .where("X", blocks(AEBlocks.QUARTZ_VIBRANT_GLASS.block()))
                            .where("S", blocks(AEBlocks.INSCRIBER.block()))
                            .where("Z", blocks(MACHINE_CASING_MV.get()))
                            .where("C", blocks(CASING_STEEL_SOLID.get())
                                    .setMinGlobalLimited(10)
                                    .or(autoAbilities(definition.getRecipeTypes()))
                                    .or(autoAbilities(true, false, true))
                            )
                            .where("O", abilities(PartAbility.MUFFLER))
                            .build())
            .workableCasingModel(
                    GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/multiblock/gcym/large_mixer")
            )
            .register();

    public static final MachineDefinition LARGE_MEKANISM_NUCLEAR_REACTOR = REGISTRATE.multiblock(
                    "large_mekanism_nuclear_reactor",
                    WorkableElectricMultiblockMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .generator(true)
            .recipeType(SFTRecipeTypes.MEKANISM_NUCLEAR_REACTION_RECIPES)
            .recipeModifiers(OC_PERFECT_SUBTICK, BATCH_MODE)
            .tooltips(Component.translatable("sftcore.multiblock.perfect_overclock.tooltip"),
                    Component.translatable("sftcore.multiblock.perfect_overclock.tooltip.1"),
                    Component.translatable("sftcore.multiblock.allow_laser"),
                    Component.translatable("sftcore.multiblock.allow_laser.1"))
            .appearanceBlock(GeneratorsBlocks.FUSION_REACTOR_FRAME::getBlock)
            .pattern(definition ->
                    FactoryBlockPattern.start()
                            .aisle("   AAAAA   ", "   ABBBA   ", "   ABBBA   ", "   ABBBA   ", "   ABBBA   ", "   ABBBA   ", "   ABBBA   ", "   ABBBA   ", "   CCCCC   ", "   DBBBD   ", "   DBBBD   ", "   DBBBD   ", "   DBBBD   ", "   DBBBD   ", "   DBBBD   ", "   DDDDD   ", "   DDDDD   ")
                            .aisle("  AAAAAAA  ", "  AE   EA  ", "  AE   EA  ", "  AE   EA  ", "  AE   EA  ", "  AE   EA  ", "  AE   EA  ", "  AE   EA  ", "  CFFFFFC  ", "  B     B  ", "  B     B  ", "  B     B  ", "  B     B  ", "  B     B  ", "  B     B  ", "  D     D  ", "  DDDDDDD  ")
                            .aisle(" AAAAAAAAA ", " A       A ", " A       A ", " A       A ", " A       A ", " A       A ", " A       A ", " A       A ", " CFF   FFC ", " B       B ", " B       B ", " B       B ", " B       B ", " B       B ", " B       B ", " D       D ", " DDDDDDDDD ")
                            .aisle("AAAAAAAAAAA", "AE  GAG  EA", "AE  AHA  EA", "AE  GAG  EA", "AE  AHA  EA", "AE  GAG  EA", "AE  AHA  EA", "AE  GAG  EA", "CFF AHA FFC", "D   GAG   D", "D   AHA   D", "D   GAG   D", "D   AHA   D", "D   GAG   D", "D   AHA   D", "D   GAG   D", "DDDDDDDDDDD")
                            .aisle("AAAAAAAAAAA", "B  GGGGG  B", "B  A   A  B", "B  GGGGG  B", "B  A   A  B", "B  GGGGG  B", "B  A   A  B", "B  GGGGG  B", "CF A   A FC", "B  GGGGG  B", "B  A   A  B", "B  GGGGG  B", "B  A   A  B", "B  GGGGG  B", "B  A   A  B", "D  GGGGG  D", "DDDDDDDDDDD")
                            .aisle("AAAAAAAAAAA", "B  AG GA  B", "B  H   H  B", "B  AG GA  B", "B  H   H  B", "B  AG GA  B", "B  H   H  B", "B  AG GA  B", "CF H   H FC", "B  AG GA  B", "B  H   H  B", "B  AG GA  B", "B  H   H  B", "B  AG GA  B", "B  H   H  B", "D  AG GA  D", "DDDDDDDDDDD")
                            .aisle("AAAAAAAAAAA", "B  GGGGG  B", "B  A   A  B", "B  GGGGG  B", "B  A   A  B", "B  GGGGG  B", "B  A   A  B", "B  GGGGG  B", "CF A   A FC", "B  GGGGG  B", "B  A   A  B", "B  GGGGG  B", "B  A   A  B", "B  GGGGG  B", "B  A   A  B", "D  GGGGG  D", "DDDDDDDDDDD")
                            .aisle("AAAAAAAAAAA", "AE  GAG  EA", "AE  AHA  EA", "AE  GAG  EA", "AE  AHA  EA", "AE  GAG  EA", "AE  AHA  EA", "AE  GAG  EA", "CFF AHA FFC", "D   GAG   D", "D   AHA   D", "D   GAG   D", "D   AHA   D", "D   GAG   D", "D   AHA   D", "D   GAG   D", "DDDDDDDDDDD")
                            .aisle(" AAAAAAAAA ", " A       A ", " A       A ", " A       A ", " A       A ", " A       A ", " A       A ", " A       A ", " CFF   FFC ", " B       B ", " B       B ", " B       B ", " B       B ", " B       B ", " B       B ", " D       D ", " DDDDDDDDD ")
                            .aisle("  AAAAAAA  ", "  AE   EA  ", "  AE   EA  ", "  AE   EA  ", "  AE   EA  ", "  AE   EA  ", "  AE   EA  ", "  AE   EA  ", "  CFFFFFC  ", "  B     B  ", "  B     B  ", "  B     B  ", "  B     B  ", "  B     B  ", "  B     B  ", "  D     D  ", "  DDDDDDD  ")
                            .aisle("   AALAA   ", "   ABBBA   ", "   ABBBA   ", "   ABBBA   ", "   ABBBA   ", "   ABBBA   ", "   ABBBA   ", "   ABBBA   ", "   CCCCC   ", "   DBBBD   ", "   DBBBD   ", "   DBBBD   ", "   DBBBD   ", "   DBBBD   ", "   DBBBD   ", "   DDDDD   ", "   DDDDD   ")
                            .where("L", controller(blocks(definition.get())))
                            .where("A", blocks(GeneratorsBlocks.FUSION_REACTOR_FRAME.getBlock())
                                    .or(autoAbilities(definition.getRecipeTypes()))
                                    .or(abilities(PartAbility.OUTPUT_LASER).setMaxGlobalLimited(1)))
                            .where("C", blocks(FIREBOX_STEEL.get()))
                            .where("B", blocks(GeneratorsBlocks.REACTOR_GLASS.getBlock()))
                            .where("E", blocks(GeneratorsBlocks.FISSION_FUEL_ASSEMBLY.getBlock()))
                            .where("F", blocks(ChemicalHelper.getBlock(TagPrefix.frameGt, GTMaterials.BlackSteel)))
                            .where("G", blocks(COIL_CUPRONICKEL.get()))
                            .where("H", blocks(LAMPS.get(DyeColor.GREEN).get()))
                            .where("D", blocks(CASING_INVAR_HEATPROOF.get()))
                            .build())
            .workableCasingModel(SFTCore.id("block/casings/solid/mek_reactor_frame"),
                    GTCEu.id("block/multiblock/generator/large_plasma_turbine"))
            .register();

    public static final MachineDefinition COMMON_MEKANISM_PROCESS_FACTORY = REGISTRATE.multiblock("common_mekanism_process_factory", WorkableElectricMultiblockMachine::new)
            .rotationState(RotationState.ALL)
            .recipeType(SFTRecipeTypes.MEKANISM_PROCESSING_RECIPES)
            .recipeModifiers(PARALLEL_HATCH, OC_HALF_PERFECT)
            .appearanceBlock(MekanismBlocks.SPS_CASING::getBlock)
            .pattern(definition ->
                    FactoryBlockPattern.start()
                            .aisle("     ", " AAA ", " AAA ", " AAA ", "     ")
                            .aisle(" BBB ", "BCDCB", "BDDDB", "BCDCB", " BBB ")
                            .aisle(" BEB ", "BC CB", "E   E", "BC CB", " BEB ")
                            .aisle(" BEB ", "BC CB", "E   E", "BC CB", " BEB ")
                            .aisle(" BEB ", "BC CB", "E   E", "BC CB", " BEB ")
                            .aisle(" BEB ", "BC CB", "E   E", "BC CB", " BEB ")
                            .aisle(" BEB ", "BC CB", "E   E", "BC CB", " BEB ")
                            .aisle(" BEB ", "BC CB", "E   E", "BC CB", " BEB ")
                            .aisle(" BBB ", "BCDCB", "BDDDB", "BCDCB", " BBB ")
                            .aisle("     ", " AAA ", " ALA ", " AAA ", "     ")
                            .where("L", controller(blocks(definition.get())))
                            .where("A", blocks(MekanismBlocks.SPS_CASING.getBlock())
                                    .or(autoAbilities(definition.getRecipeTypes()))
                                    .or(autoAbilities(true, false, true)))
                            .where("B", blocks(MekanismBlocks.SPS_CASING.getBlock()))
                            .where("C", blocks(MekanismBlocks.SUPERHEATING_ELEMENT.getBlock()))
                            .where("E", blocks(GeneratorsBlocks.REACTOR_GLASS.getBlock()))
                            .where("D", blocks(GeneratorsBlocks.ELECTROMAGNETIC_COIL.getBlock()))
                            .build())
            .workableCasingModel(SFTCore.id("block/casings/solid/mek_sps_casing"),
                    GTCEu.id("block/multiblock/fusion_reactor"))
            .register();


    public static final MachineDefinition COMMON_FACTORY = REGISTRATE.multiblock("common_factory", CommonFactoryMachine::new)
            .rotationState(RotationState.ALL)
            .recipeTypes(CommonFactoryMachine.AVAILABLE_RECIPES)
            .tooltips(
                    Component.translatable("sftcore.machine.common_factory.tooltip.0"),
                    Component.translatable("sftcore.machine.common_factory.tooltip.1"),
                    Component.translatable("sftcore.machine.common_factory.tooltip.2"),
                    Component.translatable("sftcore.multiblock.energy_multiplier.tooltip", 0.9),
                    Component.translatable("sftcore.multiblock.time_multiplier.tooltip", 0.8),
                    SFTTooltips.structureComeFrom("GregTech Leisure")
            )
            .recipeModifiers(
                    SFTRecipeModifiers::commonFactoryParallel,
                    new SimpleMultiplierModifier(0.9, 0.8),
                    BATCH_MODE,
                    OC_NON_PERFECT
            )
            .appearanceBlock(MULTI_FUNCTIONAL_CASING)
            .pattern(definition ->
                    FactoryBlockPattern.start()
                            .aisle("AAA", "AAA", "AAA")
                            .aisle("AAA", "ACA", "AAA")
                            .aisle("AAA", "ASA", "AAA")
                            .where("S", controller(blocks(definition.get())))
                            .where("C", heatingCoils())
                            .where("A", blocks(MULTI_FUNCTIONAL_CASING.get())
                                    .setMinGlobalLimited(12)
                                    .or(autoAbilities(CommonFactoryMachine.AVAILABLE_RECIPES))
                                    .or(abilities(SFTPartAbility.MACHINE_ADJUSTMENT).setExactLimit(1))
                                    .or(abilities(PartAbility.MAINTENANCE).setExactLimit(1)))
                            .build())
            .workableCasingModel(SFTCore.id("block/casings/solid/multi_functional_casing"),
                    GTCEu.id("block/multiblock/gcym/large_mixer"))
            .register();

    public static final MachineDefinition DESULFURIZER = REGISTRATE.multiblock("desulfurizer",
                    WorkableElectricMultiblockMachine::new)
            .tooltips(Component.translatable("gtceu.multiblock.parallelizable.tooltip"),
                    Component.translatable("sftcore.multiblock.half_perfect_overclock.tooltip"),
                    Component.translatable("sftcore.multiblock.half_perfect_overclock.tooltip.1"))
            .rotationState(RotationState.ALL)
            .recipeType(SFTRecipeTypes.DESULFURIZE_RECIPES)
            .recipeModifiers(PARALLEL_HATCH, OC_HALF_PERFECT)
            .appearanceBlock(CASING_STAINLESS_TURBINE)
            .pattern(definition ->
                    FactoryBlockPattern.start()
                            .aisle(" AAA     ", " AAA     ", " AAA     ", "         ", " BBB     ", "         ", " BBB     ", "         ", " BBB     ", "         ", "         ", "         ")
                            .aisle("AAAAACDCE", "A   ACDCE", "A   ACDCE", " FFF     ", "BFFFB    ", " FFF     ", "BFFFB    ", " FFF     ", "BFFFB    ", " FFF     ", "         ", "         ")
                            .aisle("AAAAACDCE", "A GGGEECE", "A G ACECE", " FGF  E  ", "BFGFB E  ", " FGF  E  ", "BFGFB E  ", " FGF  E  ", "BFGFB E  ", " FGF  E  ", "  E   E  ", "  EEEEE  ")
                            .aisle("AAAAACDCE", "A   ACDCE", "A   ACDCE", " FFF     ", "BFFFB    ", " FFF     ", "BFFFB    ", " FFF     ", "BFFFB    ", " FFF     ", "         ", "         ")
                            .aisle(" AAA     ", " AIA     ", " AAA     ", "         ", " BBB     ", "         ", " BBB     ", "         ", " BBB     ", "         ", "         ", "         ")
                            .where("I", controller(blocks(definition.get())))
                            .where("A", blocks(CASING_STAINLESS_TURBINE.get())
                                    .setMinGlobalLimited(10)
                                    .or(autoAbilities(definition.getRecipeTypes()))
                                    .or(autoAbilities(true, false, true)))
                            .where("B", blocks(ChemicalHelper.getBlock(TagPrefix.frameGt, GTMaterials.StainlessSteel)))
                            .where("C", blocks(CASING_INVAR_HEATPROOF.get()))
                            .where("D", blocks(COIL_CUPRONICKEL.get()))
                            .where("E", blocks(CASING_STAINLESS_TURBINE.get()))
                            .where("F", blocks(CASING_STAINLESS_CLEAN.get()))
                            .where("G", blocks(CASING_TITANIUM_PIPE.get()))
                            .build())
            .workableCasingModel(GTCEu.id("block/casings/mechanic/machine_casing_turbine_stainless_steel"),
                    GTCEu.id("block/multiblock/large_chemical_reactor"))
            .register();

    public static final MachineDefinition HURRY_UP = REGISTRATE.multiblock("hurry_up",
                    WorkableElectricMultiblockMachine::new)
            .rotationState(RotationState.ALL)
            .recipeType(SFTRecipeTypes.HURRY_UP_RECIPES)
            .recipeModifiers(OC_NON_PERFECT)
            .appearanceBlock(TREATED_WOOD_PLANK)
            .pattern(definition ->
                    FactoryBlockPattern.start()
                            .aisle("AA", "AA", "AA", "AA", "AA", "AA", "AA", "CD", "DC")
                            .aisle("AB", "AA", "AA", "AA", "AA", "AA", "AA", "DC", "CD")
                            .where("B", controller(blocks(definition.get())))
                            .where("A", blocks(TREATED_WOOD_PLANK.get())
                                    .setMinGlobalLimited(20)
                                    .or(autoAbilities(definition.getRecipeTypes()))
                            )
                            .where("C", blocks(PLASTCRETE.get()))
                            .where("D", blocks(CASING_ALUMINIUM_FROSTPROOF.get()))
                            .build())
            .workableCasingModel(GTCEu.id("block/treated_wood_planks"),
                    GTCEu.id("block/multiblock/gcym/large_mixer"))
            .register();


    public static final MachineDefinition GREENHOUSE = REGISTRATE.multiblock("greenhouse",
                    WorkableElectricMultiblockMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(SFTRecipeTypes.GREENHOUSE_RECIPES)
            .recipeModifiers(OC_NON_PERFECT, BATCH_MODE)
            .appearanceBlock(CASING_STEEL_SOLID)
            .pattern(definition ->
                    FactoryBlockPattern.start()
                            .aisle(" AAA ", " AAA ", " BBB ", " BBB ", " BBB ", " BBB ", " BBB ", " CCC ", "     ")
                            .aisle("AAAAA", "ADDDA", "B   B", "B   B", "B   B", "B   B", "B   B", "CEEEC", " CCC ")
                            .aisle("AAAAA", "ADFDA", "B   B", "B   B", "B   B", "B   B", "B   B", "CEGEC", " CCC ")
                            .aisle("AAAAA", "ADDDA", "B   B", "B   B", "B   B", "B   B", "B   B", "CEEEC", " CCC ")
                            .aisle(" AAA ", " AHA ", " BBB ", " BBB ", " BBB ", " BBB ", " BBB ", " CCC ", "     ")
                            .where("H", controller(blocks(definition.get())))
                            .where("A", blocks(CASING_STEEL_SOLID.get())
                                    .setMinGlobalLimited(25)
                                    .or(autoAbilities(definition.getRecipeTypes()))
                                    .or(autoAbilities(true, false, true)))
                            .where("B", blocks(CASING_TEMPERED_GLASS.get()))
                            .where("C", blocks(CASING_STEEL_SOLID.get()))
                            .where("D", blocks(Blocks.MUD))
                            .where("E", blocks(CASING_GRATE.get()))
                            .where("F", fluids(Fluids.WATER))
                            .where("G", blocks(LAMPS.get(DyeColor.WHITE).get()))
                            .build())
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/multiblock/gcym/large_mixer"))
            .register();

    public static final MachineDefinition LARGE_GREENHOUSE = REGISTRATE.multiblock("large_greenhouse",
                    WorkableElectricMultiblockMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(SFTRecipeTypes.GREENHOUSE_RECIPES)
            .recipeModifiers(PARALLEL_HATCH, GCYM_MACHINE_REDUCE, OC_HALF_PERFECT, BATCH_MODE)
            .tooltips(
                    Component.translatable("gtceu.multiblock.parallelizable.tooltip"),
                    Component.translatable("gtceu.machine.available_recipe_map_1.tooltip",
                            Component.translatable("sftcore.greenhouse")),
                    Component.translatable("sftcore.multiblock.energy_multiplier.tooltip", GCYM_EUT_MULTIPLIER),
                    Component.translatable("sftcore.multiblock.time_multiplier.tooltip", GCYM_DURATION_MULTIPLIER),
                    Component.translatable("sftcore.multiblock.half_perfect_overclock.tooltip"),
                    Component.translatable("sftcore.multiblock.half_perfect_overclock.tooltip.1")
            )
            .appearanceBlock(CASING_STAINLESS_CLEAN)
            .pattern(definition ->
                    FactoryBlockPattern.start()
                            .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "BCCCCCCCCCCCCCCCCCCCCCCCCCCCB", "BCCCCCCCCCCCCCCCCCCCCCCCCCCCB", "BCCCCCCCCCCCCCCCCCCCCCCCCCCCB", "BCCCCCCCCCCCCCCCCCCCCCCCCCCCB", "                             ")
                            .aisle("ADDDDDDDDDDDDDDDDDDDDDDDDDDDA", "A                           A", "B                           B", "B                           B", "B                           B", "B                           B", "BCCCCCCCCCCCCCCCCCCCCCCCCCCCB")
                            .aisle("ADEEEEEEEEEEEEEEEEEEEEEEEEEDA", "A                           A", "C                           C", "C                           C", "C                           C", "B                           B", "BFFFFFFFFFFFFFFFFFFFFFFFFFFFB")
                            .aisle("ADEEEEEEEEEEEEEEEEEEEEEEEEEDA", "A                           A", "C                           C", "C                           C", "C                           C", "B                           B", "BGGGGGGGGGGGGGGGGGGGGGGGGGGGB")
                            .aisle("ADDDDDDDDDDDDDDDDDDDDDDDDDDDA", "A                           A", "C                           C", "C                           C", "C                           C", "B                           B", "BFFFFFFFFFFFFFFFFFFFFFFFFFFFB")
                            .aisle("ADHHHHHHHHHHHHHHHHHHHHHHHHHDA", "A                           A", "C                           B", "C                           B", "C                           B", "B                           B", "BCCCCCCCCCCCCCCCCCCCCCCCCCCCB")
                            .aisle("ADAAAAAAAAAAAAAAAAAAAAAAAAADA", "A                           A", "B                           B", "B                           B", "B                           B", "B                           B", "BCCCCCCCCCCCCCCCCCCCCCCCCCCCB")
                            .aisle("ADHHHHHHHHHHHHHHHHHHHHHHHHHDA", "A                           A", "C                           B", "C                           B", "C                           B", "B                           B", "BCCCCCCCCCCCCCCCCCCCCCCCCCCCB")
                            .aisle("ADDDDDDDDDDDDDDDDDDDDDDDDDDDA", "A                           A", "C                           C", "C                           C", "C                           C", "B                           B", "BFFFFFFFFFFFFFFFFFFFFFFFFFFFB")
                            .aisle("ADEEEEEEEEEEEEEEEEEEEEEEEEEDA", "A                           A", "C                           C", "C                           C", "C                           C", "B                           B", "BGGGGGGGGGGGGGGGGGGGGGGGGGGGB")
                            .aisle("ADEEEEEEEEEEEEEEEEEEEEEEEEEDA", "A                           A", "C                           C", "C                           C", "C                           C", "B                           B", "BFFFFFFFFFFFFFFFFFFFFFFFFFFFB")
                            .aisle("ADDDDDDDDDDDDDDDDDDDDDDDDDDDA", "A                           A", "B                           B", "B                           B", "B                           B", "B                           B", "BCCCCCCCCCCCCCCCCCCCCCCCCCCCB")
                            .aisle("AAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAIAAAAAAAAAAAAAA", "BCCCCCCCCCCCCCCCCCCCCCCCCCCCB", "BCCCCCCCCCCCCCCCCCCCCCCCCCCCB", "BCCCCCCCCCCCCCCCCCCCCCCCCCCCB", "BCCCCCCCCCCCCCCCCCCCCCCCCCCCB", "                             ")
                            .where("I", controller(blocks(definition.get())))
                            .where("A", blocks(CASING_STAINLESS_CLEAN.get())
                                    .or(autoAbilities(definition.getRecipeTypes()))
                                    .or(autoAbilities(true, false, true)))
                            .where("B", blocks(PLASTCRETE.get()))
                            .where("C", blocks(CLEANROOM_GLASS.get()))
                            .where("D", blocks(CASING_STAINLESS_CLEAN.get()))
                            .where("E", blocks(Blocks.MOSS_BLOCK))
                            .where("F", blocks(FILTER_CASING.get()))
                            .where("G", blocks(LAMPS.get(DyeColor.WHITE).get()))
                            .where("H", blocks(ChemicalHelper.getBlock(TagPrefix.frameGt, GTMaterials.StainlessSteel)))
                            .build())
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_clean_stainless_steel"),
                    GTCEu.id("block/multiblock/gcym/large_mixer"))
            .register();

    public static final MachineDefinition OIL_DRILLING_RIG = REGISTRATE.multiblock("oil_drilling_rig",
                    WorkableElectricMultiblockMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(SFTRecipeTypes.OIL_DRILLING_RECIPES)
            .recipeModifiers(PARALLEL_HATCH, OC_NON_PERFECT, BATCH_MODE)
            .appearanceBlock(CASING_STEEL_SOLID)
            .pattern(definition ->
                    FactoryBlockPattern.start()
                            .aisle("AAA", " B ", " B ", " B ", "   ", "   ", "   ")
                            .aisle("AAA", "B B", "B B", "B B", " B ", " B ", " B ")
                            .aisle("ACA", " B ", " B ", " B ", "   ", "   ", "   ")
                            .where("C", controller(blocks(definition.get())))
                            .where("A", blocks(CASING_STEEL_SOLID.get())
                                    .setMinGlobalLimited(4)
                                    .or(autoAbilities(definition.getRecipeTypes()))
                                    .or(autoAbilities(true, false, true)))
                            .where("B", blocks(ChemicalHelper.getBlock(TagPrefix.frameGt, GTMaterials.Steel)))
                            .build())
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/multiblock/gcym/large_mixer"))
            .register();

    public static final MachineDefinition LARGE_GAS_COLLECTOR = REGISTRATE.multiblock("large_gas_collector", WorkableElectricMultiblockMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(SFTRecipeTypes.LARGE_GAS_COLLECTOR_RECIPES)
            .recipeModifiers(SFTRecipeModifiers::gasCollectorParallel, OC_NON_PERFECT, BATCH_MODE)
            .appearanceBlock(CASING_STRESS_PROOF)
            .tooltips(Component.translatable("sftcore.machine.large_gas_collector.tooltip.1"))
            .pattern(definition ->
                    FactoryBlockPattern.start()
                            .aisle(" AAA   AAA ", " BBB   BBB ", " BBB   BBB ", " BBBCCCBBB ", " BBB   BBB ", " BBB   BBB ", " BBB   BBB ", "           ")
                            .aisle("AAAAA AAAAA", "B   B B   B", "B   B B   B", "B   BCB   B", "B   B B   B", "B   B B   B", "B   B B   B", " BBB   BBB ")
                            .aisle("AAAAA AAAAA", "B D B B D B", "B D B B D B", "B D BCB D B", "B D B B D B", "B D B B D B", "B D B B D B", " BBB   BBB ")
                            .aisle("AAAAA AAAAA", "B   B B   B", "B   B B   B", "B E BCB E B", "B   B B   B", "B   B B   B", "B   B B   B", " BBB   BBB ")
                            .aisle(" AAA   AAA ", " BBB   BBB ", " BBB   BBB ", " BEB C BEB ", " BBB   BBB ", " BBB   BBB ", " BBB   BBB ", "           ")
                            .aisle("           ", "           ", "  C     C  ", " CECCCCCEC ", "  C     C  ", "           ", "           ", "           ")
                            .aisle(" AAAAAAAAA ", " AAAAAAAAA ", " AAAAAAAAA ", " AEAAAAAAA ", " AAAAAAAAA ", "           ", "           ", "           ")
                            .aisle("AAAAAAAAAAA", " A       A ", " A       A ", " A       A ", "AFFFFFFFFFA", "           ", "           ", "           ")
                            .aisle("AAAAAAAAAAA", "A         A", "A         A", "A         A", "AFFFFFFFFFA", "           ", "           ", "           ")
                            .aisle("AAAAAAAAAAA", "A         A", "A         A", "A         A", "AFFFFFFFFFA", "           ", "           ", "           ")
                            .aisle("AAAAAAAAAAA", " A       A ", " A       A ", " A       A ", "AFFFFFFFFFA", "           ", "           ", "           ")
                            .aisle(" GGGGGGGGG ", "  GGGGGGG  ", "  GGGHGGG  ", "  GGGGGGG  ", " GGGGGGGGG ", "           ", "           ", "           ")
                            .where("H", controller(blocks(definition.get())))
                            .where("A", blocks(CASING_STRESS_PROOF.get()))
                            .where("B", blocks(CASING_CORROSION_PROOF.get()))
                            .where("C", blocks(ChemicalHelper.getBlock(TagPrefix.frameGt, GTMaterials.StainlessSteel)))
                            .where("D", blocks(HERMETIC_CASING_IV.get()))
                            .where("E", blocks(CASING_STEEL_PIPE.get()))
                            .where("F", blocks(CASING_GRATE.get()))
                            .where("G", blocks(CASING_STRESS_PROOF.get())
                                    .or(autoAbilities(definition.getRecipeTypes()))
                                    .or(abilities(PartAbility.MAINTENANCE).setExactLimit(1)))
                            .build())
            .workableCasingModel(GTCEu.id("block/casings/gcym/stress_proof_casing"),
                    GTCEu.id("block/multiblock/gcym/large_mixer"))
            .register();

    public static final MachineDefinition SEMICONDUCTOR_BLAST_FURNACE = REGISTRATE.multiblock("semiconductor_blast_furnace", CoilWorkableElectricMultiblockMachine::new)
            .rotationState(RotationState.ALL)
            .recipeType(SFTRecipeTypes.SEMICONDUCTOR_BLAST_RECIPES)
            .recipeModifiers(PARALLEL_HATCH, GTRecipeModifiers::ebfOverclock, BATCH_MODE)
            .appearanceBlock(CASING_HIGH_TEMPERATURE_SMELTING)
            .pattern(definition ->
                    FactoryBlockPattern.start()
                            .aisle("  AAAAA  ", "         ", "         ", "         ", "         ", "         ", "         ", "         ", "  BBBBB  ", "         ")
                            .aisle(" AAAAAAA ", "  ACCCA  ", "  AAAAA  ", "  BDDDB  ", "  BDDDB  ", "  BDDDB  ", "  BBBBB  ", "  BCCCB  ", " BBBBBBB ", "         ")
                            .aisle("AAAAAAAAA", " AA   AA ", " AA   AA ", " BB   BB ", " BB   BB ", " BB   BB ", " BB   BB ", " BB   BB ", "BBBBBBBBB", "   BBB   ")
                            .aisle("AAAAAAAAA", " C E E C ", " A E E A ", " D E E D ", " D E E D ", " D E E D ", " B E E B ", " C E E C ", "BBBE EBBB", "  BBBBB  ")
                            .aisle("AAAAAAAAA", " C     C ", " A     A ", " D     D ", " D     D ", " D     D ", " B     B ", " C     C ", "BBB   BBB", "  BBFBB  ")
                            .aisle("AAAAAAAAA", " C E E C ", " A E E A ", " D E E D ", " D E E D ", " D E E D ", " B E E B ", " C E E C ", "BBBE EBBB", "  BBBBB  ")
                            .aisle("AAAAAAAAA", " AA   AA ", " AA   AA ", " BB   BB ", " BB   BB ", " BB   BB ", " BB   BB ", " BB   BB ", "BBBBBBBBB", "   BBB   ")
                            .aisle(" AAAAAAA ", "  AAGAA  ", "  AAAAA  ", "  BDDDB  ", "  BDDDB  ", "  BDDDB  ", "  BBBBB  ", "  BCCCB  ", " BBBBBBB ", "         ")
                            .aisle("  AAAAA  ", "         ", "         ", "         ", "         ", "         ", "         ", "         ", "  BBBBB  ", "         ")
                            .where("G", controller(blocks(definition.get())))
                            .where("A", blocks(CASING_HIGH_TEMPERATURE_SMELTING.get())
                                    .or(autoAbilities(definition.getRecipeTypes()))
                                    .or(autoAbilities(true, false, true)))
                            .where("B", blocks(CASING_HIGH_TEMPERATURE_SMELTING.get()))
                            .where("C", blocks(HEAT_VENT.get()))
                            .where("D", heatingCoils())
                            .where("E", blocks(CASING_TITANIUM_PIPE.get()))
                            .where("F", abilities(PartAbility.MUFFLER))
                            .build()
            )
            .recoveryItems(
                    () -> new ItemLike[]{
                            GTMaterialItems.MATERIAL_ITEMS.get(TagPrefix.dustTiny, GTMaterials.Ash).get()})
            .workableCasingModel(GTCEu.id("block/casings/gcym/high_temperature_smelting_casing"),
                    GTCEu.id("block/multiblock/gcym/large_mixer"))
            .tooltips(Component.translatable("gtceu.multiblock.parallelizable.tooltip"),
                    Component.translatable("gtceu.machine.electric_blast_furnace.tooltip.0"),
                    Component.translatable("gtceu.machine.electric_blast_furnace.tooltip.1"),
                    Component.translatable("gtceu.machine.electric_blast_furnace.tooltip.2"))
            .additionalDisplay((controller, components) -> {
                if (controller instanceof CoilWorkableElectricMultiblockMachine coilMachine && controller.isFormed()) {
                    components.add(Component.translatable("gtceu.multiblock.blast_furnace.max_temperature",
                            Component.translatable(FormattingUtil.formatNumbers(coilMachine.getCoilType().getCoilTemperature() +
                                            100L * Math.max(0, coilMachine.getTier() - GTValues.MV)) + "K")
                                    .setStyle(Style.EMPTY.withColor(ChatFormatting.RED))));
                }
            })
            .register();

    public static final MachineDefinition LARGE_CRACKER = REGISTRATE.multiblock("large_cracker", CoilWorkableElectricMultiblockMachine::new)
            .rotationState(RotationState.ALL)
            .recipeType(GTRecipeTypes.CRACKING_RECIPES)
            .recipeModifiers(PARALLEL_HATCH, BATCH_MODE, SFTRecipeModifiers::largeCrackerOverlock, OC_HALF_PERFECT, GCYM_MACHINE_REDUCE)
            .tooltips(
                    Component.translatable("gtceu.multiblock.parallelizable.tooltip"),
                    Component.translatable(
                            "gtceu.machine.available_recipe_map_1.tooltip",
                            Component.translatable("gtceu.cracker")
                    ),
                    Component.translatable("sftcore.machine.large_cracker.tooltip.1"),
                    Component.translatable("sftcore.multiblock.energy_multiplier.tooltip", GCYM_EUT_MULTIPLIER),
                    Component.translatable("sftcore.multiblock.time_multiplier.tooltip", GCYM_DURATION_MULTIPLIER),
                    Component.translatable("sftcore.multiblock.half_perfect_overclock.tooltip"),
                    Component.translatable("sftcore.multiblock.half_perfect_overclock.tooltip.1"))
            .appearanceBlock(CASING_STAINLESS_CLEAN)
            .pattern(definition ->
                    FactoryBlockPattern.start()
                            .aisle("  AAAAAAA  ", "    AAA    ", "    AAA    ", "     B     ", "     B     ", "     B     ", "     B     ", "     B     ", "     B     ", "     B     ", "     B     ", "    BBB    ", "  BBBBBBB  ", "           ")
                            .aisle(" AAAAAAAAA ", " C DDDDD C ", " C DDDDD C ", " C DDDDD C ", " C DDDDD C ", " C DDDDD C ", " C DDDDD C ", " C DDDDD C ", " C DDDDD C ", " C DDDDD C ", " C DDDDD C ", " CBDDDDD C ", " BBBBBBBBB ", "   BBBBB   ")
                            .aisle("AAAAAAAAAAA", "  DDDDDDD  ", "  D     D  ", "  D     D  ", "  D     D  ", "  D     D  ", "  D     D  ", "  D     D  ", "  D     D  ", "  D     D  ", "  D     D  ", " BD     D  ", "BBBBBBBBBBB", "  BBBBBBB  ")
                            .aisle("AAAAAAAAAAA", " DDDDDDDDD ", " D EEEEE D ", " D       D ", " D EEEEE D ", " D       D ", " D EEEEE D ", " D       D ", " D EEEEE D ", " D       D ", " D EEEEE D ", " D       D ", "BBBBBBBBBBB", " BBBBBBBBB ")
                            .aisle("AAAAAAAAAAA", "ADDDDDDDDDA", "AD EC CE DA", " D  C C  D ", " D EC CE D ", " D  C C  D ", " D EC CE D ", " D  C C  D ", " D EC CE D ", " D  C C  D ", " D EC CE D ", "BD       DB", "BBBBBBBBBBB", " BBBBBBBBB ")
                            .aisle("AAAAAAAAAAA", "ADDDDDDDDDA", "AD E   E DA", "BD       DB", "BD E   E DB", "BD       DB", "BD E   E DB", "BD       DB", "BD E   E DB", "BD       DB", "BD E   E DB", "BD       DB", "BBBBBBBBBBB", " BBBBFBBBB ")
                            .aisle("AAAAAAAAAAA", "ADDDDDDDDDA", "AD EC CE DA", " D  C C  D ", " D EC CE D ", " D  C C  D ", " D EC CE D ", " D  C C  D ", " D EC CE D ", " D  C C  D ", " D EC CE D ", "BD       DB", "BBBBBBBBBBB", " BBBBBBBBB ")
                            .aisle("AAAAAAAAAAA", " DDDDDDDDD ", " D EEEEE D ", " D       D ", " D EEEEE D ", " D       D ", " D EEEEE D ", " D       D ", " D EEEEE D ", " D       D ", " D EEEEE D ", " D       D ", "BBBBBBBBBBB", " BBBBBBBBB ")
                            .aisle("AAAAAAAAAAA", "  DDDDDDD  ", "  D     D  ", "  D     D  ", "  D     D  ", "  D     D  ", "  D     D  ", "  D     D  ", "  D     D  ", "  D     D  ", "  D     D  ", "  D     D  ", "BBBBBBBBBBB", "  BBBBBBB  ")
                            .aisle(" AAAAAAAAA ", " C DDDDD C ", " C DDDDD C ", " C DDDDD C ", " C DGDGD C ", " C DGDGD C ", " C DGDGD C ", " C DGDGD C ", " C DGDGD C ", " C DGDGD C ", " C DDDDD C ", " C DDDDD C ", " BBBBBBBBB ", "   BBBBB   ")
                            .aisle("  AAAAAAA  ", "    AHA    ", "    AAA    ", "     B     ", "     B     ", "     B     ", "     B     ", "     B     ", "     B     ", "     B     ", "     B     ", "    BBB    ", "  BBBBBBB  ", "           ")
                            .where("H", controller(blocks(definition.get())))
                            .where("A", blocks(CASING_STAINLESS_CLEAN.get())
                                    .or(autoAbilities(definition.getRecipeTypes()))
                                    .or(autoAbilities(true, false, true)))
                            .where("B", blocks(CASING_STAINLESS_TURBINE.get()))
                            .where("C", blocks(ChemicalHelper.getBlock(TagPrefix.frameGt, GTMaterials.StainlessSteel)))
                            .where("D", blocks(CASING_STAINLESS_CLEAN.get()))
                            .where("E", heatingCoils())
                            .where("F", abilities(PartAbility.MUFFLER))
                            .where("G", blocks(CASING_TEMPERED_GLASS.get()))
                            .build())
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_clean_stainless_steel"),
                    GTCEu.id("block/multiblock/cracking_unit"))
            .register();

    public static final MachineDefinition CHEMICAL_FACTORY = REGISTRATE.multiblock("chemical_factory", CoilWorkableElectricMultiblockMachine::new)
            .rotationState(RotationState.ALL)
            .recipeType(LARGE_CHEMICAL_RECIPES)
            .recipeModifiers(
                    PARALLEL_HATCH,
                    OC_PERFECT_SUBTICK,
                    DEFAULT_ENVIRONMENT_REQUIREMENT,
                    BATCH_MODE,
                    MEGA_COIL_MACHINE_REDUCE)
            .tooltips(
                    Component.translatable("gtceu.multiblock.parallelizable.tooltip"),
                    Component.translatable(
                            "gtceu.machine.available_recipe_map_1.tooltip",
                            Component.translatable("gtceu.large_chemical_reactor")
                    ),
                    Component.translatable("sftcore.multiblock.mega_reduce_with_coil"),
                    Component.translatable("sftcore.multiblock.mega_reduce_with_coil.1"),
                    Component.translatable("sftcore.multiblock.perfect_overclock.tooltip"),
                    Component.translatable("sftcore.multiblock.perfect_overclock.tooltip.1"),
                    Component.translatable("sftcore.multiblock.allow_laser"),
                    Component.translatable("sftcore.multiblock.allow_laser.1")
            )
            .appearanceBlock(CASING_PTFE_INERT)
            .pattern(definition ->
                    FactoryBlockPattern.start()
                            .aisle("AAAAAAAAA", "         ", "         ", "         ", "         ", "AAAAAAAAA")
                            .aisle("AAAAAAAAA", " ABBBBBA ", " ACCCCCA ", " ACCCCCA ", " ABBBBBA ", "AAAAAAAAA")
                            .aisle("AAAAAAAAA", " ADDDDDA ", " C     C ", " C     C ", " ADDDDDA ", "AAAAAAAAA")
                            .aisle("AAAAAAAAA", " ADDDDDA ", " C     C ", " C     C ", " ADDDDDA ", "AAAAAAAAA")
                            .aisle("AAAAAAAAA", " ABBBBBA ", " ACCCCCA ", " ACCCCCA ", " ABBBBBA ", "AAAAAAAAA")
                            .aisle("AAAANAAAA", "         ", "         ", "         ", "         ", "AAAAAAAAA")
                            .where("N", controller(blocks(definition.getBlock())))
                            .where("A", blocks(CASING_PTFE_INERT.get())
                                    .or(autoAbilities(definition.getRecipeTypes()))
                                    .or(autoAbilities(true, false, true))
                                    .or(abilities(PartAbility.INPUT_LASER).setMaxGlobalLimited(1)))
                            .where("C", blocks(CASING_LAMINATED_GLASS.get()))
                            .where("B", blocks(CASING_POLYTETRAFLUOROETHYLENE_PIPE.get()))
                            .where("D", heatingCoils())
                            .build())
            .additionalDisplay((controller, components) -> {
                if (!controller.isFormed())
                    return;
                if (!(controller instanceof CoilWorkableElectricMultiblockMachine coilMachine))
                    return;
                components.add(
                        Component.translatable(
                                "gtceu.multiblock.blast_furnace.max_temperature",
                                Component.translatable(
                                        FormattingUtil.formatNumbers(
                                                coilMachine.getCoilType().getCoilTemperature()
                                        ) + "K"
                                )
                        )
                );
            })
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_inert_ptfe"),
                    GTCEu.id("block/multiblock/large_chemical_reactor"))
            .register();


    public static final MachineDefinition MEGA_ALLOY_BLAST_SMELTER = REGISTRATE.multiblock("mega_alloy_blast_smelter", CoilWorkableElectricMultiblockMachine::new)
            .rotationState(RotationState.ALL)
            .recipeModifiers(PARALLEL_HATCH,
                    BATCH_MODE,
                    GTRecipeModifiers::ebfOverclock,
                    GCYM_MACHINE_REDUCE,
                    MEGA_COIL_MACHINE_REDUCE)
            .recipeType(ALLOY_BLAST_RECIPES)
            .tooltips(Component.translatable("gtceu.multiblock.parallelizable.tooltip"),
                    Component.translatable(
                            "gtceu.machine.available_recipe_map_1.tooltip",
                            Component.translatable("gtceu.alloy_blast_smelter")
                    ),
                    Component.translatable("gtceu.machine.electric_blast_furnace.tooltip.0"),
                    Component.translatable("gtceu.machine.electric_blast_furnace.tooltip.1"),
                    Component.translatable("gtceu.machine.electric_blast_furnace.tooltip.2"),
                    Component.translatable(
                            "sftcore.multiblock.energy_multiplier.tooltip", 0.8
                    ),
                    Component.translatable(
                            "sftcore.multiblock.time_multiplier.tooltip", 0.6
                    ),
                    Component.translatable("sftcore.multiblock.mega_reduce_with_coil"),
                    Component.translatable("sftcore.multiblock.mega_reduce_with_coil.1"),
                    Component.translatable("sftcore.multiblock.allow_laser"),
                    Component.translatable("sftcore.multiblock.allow_laser.1")
            )
            .appearanceBlock(GCYMBlocks.CASING_HIGH_TEMPERATURE_SMELTING)
            .pattern(definition ->
                    FactoryBlockPattern.start()
                            .aisle("    AAAAAAA     ", "    BCCCCCB     ", "    BBBBBBB     ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ")
                            .aisle("   ADDDDDDDA    ", "   BEFFFFFEB    ", "   BBDDDDDBB    ", "     BBBBB      ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ")
                            .aisle("  ADDDDDDDDDA   ", "  BEFFFFFFFEB   ", "  BBDDDDDDDBB   ", "    BBAAABB     ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ")
                            .aisle(" ADDDDDDDDDDDA  ", " BEFFFFFFFFFEB  ", " BBDDDDDDDDDBB  ", "   BBDDDDDBB    ", "     GGGGG      ", "     GGGGG      ", "     GGGGG      ", "     GGGGG      ", "     GGGGG      ", "     HCCCH      ", "                ", "                ", "                ", "                ", "                ", "     IIIII      ")
                            .aisle("ADDDDDDDDDDDDDA ", "BEFFFFJJJFFFFEB ", "BBDDDDDDDDDDDBB ", "  BBDDDDDDDBB   ", "    GKKKKKG     ", "    GKKKKKG     ", "    GKKKKKG     ", "    GKKKKKG     ", "    GKKKKKG     ", "    HHDDDHH     ", "      GGG       ", "      GGG       ", "      GGG       ", "      GGG       ", "      GGG       ", "    IIIIIII     ")
                            .aisle("ADDDDDDDDDDDDDA ", "CFFFFJFFFJFFFFC ", "BDDDDDFFFDDDDDB ", " BBDDDFFFDDDBB  ", "   GKDFFFDKG    ", "   GKDFFFDKG    ", "   GKDFFFDKG    ", "   GKDFFFDKG    ", "   GKDFFFDKG    ", "   HHDDDDDHH    ", "     GKKKG      ", "     GKKKG      ", "     GKKKG      ", "     GKKKG      ", "     GKKKG      ", "   IIIIIIIII    ")
                            .aisle("ADDDDDDDDDDDDDA ", "CFFFJFFFFFJFFFC ", "BDDDDFFFFFDDDDB ", " BADDFFFFFDDAB  ", "   GKFFFFFKG    ", "   GKFFFFFKG    ", "   GKFFFFFKG    ", "   GKFFFFFKG    ", "   GKFFFFFKG    ", "   CDDFFFDDC    ", "    GKFFFKG     ", "    GKFFFKG     ", "    GKFFFKG     ", "    GKFFFKG     ", "    GKFFFKG     ", "   IIIIIIIII    ")
                            .aisle("ADDDDDDDDDDDDDA ", "CFFFJFFFFFJFFFC ", "BDDDDFFFFFDDDDB ", " BADDFFFFFDDAB  ", "   GKFFFFFKG    ", "   GKFFFFFKG    ", "   GKFFFFFKG    ", "   GKFFFFFKG    ", "   GKFFFFFKG    ", "   CDDFFFDDC    ", "    GKFFFKG     ", "    GKFFFKG     ", "    GKFFFKG     ", "    GKFFFKG     ", "    GKFFFKG     ", "   IIIIMIIII    ")
                            .aisle("ADDDDDDDDDDDDDA ", "CFFFJFFFFFJFFFC ", "BDDDDFFFFFDDDDB ", " BADDFFFFFDDAB  ", "   GKFFFFFKG    ", "   GKFFFFFKG    ", "   GKFFFFFKG    ", "   GKFFFFFKG    ", "   GKFFFFFKG    ", "   CDDFFFDDC    ", "    GKFFFKG     ", "    GKFFFKG     ", "    GKFFFKG     ", "    GKFFFKG     ", "    GKFFFKG     ", "   IIIIIIIII    ")
                            .aisle("ADDDDDDDDDDDDDA ", "CFFFFJFFFJFFFFC ", "BDDDDDFFFDDDDDB ", " BBDDDFFFDDDBB  ", "   GKDFFFDKG    ", "   GKDFFFDKG    ", "   GKDFFFDKG    ", "   GKDFFFDKG    ", "   GKDFFFDKG    ", "   HHDDDDDHH    ", "     GKKKG      ", "     GKKKG      ", "     GKKKG      ", "     GKKKG      ", "     GKKKG      ", "   IIIIIIIII    ")
                            .aisle("ADDDDDDDDDDDDDA ", "BEFFFFJJJFFFFEB ", "BBDDDDDDDDDDDBB ", "  BBDDDDDDDBB   ", "    GKKKKKG     ", "    GKKKKKG     ", "    GKKKKKG     ", "    GKKKKKG     ", "    GKKKKKG     ", "    HHDDDHH     ", "      GGG       ", "      GGG       ", "      GGG       ", "      GGG       ", "      GGG       ", "    IIIIIII     ")
                            .aisle(" ADDDDDDDDDDDA  ", " BEFFFFFFFFFEB  ", " BBDDDDDDDDDBB  ", "   BBDDDDDBB    ", "     GGGGG      ", "     GGGGG      ", "     GGGGG      ", "     GGGGG      ", "     GGGGG      ", "     HCCCH      ", "                ", "                ", "                ", "                ", "                ", "     IIIII      ")
                            .aisle("  ADDDDDDDDDA   ", "  BEFFFFFFFEB   ", "  BBDDDDDDDBB   ", "    BBAAABB     ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ")
                            .aisle("   ADDDDDDDA    ", "   BEFFFFFEB    ", "   BBDDDDDBB    ", "     BBBBB      ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ")
                            .aisle("    AAAAAAA     ", "    BCCCCCB     ", "    BBBSBBB     ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ", "                ")
                            .where("S", controller(blocks(definition.get())))
                            .where("A", blocks(HEAT_VENT.get()))
                            .where("B", blocks(GCYMBlocks.CASING_HIGH_TEMPERATURE_SMELTING.get())
                                    .or(autoAbilities(definition.getRecipeTypes()))
                                    .or(autoAbilities(true, false, true))
                                    .or(abilities(PartAbility.INPUT_LASER).setMaxGlobalLimited(1))
                            )
                            .where("C", blocks(CASING_EXTREME_ENGINE_INTAKE.get()))
                            .where("D", blocks(GCYMBlocks.CASING_HIGH_TEMPERATURE_SMELTING.get()))
                            .where("E", blocks(CASING_STAINLESS_STEEL_GEARBOX.get()))
                            .where("F", air())
                            .where("G", blocks(CASING_TEMPERED_GLASS.get()))
                            .where("H", blocks(CASING_TUNGSTENSTEEL_PIPE.get()))
                            .where("I", blocks(CASING_TUNGSTENSTEEL_ROBUST.get()))
                            .where("J", blocks(SUPERCONDUCTING_COIL.get()))
                            .where("K", heatingCoils())
                            .where("M", abilities(PartAbility.MUFFLER))
                            .build())
            .additionalDisplay((controller, components) -> {
                if (!controller.isFormed())
                    return;
                if (!(controller instanceof CoilWorkableElectricMultiblockMachine coilMachine))
                    return;
                components.add(
                        Component.translatable(
                                "gtceu.multiblock.blast_furnace.max_temperature",
                                Component.translatable(
                                        FormattingUtil.formatNumbers(
                                                coilMachine.getCoilType().getCoilTemperature() +
                                                        100 * Math.max(0, coilMachine.getTier() - GTValues.MV)
                                        ) + "K"
                                )
                        )
                );
            })
            .workableCasingModel(
                    GTCEu.id("block/casings/gcym/high_temperature_smelting_casing"),
                    GTCEu.id("block/multiblock/gcym/mega_blast_furnace"))
            .register();

}
