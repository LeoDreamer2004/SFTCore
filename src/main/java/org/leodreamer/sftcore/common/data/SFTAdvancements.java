package org.leodreamer.sftcore.common.data;

import org.leodreamer.sftcore.common.advancement.SFTAdvancementBuilder;
import org.leodreamer.sftcore.common.advancement.SFTCriteriaTriggers;
import org.leodreamer.sftcore.common.advancement.trigger.WireBurnedTrigger;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.common.data.machines.GCYMMachines;
import com.gregtechceu.gtceu.common.data.machines.GTMultiMachines;
import com.gregtechceu.gtceu.common.data.machines.GTResearchMachines;

import net.minecraft.advancements.Advancement;
import net.minecraft.world.item.Items;

import com.tterrag.registrate.providers.RegistrateAdvancementProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public final class SFTAdvancements {

    public static final List<Advancement> ADVANCEMENTS = new ArrayList<>();

    public static final Advancement ROOT = SFTAdvancementBuilder.create("root")
        .silent()
        .display(
            GTItems.TERMINAL,
            "Welcome to GregTech Modern",
            "This is an unofficial advancement system for GTM, authored by LeoDreamer in Starter For Technology."
        )
        .free()
        .buildAndRegister();

    /***********************************************
     * GregTech Modern
     ***********************************************/

    // ULV
    public static final Advancement BRONZE = SFTAdvancementBuilder.create("bronze")
        .parent(ROOT)
        .goal()
        .display(
            ChemicalHelper.get(TagPrefix.ingot, GTMaterials.Bronze).getItem(),
            "Primitive Alloy",
            "Get a bronze ingot"
        )
        .obtain(ChemicalHelper.get(TagPrefix.ingot, GTMaterials.Bronze).getItem())
        .buildAndRegister();

    public static final Advancement VACUUM_TUBE = SFTAdvancementBuilder.create("vacuum_tube")
        .parent(BRONZE)
        .display(
            GTItems.VACUUM_TUBE,
            "Not really vacuum",
            "Craft a vacuum tube"
        )
        .obtain(GTItems.VACUUM_TUBE)
        .buildAndRegister();

    public static final Advancement BASIC_ELECTRONIC_CIRCUIT = SFTAdvancementBuilder.create("basic_electronic_circuit")
        .parent(VACUUM_TUBE)
        .display(
            GTItems.ELECTRONIC_CIRCUIT_LV,
            "Information Age?",
            "Craft your first circuit"
        )
        .obtain(GTItems.ELECTRONIC_CIRCUIT_LV)
        .buildAndRegister();

    public static final Advancement COKE_OVEN = SFTAdvancementBuilder.create("coke_oven")
        .parent(BRONZE)
        .display(
            GTMultiMachines.COKE_OVEN.getItem(),
            "Better Than Furnaces!",
            "Build a coke oven"
        )
        .form(GTMultiMachines.COKE_OVEN)
        .buildAndRegister();

    public static final Advancement PRIMITIVE_PUMP = SFTAdvancementBuilder.create("primitive_pump")
        .parent(COKE_OVEN)
        .display(
            GTMultiMachines.PRIMITIVE_PUMP.getItem(),
            "Remember the One Who Dug the Well",
            "Build a primitive pump to obtain infinite water"
        )
        .form(GTMultiMachines.PRIMITIVE_PUMP)
        .buildAndRegister();

    public static final Advancement LARGE_BRONZE_BOILER = SFTAdvancementBuilder.create("large_bronze_boiler")
        .parent(PRIMITIVE_PUMP)
        .display(
            GTMultiMachines.LARGE_BOILER_BRONZE.getItem(),
            "All Industry Comes Down to Boiling Water",
            "Build a large bronze boiler"
        )
        .form(GTMultiMachines.LARGE_BOILER_BRONZE)
        .buildAndRegister();

    public static final Advancement STEAM_PARALLEL_MULTIBLOCK = SFTAdvancementBuilder
        .create("steam_parallel_multiblock")
        .parent(LARGE_BRONZE_BOILER)
        .any()
        .display(
            GTMultiMachines.STEAM_GRINDER.getItem(),
            "The Mighty Power of the Steam Age",
            "Build a steam multiblock with parallels"
        )
        .form(GTMultiMachines.STEAM_GRINDER)
        .form(GTMultiMachines.STEAM_OVEN)
        .buildAndRegister();

    public static final Advancement PRIMITIVE_BLAST_FURNACE = SFTAdvancementBuilder.create("primitive_blast_furnace")
        .parent(BRONZE)
        .display(
            GTMultiMachines.PRIMITIVE_BLAST_FURNACE.getItem(),
            "We are marching on the great road!",
            "Build a primitive blast furnace and make it formed"
        )
        .form(GTMultiMachines.PRIMITIVE_BLAST_FURNACE)
        .buildAndRegister();

    // LV
    public static final Advancement STEEL = SFTAdvancementBuilder.create("steel")
        .parent(PRIMITIVE_BLAST_FURNACE)
        .goal()
        .display(
            ChemicalHelper.get(TagPrefix.ingot, GTMaterials.Steel).getItem(),
            "Heavy Industry",
            "Get a steel ingot from your primitive blast furnace!"
        )
        .obtain(ChemicalHelper.get(TagPrefix.ingot, GTMaterials.Steel).getItem())
        .buildAndRegister();

    public static final Advancement WIRE_BURNED = SFTAdvancementBuilder.create("wire_burned")
        .parent(STEEL)
        .goal()
        .hidden()
        .display(
            ChemicalHelper.get(TagPrefix.wireGtSingle, GTMaterials.Copper).getItem(),
            "Electricity Safety",
            "Good news, your cable has become a very expensive fuse."
        )
        .criterion("wire_burned", WireBurnedTrigger.Instance.burned())
        .buildAndRegister();

    public static final Advancement MACHINE_EXPLODED = SFTAdvancementBuilder.create("machine_exploded")
        .parent(WIRE_BURNED)
        .challenge()
        .hidden()
        .display(
            Items.TNT,
            "GregTech? Piece of Cake!",
            "Maybe it is time to think very carefully about rolling back that save..."
        )
        .simple(SFTCriteriaTriggers.MACHINE_EXPLODED)
        .buildAndRegister();

    public static final Advancement ELECTRIC_BLAST_FURNACE = SFTAdvancementBuilder.create("electric_blast_furnace")
        .parent(STEEL)
        .display(
            GTMultiMachines.ELECTRIC_BLAST_FURNACE.getItem(),
            "Joule's Law",
            "Heat your metal with electricity and coils!"
        )
        .form(GTMultiMachines.ELECTRIC_BLAST_FURNACE)
        .buildAndRegister();

    public static final Advancement DUCT_TAPED_MAINTENANCE = SFTAdvancementBuilder.create("duct_taped_maintenance")
        .parent(ELECTRIC_BLAST_FURNACE)
        .display(
            GTItems.DUCT_TAPE,
            "Tape!",
            "Use duct tape on a maintenance hatch to keep it working for a while"
        )
        .simple(SFTCriteriaTriggers.DUCT_TAPED_MAINTENANCE)
        .buildAndRegister();

    public static final Advancement AUTO_MAINTENANCE = SFTAdvancementBuilder.create("auto_maintenance")
        .parent(DUCT_TAPED_MAINTENANCE)
        .display(
            GTMachines.AUTO_MAINTENANCE_HATCH.getItem(),
            "No More Maintenance",
            "Craft an auto maintenance hatch to automatically maintain your machine"
        )
        .obtain(GTMachines.AUTO_MAINTENANCE_HATCH.getItem())
        .buildAndRegister();

    public static final Advancement SHARED_MULTIBLOCK_PART = SFTAdvancementBuilder.create("shared_multiblock_part")
        .parent(ELECTRIC_BLAST_FURNACE)
        .display(
            Items.CHAIN,
            "Cut Corners",
            "Share a part with another machine"
        )
        .simple(SFTCriteriaTriggers.SHARED_MULTIBLOCK_PART)
        .buildAndRegister();

    public static final Advancement WORLD_ACCELERATOR = SFTAdvancementBuilder.create("world_accelerator")
        .parent(ELECTRIC_BLAST_FURNACE)
        .any()
        .display(
            GTMachines.WORLD_ACCELERATOR[GTValues.LV].getItem(),
            "Accel World",
            "Craft a world accelerator"
        )
        .obtain(
            Arrays.stream(GTMachines.WORLD_ACCELERATOR)
                .filter(Objects::nonNull)
                .map(MachineDefinition::getItem)
                .toList()
        )
        .buildAndRegister();

    // MV
    public static final Advancement ALUMINIUM = SFTAdvancementBuilder.create("aluminum")
        .parent(ELECTRIC_BLAST_FURNACE)
        .goal()
        .display(
            ChemicalHelper.get(TagPrefix.ingot, GTMaterials.Aluminium).getItem(),
            "\"Light\" Industry",
            "Get an aluminum ingot from your blast furnace"
        )
        .obtain(ChemicalHelper.get(TagPrefix.ingot, GTMaterials.Aluminium).getItem())
        .buildAndRegister();

    public static final Advancement TRANSFORMER = SFTAdvancementBuilder.create("transformer")
        .parent(ALUMINIUM)
        .any()
        .display(
            GTMachines.TRANSFORMER[GTValues.LV].getItem(),
            "Transformer architecture",
            "Craft a transformer to step down/up your voltage"
        )
        .obtain(
            Stream.of(
                GTMachines.TRANSFORMER,
                GTMachines.HI_AMP_TRANSFORMER_2A,
                GTMachines.HI_AMP_TRANSFORMER_4A,
                GTMachines.POWER_TRANSFORMER
            )
                .flatMap(Arrays::stream)
                .filter(Objects::nonNull)
                .map(MachineDefinition::getItem)
                .toList()
        )
        .buildAndRegister();

    public static final Advancement CENTRAL_MONITOR_MODULE = SFTAdvancementBuilder
        .create("central_monitor_module")
        .parent(ALUMINIUM)
        .display(
            GTMultiMachines.CENTRAL_MONITOR.getItem(),
            "Factory Dashboard",
            "Install any module into a Central Monitor group."
        )
        .simple(SFTCriteriaTriggers.CENTRAL_MONITOR_MODULE_INSTALLED)
        .buildAndRegister();

    public static final Advancement POLYETHYLENE = SFTAdvancementBuilder.create("polyethene")
        .parent(ALUMINIUM)
        .any()
        .display(
            ChemicalHelper.get(TagPrefix.ingot, GTMaterials.Polyethylene).getItem(),
            "Plastic",
            "Craft polyethene from chemical reactor"
        )
        .recipeExecute(
            GTRecipeTypes.CHEMICAL_RECIPES,
            "polyethylene_from_air",
            "polyethylene_from_oxygen"
        )
        .recipeExecute(
            GTRecipeTypes.LARGE_CHEMICAL_RECIPES,
            "polyethylene_from_tetrachloride_air",
            "polyethylene_from_tetrachloride_oxygen"
        )
        .buildAndRegister();

    public static final Advancement SUPER_TANK = SFTAdvancementBuilder.create("super_tank")
        .parent(POLYETHYLENE)
        .any()
        .display(
            GTMachines.SUPER_TANK[GTValues.LV].getItem(),
            "Compact Power, Profound Impact",
            "Craft a super tank"
        )
        .obtain(
            Arrays.stream(GTMachines.SUPER_TANK)
                .filter(Objects::nonNull)
                .map(MachineDefinition::getItem)
                .toList()
        )
        .buildAndRegister();

    public static final Advancement SILICON_BOULE = SFTAdvancementBuilder.create("silicon_boule")
        .parent(ELECTRIC_BLAST_FURNACE)
        .display(
            GTItems.SILICON_BOULE,
            "7 Minutes for Silicon",
            "Develop... or blast a silicon boule"
        )
        .recipeExecute(
            GTRecipeTypes.BLAST_RECIPES, "silicon_boule"
        )
        .buildAndRegister();

    public static final Advancement BATHING_TO_COOL = SFTAdvancementBuilder.create("bathing_to_cool")
        .parent(ALUMINIUM)
        .display(
            GTMaterials.DistilledWater.getBucket(),
            "Take a bath",
            "Cool down a hot ingot with bathing"
        )
        .recipeExecute(
            GTRecipeTypes.CHEMICAL_BATH_RECIPES,
            "kanthal_cool_down",
            "kanthal_cool_down_distilled_water",
            "silicon_cool_down",
            "silicon_cool_down_distilled_water",
            "black_steel_cool_down",
            "black_steel_cool_down_distilled_water",
            "red_steel_cool_down",
            "red_steel_cool_down_distilled_water",
            "blue_steel_cool_down",
            "blue_steel_cool_down_distilled_water"
        )
        .buildAndRegister();

    // HV
    public static final Advancement STAINLESS_STEEL = SFTAdvancementBuilder.create("stainless_steel")
        .parent(BATHING_TO_COOL)
        .goal()
        .display(
            ChemicalHelper.get(TagPrefix.ingot, GTMaterials.StainlessSteel).getItem(),
            "Modern Industrialization",
            "Get a stainless steel for HV"
        )
        .obtain(ChemicalHelper.get(TagPrefix.ingot, GTMaterials.StainlessSteel).getItem())
        .buildAndRegister();

    public static final Advancement CLEANROOM = SFTAdvancementBuilder.create("cleanroom")
        .parent(STAINLESS_STEEL)
        .display(
            GTMultiMachines.CLEANROOM.getItem(),
            "Spotless",
            "Build a cleanroom to craft advanced circuits."
        )
        .form(GTMultiMachines.CLEANROOM)
        .buildAndRegister();

    public static final Advancement MAX_CLEANROOM_CLEAN = SFTAdvancementBuilder.create("max_cleanroom_clean")
        .parent(CLEANROOM)
        .goal()
        .display(
            GTMultiMachines.CLEANROOM.getItem(),
            "Technology is dear to me, but dearer still is cleanliness",
            "Make a max size cleanroom clean up"
        )
        .simple(SFTCriteriaTriggers.MAX_CLEANROOM_CLEAN)
        .buildAndRegister();

    public static final Advancement MICRO_MAINFRAME = SFTAdvancementBuilder
        .create("micro_mainframe")
        .parent(CLEANROOM)
        .display(
            GTItems.MAINFRAME_IV,
            "Can I Play Minecraft?",
            "Craft a micro-processor mainframe"
        )
        .obtain(GTItems.MAINFRAME_IV)
        .buildAndRegister();

    public static final Advancement OIL = SFTAdvancementBuilder.create("oil")
        .parent(STAINLESS_STEEL)
        .any()
        .display(
            GTMaterials.Oil.getBucket(),
            "Black Gold",
            "Mine endless oil with a drilling dig"
        )
        .form(GTMultiMachines.FLUID_DRILLING_RIG[GTValues.MV])
        .form(GTMultiMachines.FLUID_DRILLING_RIG[GTValues.HV])
        .form(GTMultiMachines.FLUID_DRILLING_RIG[GTValues.EV])
        .buildAndRegister();

    public static final Advancement LARGE_CHEMICAL_REACTOR = SFTAdvancementBuilder.create("large_chemical_reactor")
        .parent(OIL)
        .display(
            GTMultiMachines.LARGE_CHEMICAL_REACTOR.getItem(),
            "Dark Mage",
            "Build a large chemical reactor to process on a large scale"
        )
        .form(GTMultiMachines.LARGE_CHEMICAL_REACTOR)
        .buildAndRegister();

    public static final Advancement PETROCHEMICAL = SFTAdvancementBuilder.create("petrochemical")
        .parent(LARGE_CHEMICAL_REACTOR)
        .goal()
        .display(
            GTMultiMachines.DISTILLATION_TOWER.getItem(),
            "Petrochemical",
            "Process oil into petrochemical products"
        )
        .form(GTMultiMachines.DISTILLATION_TOWER)
        .form(GTMultiMachines.CRACKER)
        .form(GTMultiMachines.LARGE_CHEMICAL_REACTOR)
        .buildAndRegister();

    public static final Advancement OXYGEN_BOOSTED_COMBUSTION = SFTAdvancementBuilder
        .create("oxygen_boosted_combustion")
        .parent(OIL)
        .display(
            GTMultiMachines.LARGE_COMBUSTION_ENGINE.getItem(),
            "Oxy-Fuel Combustion",
            "Feed oxygen or liquid oxygen into a large combustion engine to boost its output."
        )
        .simple(SFTCriteriaTriggers.OXYGEN_BOOSTED_COMBUSTION)
        .buildAndRegister();

    public static final Advancement SMD = SFTAdvancementBuilder.create("smd")
        .parent(STAINLESS_STEEL)
        .display(
            GTItems.SMD_CAPACITOR,
            "Real: Getting an Upgrade",
            "Craft all 5 SMDs"
        )
        .obtain(GTItems.SMD_CAPACITOR)
        .obtain(GTItems.SMD_DIODE)
        .obtain(GTItems.SMD_INDUCTOR)
        .obtain(GTItems.SMD_RESISTOR)
        .obtain(GTItems.SMD_TRANSISTOR)
        .buildAndRegister();

    public static final Advancement LARGE_TURBINE_FULL_SPEED = SFTAdvancementBuilder
        .create("large_turbine_full_speed")
        .hidden()
        .parent(STAINLESS_STEEL)
        .display(
            GTMultiMachines.LARGE_STEAM_TURBINE.getItem(),
            "Run with the Wind",
            "Run a large turbine until its rotor reaches maximum speed."
        )
        .simple(SFTCriteriaTriggers.LARGE_TURBINE_FULL_SPEED)
        .buildAndRegister();

    // EV
    public static final Advancement TITANIUM = SFTAdvancementBuilder.create("titanium")
        .parent(LARGE_CHEMICAL_REACTOR)
        .goal()
        .display(
            ChemicalHelper.get(TagPrefix.ingot, GTMaterials.Titanium).getItem(),
            "Metal for 21th Century",
            "Extract titanium from Rutile"
        )
        .obtain(ChemicalHelper.get(TagPrefix.ingot, GTMaterials.Titanium).getItem())
        .buildAndRegister();

    public static final Advancement NANO_MAINFRAME = SFTAdvancementBuilder.create("nano_mainframe")
        .parent(TITANIUM)
        .display(
            GTItems.NANO_MAINFRAME_LuV,
            "Do you believe in Moore's Law?",
            "Craft a nano mainframe"
        )
        .obtain(GTItems.NANO_MAINFRAME_LuV)
        .buildAndRegister();

    public static final Advancement TUNGSTEN = SFTAdvancementBuilder.create("tungsten")
        .parent(TITANIUM)
        .display(
            ChemicalHelper.get(TagPrefix.dust, GTMaterials.Tungsten).getItem(),
            "Wolfram Alpha",
            "Electrolyze to get Tungsten"
        )
        .obtain(ChemicalHelper.get(TagPrefix.dust, GTMaterials.Tungsten).getItem())
        .buildAndRegister();

    public static final Advancement PLATINUM_GROUP = SFTAdvancementBuilder.create("platinum_group")
        .parent(TITANIUM)
        .display(
            ChemicalHelper.get(TagPrefix.dust, GTMaterials.Platinum).getItem(),
            "50 Hours' of Platinum Chain",
            "Congratulations! You overcame such a long and boring processing"
        )
        .obtain(ChemicalHelper.get(TagPrefix.dust, GTMaterials.Platinum).getItem())
        .obtain(ChemicalHelper.get(TagPrefix.dust, GTMaterials.Palladium).getItem())
        .obtain(ChemicalHelper.get(TagPrefix.dust, GTMaterials.Ruthenium).getItem())
        .obtain(ChemicalHelper.get(TagPrefix.dust, GTMaterials.Rhodium).getItem())
        .obtain(ChemicalHelper.get(TagPrefix.dust, GTMaterials.Iridium).getItem())
        .obtain(ChemicalHelper.get(TagPrefix.dust, GTMaterials.Osmium).getItem())
        .buildAndRegister();

    public static final Advancement BLAST_ALLOY_SMELTER = SFTAdvancementBuilder.create("blast_alloy_smelter")
        .parent(PLATINUM_GROUP)
        .display(
            GCYMMachines.BLAST_ALLOY_SMELTER.getItem(),
            "Melt! Melt!",
            "Build an alloy blast furnace to make advanced alloys"
        )
        .form(GCYMMachines.BLAST_ALLOY_SMELTER)
        .buildAndRegister();

    // IV
    public static final Advancement TUNGSTEN_STEEL = SFTAdvancementBuilder.create("tungsten_steel")
        .parent(TUNGSTEN)
        .goal()
        .display(
            ChemicalHelper.get(TagPrefix.ingot, GTMaterials.TungstenSteel).getItem(),
            "\"Blue Alloy\"",
            "Make the IV alloy Tungsten steel"
        )
        .obtain(ChemicalHelper.get(TagPrefix.ingot, GTMaterials.TungstenSteel).getItem())
        .buildAndRegister();

    public static final Advancement ADVANCED_SMD = SFTAdvancementBuilder.create("advanced_smd")
        .parent(TUNGSTEN_STEEL)
        .display(
            GTItems.ADVANCED_SMD_CAPACITOR,
            "Advanced Circuit Fab",
            "Craft all 5 advanced SMDs"
        )
        .obtain(GTItems.ADVANCED_SMD_CAPACITOR)
        .obtain(GTItems.ADVANCED_SMD_DIODE)
        .obtain(GTItems.ADVANCED_SMD_RESISTOR)
        .obtain(GTItems.ADVANCED_SMD_INDUCTOR)
        .obtain(GTItems.ADVANCED_SMD_TRANSISTOR)
        .buildAndRegister();

    public static final Advancement QUANTUM_MAINFRAME = SFTAdvancementBuilder.create("quantum_mainframe")
        .parent(ADVANCED_SMD)
        .display(
            GTItems.QUANTUM_MAINFRAME_ZPM,
            "Quantum Singer",
            "Craft a quantum mainframe"
        )
        .obtain(GTItems.QUANTUM_MAINFRAME_ZPM)
        .buildAndRegister();

    public static final Advancement PARALLEL_HATCH = SFTAdvancementBuilder.create("parallel_hatch")
        .parent(TUNGSTEN_STEEL)
        .any()
        .display(
            GCYMMachines.PARALLEL_HATCH[GTValues.IV].getItem(),
            "Parallelization",
            "Build a parallel hatch and put it on a large machine"
        )
        .obtain(GCYMMachines.PARALLEL_HATCH[GTValues.IV].getItem())
        .obtain(GCYMMachines.PARALLEL_HATCH[GTValues.LuV].getItem())
        .obtain(GCYMMachines.PARALLEL_HATCH[GTValues.ZPM].getItem())
        .obtain(GCYMMachines.PARALLEL_HATCH[GTValues.UV].getItem())
        .buildAndRegister();

    public static final Advancement LARGE_MACHINE = SFTAdvancementBuilder.create("large_machine")
        .parent(PARALLEL_HATCH)
        .any()
        .display(
            GCYMMachines.LARGE_ASSEMBLER.getItem(),
            "Large-scale Processing",
            "Build a large machine"
        )
        .form(GCYMMachines.LARGE_ARC_SMELTER)
        .form(GCYMMachines.LARGE_ASSEMBLER)
        .form(GCYMMachines.LARGE_AUTOCLAVE)
        .form(GCYMMachines.LARGE_BREWER)
        .form(GCYMMachines.LARGE_CENTRIFUGE)
        .form(GCYMMachines.LARGE_CHEMICAL_BATH)
        .form(GCYMMachines.LARGE_CIRCUIT_ASSEMBLER)
        .form(GCYMMachines.LARGE_CUTTER)
        .form(GCYMMachines.LARGE_DISTILLERY)
        .form(GCYMMachines.LARGE_ELECTROLYZER)
        .form(GCYMMachines.LARGE_ELECTROMAGNET)
        .form(GCYMMachines.LARGE_ENGRAVING_LASER)
        .form(GCYMMachines.LARGE_EXTRACTOR)
        .form(GCYMMachines.LARGE_EXTRUDER)
        .form(GCYMMachines.LARGE_MACERATION_TOWER)
        .form(GCYMMachines.LARGE_MATERIAL_PRESS)
        .form(GCYMMachines.LARGE_MIXER)
        .form(GCYMMachines.LARGE_PACKER)
        .form(GCYMMachines.LARGE_SIFTING_FUNNEL)
        .form(GCYMMachines.LARGE_SOLIDIFIER)
        .form(GCYMMachines.LARGE_WIREMILL)
        .buildAndRegister();

    public static final Advancement GCYM_COLLECTOR = SFTAdvancementBuilder.create("gcym_collector")
        .parent(LARGE_MACHINE)
        .challenge()
        .display(
            GCYMMachines.LARGE_ASSEMBLER.getItem(),
            "Full Metal Panic!",
            "Build all parallelizable GCYM machines"
        )
        .form(GCYMMachines.LARGE_ARC_SMELTER)
        .form(GCYMMachines.LARGE_ASSEMBLER)
        .form(GCYMMachines.LARGE_AUTOCLAVE)
        .form(GCYMMachines.LARGE_BREWER)
        .form(GCYMMachines.LARGE_CENTRIFUGE)
        .form(GCYMMachines.LARGE_CHEMICAL_BATH)
        .form(GCYMMachines.LARGE_CIRCUIT_ASSEMBLER)
        .form(GCYMMachines.LARGE_CUTTER)
        .form(GCYMMachines.LARGE_DISTILLERY)
        .form(GCYMMachines.LARGE_ELECTROLYZER)
        .form(GCYMMachines.LARGE_ELECTROMAGNET)
        .form(GCYMMachines.LARGE_ENGRAVING_LASER)
        .form(GCYMMachines.LARGE_EXTRACTOR)
        .form(GCYMMachines.LARGE_EXTRUDER)
        .form(GCYMMachines.LARGE_MACERATION_TOWER)
        .form(GCYMMachines.LARGE_MATERIAL_PRESS)
        .form(GCYMMachines.LARGE_MIXER)
        .form(GCYMMachines.LARGE_PACKER)
        .form(GCYMMachines.LARGE_SIFTING_FUNNEL)
        .form(GCYMMachines.LARGE_SOLIDIFIER)
        .form(GCYMMachines.LARGE_WIREMILL)
        .buildAndRegister();

    public static final Advancement POWER_SUBSTATION = SFTAdvancementBuilder.create("power_substation")
        .parent(TUNGSTEN_STEEL)
        .display(
            GTMultiMachines.POWER_SUBSTATION.getItem(),
            "Global Power Grid",
            "Build a power substation to distribute power to multiple machines"
        )
        .form(GTMultiMachines.POWER_SUBSTATION)
        .buildAndRegister();

    public static final Advancement ASSEMBLY_LINE = SFTAdvancementBuilder.create("assembly_line")
        .parent(TUNGSTEN_STEEL)
        .display(
            GTMultiMachines.ASSEMBLY_LINE.getItem(),
            "16 Slots, Not 16 Bits",
            "Build an assembly line"
        )
        .form(GTMultiMachines.ASSEMBLY_LINE)
        .buildAndRegister();

    public static final Advancement COMPUTER = SFTAdvancementBuilder.create("computer")
        .parent(ASSEMBLY_LINE)
        .display(
            GTResearchMachines.RESEARCH_STATION.getItem(),
            "This Is Not MySQL",
            "Build the research station and data bank to provide data"
        )
        .form(GTResearchMachines.RESEARCH_STATION)
        .form(GTResearchMachines.DATA_BANK)
        .buildAndRegister();

    public static final Advancement HPCA = SFTAdvancementBuilder.create("hpca")
        .parent(COMPUTER)
        .display(
            GTResearchMachines.HIGH_PERFORMANCE_COMPUTING_ARRAY.getItem(),
            "FLOPs Rules Everything",
            "Build an HPCA"
        )
        .form(GTResearchMachines.HIGH_PERFORMANCE_COMPUTING_ARRAY)
        .buildAndRegister();

    public static final Advancement NETWORK_SWITCH = SFTAdvancementBuilder.create("network_switch")
        .parent(HPCA)
        .display(
            GTResearchMachines.NETWORK_SWITCH.getItem(),
            "5G WIFI",
            "Build a network switch"
        )
        .form(GTResearchMachines.NETWORK_SWITCH)
        .buildAndRegister();

    // LuV
    public static final Advancement RHODIUM_PLATED_PALLADIUM = SFTAdvancementBuilder.create("rhodium_plated_palladium")
        .parent(BLAST_ALLOY_SMELTER)
        .goal()
        .display(
            ChemicalHelper.get(TagPrefix.ingot, GTMaterials.RhodiumPlatedPalladium).getItem(),
            "White Lie",
            "Get a expensive rhodium plated palladium ingot"
        )
        .obtain(ChemicalHelper.get(TagPrefix.ingot, GTMaterials.RhodiumPlatedPalladium).getItem())
        .buildAndRegister();

    public static final Advancement ADVANCED_RUBBER = SFTAdvancementBuilder.create("advanced_rubber")
        .parent(RHODIUM_PLATED_PALLADIUM)
        .display(
            ChemicalHelper.get(TagPrefix.ingot, GTMaterials.SiliconeRubber).getItem(),
            "Advanced Rubber",
            "Craft advanced rubber"
        )
        .recipeExecute(GTRecipeTypes.CHEMICAL_RECIPES, "silicone_rubber")
        .recipeExecute(GTRecipeTypes.CHEMICAL_RECIPES, "styrene_butadiene_rubber")
        .buildAndRegister();

    public static final Advancement FUSION_REACTOR_MK1 = SFTAdvancementBuilder.create("fusion_reactor_mk1")
        .parent(RHODIUM_PLATED_PALLADIUM)
        .display(
            GTMultiMachines.FUSION_REACTOR[GTValues.LuV].getItem(),
            "Tasty Doughnut",
            "Build a fusion reactor"
        )
        .form(GTMultiMachines.FUSION_REACTOR[GTValues.LuV])
        .buildAndRegister();

    public static final Advancement CRYSTAL_CYCLE = SFTAdvancementBuilder.create("crystal_cycle")
        .parent(FUSION_REACTOR_MK1)
        .display(
            GTItems.RAW_CRYSTAL_CHIP,
            "How Can It Grow?",
            "Complete the cycling production of the crystal chips"
        )
        .recipeExecute(
            GTRecipeTypes.AUTOCLAVE_RECIPES,
            "raw_crystal_chip_from_part_europium",
            "raw_crystal_chip_from_part_mutagen",
            "raw_crystal_chip_from_part_bacterial_sludge"
        )
        .recipeExecute(GTRecipeTypes.FORGE_HAMMER_RECIPES, "raw_crystal_chip_part")
        .buildAndRegister();

    public static final Advancement CRYSTAL_MAINFRAME = SFTAdvancementBuilder.create("crystal_mainframe")
        .parent(CRYSTAL_CYCLE)
        .display(
            GTItems.CRYSTAL_MAINFRAME_UV,
            "Crystal Clear",
            "Craft a crystal mainframe"
        )
        .obtain(GTItems.CRYSTAL_MAINFRAME_UV)
        .buildAndRegister();

    public static final Advancement ACTIVE_TRANSFORMER = SFTAdvancementBuilder.create("active_transformer")
        .parent(ADVANCED_RUBBER)
        .display(
            GTMultiMachines.ACTIVE_TRANSFORMER.getItem(),
            "Not Only A Transformer",
            "Build an active transformer"
        )
        .form(GTMultiMachines.ACTIVE_TRANSFORMER)
        .buildAndRegister();

    public static final Advancement ACTIVE_TRANSFORMER_LASER = SFTAdvancementBuilder
        .create("active_transformer_laser")
        .parent(ACTIVE_TRANSFORMER)
        .display(
            GTMultiMachines.ACTIVE_TRANSFORMER.getItem(),
            "Be Careful of your eyes!",
            "Use active transformer to transmit a laser beam"
        )
        .simple(SFTCriteriaTriggers.ACTIVE_TRANSFORMER_LASER)
        .buildAndRegister();

    // ZPM
    public static final Advancement NAQUADAH_ALLOY = SFTAdvancementBuilder.create("naquadah_alloy")
        .parent(RHODIUM_PLATED_PALLADIUM)
        .goal()
        .display(
            ChemicalHelper.get(TagPrefix.ingot, GTMaterials.NaquadahAlloy).getItem(),
            "Deep Dark Fantasy",
            "Get a Naquadah alloy ingot"
        )
        .obtain(ChemicalHelper.get(TagPrefix.ingot, GTMaterials.NaquadahAlloy).getItem())
        .buildAndRegister();

    public static final Advancement ZERO_POINT_MODULE = SFTAdvancementBuilder.create("zero_point_module")
        .parent(NAQUADAH_ALLOY)
        .hidden()
        .display(
            GTItems.ZERO_POINT_MODULE,
            "Gift of the Pharaoh",
            "Find a zero point module from the jungle temple"
        )
        .obtain(GTItems.ZERO_POINT_MODULE)
        .buildAndRegister();

    public static final Advancement FUSION_REACTOR_MK2 = SFTAdvancementBuilder.create("fusion_reactor_mk2")
        .parent(NAQUADAH_ALLOY)
        .display(
            GTMultiMachines.FUSION_REACTOR[GTValues.ZPM].getItem(),
            "Golden Doughnut?",
            "Upgrade your fusion reactor to MK2"
        )
        .form(GTMultiMachines.FUSION_REACTOR[GTValues.ZPM])
        .buildAndRegister();

    public static final Advancement TRITANIUM = SFTAdvancementBuilder.create("tritanium")
        .parent(FUSION_REACTOR_MK2)
        .display(
            ChemicalHelper.get(TagPrefix.ingot, GTMaterials.Tritanium).getItem(),
            "Tritanium is tri-titanium",
            "Fuse three titanium ingot into a tritanium ingot"
        )
        .obtain(ChemicalHelper.get(TagPrefix.ingot, GTMaterials.Tritanium).getItem())
        .buildAndRegister();

    public static final Advancement STEM_CELLS = SFTAdvancementBuilder.create("stem_cells")
        .parent(NAQUADAH_ALLOY)
        .display(
            GTItems.STEM_CELLS,
            "Life Finds a Way",
            "Develop stem cells"
        )
        .obtain(GTItems.STEM_CELLS)
        .buildAndRegister();

    public static final Advancement WETWARE_MAINFRAME = SFTAdvancementBuilder.create("wetware_mainframe")
        .parent(STEM_CELLS)
        .display(
            GTItems.WETWARE_MAINFRAME_UHV,
            "\"Neural\" Network",
            "Craft a wetware mainframe"
        )
        .obtain(GTItems.WETWARE_MAINFRAME_UHV)
        .buildAndRegister();

    // UV
    public static final Advancement DARMSTADTIUM = SFTAdvancementBuilder.create("darmstadtium")
        .parent(FUSION_REACTOR_MK2)
        .goal()
        .display(
            ChemicalHelper.get(TagPrefix.ingot, GTMaterials.Darmstadtium).getItem(),
            "Entropic Stasis",
            "Fuse a darmstadtium ingot. Have you ever thought why the element never decay?"
        )
        .obtain(ChemicalHelper.get(TagPrefix.ingot, GTMaterials.Darmstadtium).getItem())
        .buildAndRegister();

    public static final Advancement FUSION_REACTOR_MK3 = SFTAdvancementBuilder.create("fusion_reactor_mk3")
        .parent(DARMSTADTIUM)
        .display(
            GTMultiMachines.FUSION_REACTOR[GTValues.UV].getItem(),
            "That Seems Spicy",
            "Upgrade your fusion reactor to MK3"
        )
        .form(GTMultiMachines.FUSION_REACTOR[GTValues.UV])
        .buildAndRegister();

    // UHV
    public static final Advancement NEUTRONIUM = SFTAdvancementBuilder.create("neutronium")
        .parent(FUSION_REACTOR_MK3)
        .goal()
        .display(
            ChemicalHelper.get(TagPrefix.ingot, GTMaterials.Neutronium).getItem(),
            "Only 300 Million Tons",
            "This is Neutronium, the heaviest matter in this world"
        )
        .obtain(ChemicalHelper.get(TagPrefix.ingot, GTMaterials.Neutronium).getItem())
        .buildAndRegister();

    public static final Advancement ULTIMATE_BATTERY = SFTAdvancementBuilder.create("battery")
        .parent(NEUTRONIUM)
        .display(
            GTItems.ULTIMATE_BATTERY,
            "Grasp of the Threshold",
            "Craft the most powerful battery in GregTech Modern"
        )
        .obtain(GTItems.ULTIMATE_BATTERY)
        .buildAndRegister();

    public static final Advancement ULTIMATE_BATTERY_FULL = SFTAdvancementBuilder.create("ultimate_battery_full")
        .parent(ULTIMATE_BATTERY)
        .challenge()
        .hidden()
        .display(
            GTItems.ULTIMATE_BATTERY,
            "Infinity Achieved, Eternity Charged",
            "Fully charge the ultimate battery..."
        )
        .simple(SFTCriteriaTriggers.ULTIMATE_BATTERY_FULL)
        .buildAndRegister();

    public static final Advancement NAN_CERTIFICATE = SFTAdvancementBuilder.create("nan_certificate")
        .parent(NEUTRONIUM)
        .challenge()
        .display(
            GTItems.NAN_CERTIFICATE,
            "CONQUER THE WORLD!",
            "Prove you are an expert, and take a rest now!"
        )
        .obtain(GTItems.NAN_CERTIFICATE)
        .buildAndRegister();

    public static void init(RegistrateAdvancementProvider provider) {
        ADVANCEMENTS.forEach(provider);
    }
}
