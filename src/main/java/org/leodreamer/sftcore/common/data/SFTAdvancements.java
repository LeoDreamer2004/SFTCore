package org.leodreamer.sftcore.common.data;

import org.leodreamer.sftcore.common.advancement.SFTAdvancementBuilder;
import org.leodreamer.sftcore.common.advancement.trigger.DuctTapedMaintenanceTrigger;
import org.leodreamer.sftcore.common.advancement.trigger.MachineExplodedTrigger;
import org.leodreamer.sftcore.common.advancement.trigger.WireBurnedTrigger;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
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
        .free().build();

    public static final Advancement BRONZE = SFTAdvancementBuilder.create("bronze")
        .parent(ROOT)
        .goal()
        .display(
            ChemicalHelper.get(TagPrefix.ingot, GTMaterials.Bronze).getItem(),
            "Primitive Alloy",
            "Get a bronze ingot"
        )
        .obtain(ChemicalHelper.get(TagPrefix.ingot, GTMaterials.Bronze).getItem()).build();

    public static final Advancement VACUUM_TUBE = SFTAdvancementBuilder.create("vacuum_tube")
        .parent(BRONZE)
        .display(
            GTItems.VACUUM_TUBE,
            "Not really vacuum",
            "Craft a vacuum tube"
        )
        .obtain(GTItems.VACUUM_TUBE).build();

    public static final Advancement BASIC_ELECTRONIC_CIRCUIT = SFTAdvancementBuilder.create("basic_electronic_circuit")
        .parent(VACUUM_TUBE)
        .display(
            GTItems.ELECTRONIC_CIRCUIT_LV,
            "Information Age?",
            "Craft your first circuit"
        )
        .obtain(GTItems.ELECTRONIC_CIRCUIT_LV).build();

    public static final Advancement PRIMITIVE_BLAST_FURNACE = SFTAdvancementBuilder.create("primitive_blast_furnace")
        .parent(BRONZE)
        .display(
            GTMultiMachines.PRIMITIVE_BLAST_FURNACE.getItem(),
            "We are marching on the great road!",
            "Build a primitive blast furnace and make it formed"
        )
        .onFormed(GTMultiMachines.PRIMITIVE_BLAST_FURNACE).build();

    public static final Advancement STEEL = SFTAdvancementBuilder.create("steel")
        .parent(PRIMITIVE_BLAST_FURNACE)
        .goal()
        .display(
            ChemicalHelper.get(TagPrefix.ingot, GTMaterials.Steel).getItem(),
            "Heavy Industry",
            "Get a steel ingot from your primitive blast furnace!"
        )
        .obtain(ChemicalHelper.get(TagPrefix.ingot, GTMaterials.Steel).getItem()).build();

    public static final Advancement WIRE_BURNED = SFTAdvancementBuilder.create("wire_burned")
        .parent(STEEL)
        .hidden()
        .display(
            ChemicalHelper.get(TagPrefix.wireGtSingle, GTMaterials.Copper).getItem(),
            "Electricity Safety",
            "Congratulations, your cable has become a very expensive fuse."
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
        .onFormed(GTMultiMachines.ELECTRIC_BLAST_FURNACE).build();

    public static final Advancement DUCT_TAPED_MAINTENANCE = SFTAdvancementBuilder.create("duct_taped_maintenance")
        .parent(ELECTRIC_BLAST_FURNACE)
        .display(
            GTItems.DUCT_TAPE,
            "Tape!",
            "Use duct tape on a maintenance hatch to keep it working for a while"
        )
        .criterion("duct_taped_maintenance", DuctTapedMaintenanceTrigger.Instance.taped())
        .build();

    public static final Advancement ALUMINIUM = SFTAdvancementBuilder.create("aluminum")
        .parent(ELECTRIC_BLAST_FURNACE)
        .goal()
        .display(
            ChemicalHelper.get(TagPrefix.ingot, GTMaterials.Aluminium).getItem(),
            "\"Light\" Industry",
            "Get an aluminum ingot from your blast furnace"
        )
        .obtain(ChemicalHelper.get(TagPrefix.ingot, GTMaterials.Aluminium).getItem()).build();

    public static final Advancement SILICON_BOULE = SFTAdvancementBuilder.create("silicon_boule")
        .parent(ELECTRIC_BLAST_FURNACE)
        .display(
            GTItems.SILICON_BOULE,
            "Semiconductor",
            "Develop... or blast a silicon boule"
        )
        .obtain(GTItems.SILICON_BOULE).build();

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
        provider.accept(ALUMINIUM);
        provider.accept(SILICON_BOULE);
    }
}
