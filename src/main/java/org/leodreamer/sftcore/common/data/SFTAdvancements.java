package org.leodreamer.sftcore.common.data;

import org.leodreamer.sftcore.common.advancement.SFTAdvancementBuilder;
import org.leodreamer.sftcore.common.advancement.trigger.DuctTapedMaintenanceTrigger;
import org.leodreamer.sftcore.common.advancement.trigger.MachineExplodedTrigger;
import org.leodreamer.sftcore.common.advancement.trigger.MaxCleanroomCleanTrigger;
import org.leodreamer.sftcore.common.advancement.trigger.WireBurnedTrigger;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.common.data.machines.GCYMMachines;
import com.gregtechceu.gtceu.common.data.machines.GTMultiMachines;

import net.minecraft.advancements.Advancement;
import net.minecraft.world.item.Items;

import com.tterrag.registrate.providers.RegistrateAdvancementProvider;

public final class SFTAdvancements {

    public static final Advancement ROOT = SFTAdvancementBuilder.create("root")
        .silent()
        .display(
            GTItems.TERMINAL,
            "Welcome to GregTech Modern",
            "This is an unofficial advancement system for GTM, authored by LeoDreamer in Starter For Technology."
        )
        .free()
        .build();

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
        .build();

    public static final Advancement VACUUM_TUBE = SFTAdvancementBuilder.create("vacuum_tube")
        .parent(BRONZE)
        .display(
            GTItems.VACUUM_TUBE,
            "Not really vacuum",
            "Craft a vacuum tube"
        )
        .obtain(GTItems.VACUUM_TUBE)
        .build();

    public static final Advancement BASIC_ELECTRONIC_CIRCUIT = SFTAdvancementBuilder.create("basic_electronic_circuit")
        .parent(VACUUM_TUBE)
        .display(
            GTItems.ELECTRONIC_CIRCUIT_LV,
            "Information Age?",
            "Craft your first circuit"
        )
        .obtain(GTItems.ELECTRONIC_CIRCUIT_LV)
        .build();

    public static final Advancement PRIMITIVE_BLAST_FURNACE = SFTAdvancementBuilder.create("primitive_blast_furnace")
        .parent(BRONZE)
        .display(
            GTMultiMachines.PRIMITIVE_BLAST_FURNACE.getItem(),
            "We are marching on the great road!",
            "Build a primitive blast furnace and make it formed"
        )
        .onFormed(GTMultiMachines.PRIMITIVE_BLAST_FURNACE)
        .build();

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
        .build();

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
        .build();

    public static final Advancement MACHINE_EXPLODED = SFTAdvancementBuilder.create("machine_exploded")
        .parent(WIRE_BURNED)
        .challenge()
        .hidden()
        .display(
            Items.TNT,
            "GregTech? Piece of Cake!",
            "Maybe it is time to think very carefully about rolling back that save..."
        )
        .criterion("machine_exploded", MachineExplodedTrigger.Instance.exploded())
        .build();

    public static final Advancement ELECTRIC_BLAST_FURNACE = SFTAdvancementBuilder.create("electric_blast_furnace")
        .parent(STEEL)
        .display(
            GTMultiMachines.ELECTRIC_BLAST_FURNACE.getItem(),
            "Joule's Law",
            "Heat your metal with electricity and coils!"
        )
        .onFormed(GTMultiMachines.ELECTRIC_BLAST_FURNACE)
        .build();

    public static final Advancement DUCT_TAPED_MAINTENANCE = SFTAdvancementBuilder.create("duct_taped_maintenance")
        .parent(ELECTRIC_BLAST_FURNACE)
        .display(
            GTItems.DUCT_TAPE,
            "Tape!",
            "Use duct tape on a maintenance hatch to keep it working for a while"
        )
        .criterion("duct_taped_maintenance", DuctTapedMaintenanceTrigger.Instance.taped())
        .build();

    public static final Advancement AUTO_MAINTENANCE = SFTAdvancementBuilder.create("auto_maintenance")
        .parent(DUCT_TAPED_MAINTENANCE)
        .display(
            GTMachines.AUTO_MAINTENANCE_HATCH.getItem(),
            "No More Maintenance",
            "Craft an auto maintenance hatch to automatically maintain your machine"
        )
        .obtain(GTMachines.AUTO_MAINTENANCE_HATCH.getItem())
        .build();

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
        .build();

    public static final Advancement POLYETHYLENE = SFTAdvancementBuilder.create("polyethene")
        .parent(ALUMINIUM)
        .any()
        .display(
            ChemicalHelper.get(TagPrefix.ingot, GTMaterials.Polyethylene).getItem(),
            "Plastic",
            "Craft polyethene from chemical reactor"
        )
        .onRecipeExecuted(
            GTRecipeTypes.CHEMICAL_RECIPES,
            "polyethylene_from_air",
            "polyethylene_from_oxygen"
        )
        .onRecipeExecuted(
            GTRecipeTypes.LARGE_CHEMICAL_RECIPES,
            "polyethylene_from_tetrachloride_air",
            "polyethylene_from_tetrachloride_oxygen"
        )
        .build();

    public static final Advancement SILICON_BOULE = SFTAdvancementBuilder.create("silicon_boule")
        .parent(ELECTRIC_BLAST_FURNACE)
        .display(
            GTItems.SILICON_BOULE,
            "7 Minutes for Silicon",
            "Develop... or blast a silicon boule"
        )
        .onRecipeExecuted(
            GTRecipeTypes.BLAST_RECIPES, "silicon_boule"
        )
        .build();

    public static final Advancement BATHING_TO_COOL = SFTAdvancementBuilder.create("bathing_to_cool")
        .parent(ALUMINIUM)
        .display(
            GTMaterials.DistilledWater.getBucket(),
            "Take a bath",
            "Cool down a hot ingot with bathing"
        )
        .onRecipeExecuted(
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
        .build();

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
        .build();

    public static final Advancement CLEANROOM = SFTAdvancementBuilder.create("cleanroom")
        .parent(STAINLESS_STEEL)
        .display(
            GTMultiMachines.CLEANROOM.getItem(),
            "Spotless",
            "Build a cleanroom to craft advanced circuits."
        )
        .onFormed(GTMultiMachines.CLEANROOM)
        .build();

    public static final Advancement MAX_CLEANROOM_CLEAN = SFTAdvancementBuilder.create("max_cleanroom_clean")
        .parent(CLEANROOM)
        .goal()
        .display(
            GTMultiMachines.CLEANROOM.getItem(),
            "Technology is dear to me, but dearer still is cleanliness",
            "Make a max size cleanroom clean up"
        )
        .criterion("max_cleanroom_clean", MaxCleanroomCleanTrigger.Instance.cleaned())
        .build();

    public static final Advancement MICRO_PROCESSOR_MAINFRAME = SFTAdvancementBuilder
        .create("micro_processor_mainframe")
        .parent(CLEANROOM)
        .display(
            GTItems.MAINFRAME_IV,
            "Can I Play Minecraft?",
            "Craft a micro-processor mainframe"
        )
        .obtain(GTItems.MAINFRAME_IV)
        .build();

    public static final Advancement OIL = SFTAdvancementBuilder.create("oil")
        .parent(STAINLESS_STEEL)
        .any()
        .display(
            GTMaterials.Oil.getBucket(),
            "Black Gold",
            "Mine endless oil with a drilling dig"
        )
        .onFormed(GTMultiMachines.FLUID_DRILLING_RIG[GTValues.MV])
        .onFormed(GTMultiMachines.FLUID_DRILLING_RIG[GTValues.HV])
        .onFormed(GTMultiMachines.FLUID_DRILLING_RIG[GTValues.EV])
        .build();

    public static final Advancement LARGE_CHEMICAL_REACTOR = SFTAdvancementBuilder.create("large_chemical_reactor")
        .parent(OIL)
        .display(
            GTMultiMachines.LARGE_CHEMICAL_REACTOR.getItem(),
            "Dark Mage",
            "Build a large chemical reactor to process on a large scale"
        )
        .onFormed(GTMultiMachines.LARGE_CHEMICAL_REACTOR)
        .build();

    public static final Advancement PETROCHEMICAL = SFTAdvancementBuilder.create("petrochemical")
        .parent(LARGE_CHEMICAL_REACTOR)
        .goal()
        .display(
            GTMultiMachines.DISTILLATION_TOWER.getItem(),
            "Petrochemical",
            "Process oil into petrochemical products"
        )
        .onFormed(GTMultiMachines.DISTILLATION_TOWER)
        .onFormed(GTMultiMachines.CRACKER)
        .onFormed(GTMultiMachines.LARGE_CHEMICAL_REACTOR)
        .build();

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
        .build();

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
        .build();

    public static final Advancement TUNGSTEN = SFTAdvancementBuilder.create("tungsten")
        .parent(TITANIUM)
        .display(
            ChemicalHelper.get(TagPrefix.dust, GTMaterials.Tungsten).getItem(),
            "Wolfram Alpha",
            "Electrolyze to get Tungsten"
        )
        .obtain(ChemicalHelper.get(TagPrefix.dust, GTMaterials.Tungsten).getItem())
        .build();

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
        .build();

    public static final Advancement BLAST_ALLOY_SMELTER = SFTAdvancementBuilder.create("blast_alloy_smelter")
        .parent(PLATINUM_GROUP)
        .display(
            GCYMMachines.BLAST_ALLOY_SMELTER.getItem(),
            "Melt! Melt!",
            "Build an alloy blast furnace to make advanced alloys"
        )
        .onFormed(GCYMMachines.BLAST_ALLOY_SMELTER)
        .build();

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
        .build();

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
        .build();

    public static final Advancement LARGE_MACHINE = SFTAdvancementBuilder.create("large_machine")
        .parent(PARALLEL_HATCH)
        .any()
        .display(
            GCYMMachines.LARGE_ASSEMBLER.getItem(),
            "Large-scale Processing",
            "Build a large machine"
        )
        .onFormed(GCYMMachines.LARGE_ARC_SMELTER)
        .onFormed(GCYMMachines.LARGE_ASSEMBLER)
        .onFormed(GCYMMachines.LARGE_AUTOCLAVE)
        .onFormed(GCYMMachines.LARGE_BREWER)
        .onFormed(GCYMMachines.LARGE_CENTRIFUGE)
        .onFormed(GCYMMachines.LARGE_CHEMICAL_BATH)
        .onFormed(GCYMMachines.LARGE_CIRCUIT_ASSEMBLER)
        .onFormed(GCYMMachines.LARGE_CUTTER)
        .onFormed(GCYMMachines.LARGE_DISTILLERY)
        .onFormed(GCYMMachines.LARGE_ELECTROLYZER)
        .onFormed(GCYMMachines.LARGE_ELECTROMAGNET)
        .onFormed(GCYMMachines.LARGE_ENGRAVING_LASER)
        .onFormed(GCYMMachines.LARGE_EXTRACTOR)
        .onFormed(GCYMMachines.LARGE_EXTRUDER)
        .onFormed(GCYMMachines.LARGE_MACERATION_TOWER)
        .onFormed(GCYMMachines.LARGE_MATERIAL_PRESS)
        .onFormed(GCYMMachines.LARGE_MIXER)
        .onFormed(GCYMMachines.LARGE_PACKER)
        .onFormed(GCYMMachines.LARGE_SIFTING_FUNNEL)
        .onFormed(GCYMMachines.LARGE_SOLIDIFIER)
        .onFormed(GCYMMachines.LARGE_WIREMILL)
        .build();

    public static final Advancement GCYM_COLLECTOR = SFTAdvancementBuilder.create("gcym_collector")
        .parent(LARGE_MACHINE)
        .challenge()
        .display(
            GCYMMachines.LARGE_ASSEMBLER.getItem(),
            "Full Metal Panic!",
            "Build all parallelizable GCYM machines"
        )
        .onFormed(GCYMMachines.LARGE_ARC_SMELTER)
        .onFormed(GCYMMachines.LARGE_ASSEMBLER)
        .onFormed(GCYMMachines.LARGE_AUTOCLAVE)
        .onFormed(GCYMMachines.LARGE_BREWER)
        .onFormed(GCYMMachines.LARGE_CENTRIFUGE)
        .onFormed(GCYMMachines.LARGE_CHEMICAL_BATH)
        .onFormed(GCYMMachines.LARGE_CIRCUIT_ASSEMBLER)
        .onFormed(GCYMMachines.LARGE_CUTTER)
        .onFormed(GCYMMachines.LARGE_DISTILLERY)
        .onFormed(GCYMMachines.LARGE_ELECTROLYZER)
        .onFormed(GCYMMachines.LARGE_ELECTROMAGNET)
        .onFormed(GCYMMachines.LARGE_ENGRAVING_LASER)
        .onFormed(GCYMMachines.LARGE_EXTRACTOR)
        .onFormed(GCYMMachines.LARGE_EXTRUDER)
        .onFormed(GCYMMachines.LARGE_MACERATION_TOWER)
        .onFormed(GCYMMachines.LARGE_MATERIAL_PRESS)
        .onFormed(GCYMMachines.LARGE_MIXER)
        .onFormed(GCYMMachines.LARGE_PACKER)
        .onFormed(GCYMMachines.LARGE_SIFTING_FUNNEL)
        .onFormed(GCYMMachines.LARGE_SOLIDIFIER)
        .onFormed(GCYMMachines.LARGE_WIREMILL)
        .build();

    public static final Advancement ASSEMBLY_LINE = SFTAdvancementBuilder.create("assembly_line")
        .parent(TUNGSTEN_STEEL)
        .display(
            GTMultiMachines.ASSEMBLY_LINE.getItem(),
            "16 Slots, not 16 Bits",
            "Build an assembly line"
        )
        .onFormed(GTMultiMachines.ASSEMBLY_LINE)
        .build();

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
        .build();

    public static final Advancement FUSION_REACTOR = SFTAdvancementBuilder.create("fusion_reactor")
        .parent(RHODIUM_PLATED_PALLADIUM)
        .display(
            GTMultiMachines.FUSION_REACTOR[GTValues.LuV].getItem(),
            "Tasty Doughnut",
            "Build a fusion reactor"
        )
        .onFormed(GTMultiMachines.FUSION_REACTOR[GTValues.LuV])
        .build();

    public static void init(RegistrateAdvancementProvider provider) {
        provider.accept(ROOT);

        provider.accept(BRONZE);
        provider.accept(PRIMITIVE_BLAST_FURNACE);
        provider.accept(VACUUM_TUBE);
        provider.accept(BASIC_ELECTRONIC_CIRCUIT);

        provider.accept(STEEL);
        provider.accept(WIRE_BURNED);
        provider.accept(MACHINE_EXPLODED);
        provider.accept(ELECTRIC_BLAST_FURNACE);
        provider.accept(DUCT_TAPED_MAINTENANCE);
        provider.accept(AUTO_MAINTENANCE);

        provider.accept(ALUMINIUM);
        provider.accept(POLYETHYLENE);
        provider.accept(SILICON_BOULE);
        provider.accept(BATHING_TO_COOL);

        provider.accept(STAINLESS_STEEL);
        provider.accept(CLEANROOM);
        provider.accept(MAX_CLEANROOM_CLEAN);
        provider.accept(MICRO_PROCESSOR_MAINFRAME);
        provider.accept(OIL);
        provider.accept(LARGE_CHEMICAL_REACTOR);
        provider.accept(PETROCHEMICAL);
        provider.accept(SMD);

        provider.accept(TITANIUM);
        provider.accept(TUNGSTEN);
        provider.accept(PLATINUM_GROUP);
        provider.accept(BLAST_ALLOY_SMELTER);

        provider.accept(TUNGSTEN_STEEL);
        provider.accept(PARALLEL_HATCH);
        provider.accept(LARGE_MACHINE);
        provider.accept(GCYM_COLLECTOR);
        provider.accept(ASSEMBLY_LINE);

        provider.accept(RHODIUM_PLATED_PALLADIUM);
        provider.accept(FUSION_REACTOR);
    }
}
