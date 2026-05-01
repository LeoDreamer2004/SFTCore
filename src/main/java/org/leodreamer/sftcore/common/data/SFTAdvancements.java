package org.leodreamer.sftcore.common.data;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.machines.GTMultiMachines;
import com.tterrag.registrate.providers.RegistrateAdvancementProvider;
import net.minecraft.advancements.Advancement;
import org.leodreamer.sftcore.common.advancement.SFTAdvancementBuilder;

public final class SFTAdvancements {

    public static final Advancement ROOT = SFTAdvancementBuilder.create("root")
        .silent()
        .display(GTItems.TERMINAL, "Welcome to GregTech Modern", "This is an unofficial advancement system for GTM, authored by LeoDreamer in Starter For Technology.")
        .free().build();

    public static final Advancement BRONZE = SFTAdvancementBuilder.create("bronze")
        .parent(ROOT)
        .display(ChemicalHelper.get(TagPrefix.ingot, GTMaterials.Bronze).getItem(), "Primitive Alloy", "The first step towards progress")
        .obtain(ChemicalHelper.get(TagPrefix.ingot, GTMaterials.Bronze).getItem()).build();

    public static final Advancement PRIMITIVE_BLAST_FURNACE = SFTAdvancementBuilder.create("primitive_blast_furnace")
        .parent(BRONZE)
        .display(GTMultiMachines.PRIMITIVE_BLAST_FURNACE.getItem(), "Primitive Blast Furnace", "We are marching on thhe great road!")
        .onFormed(GTMultiMachines.PRIMITIVE_BLAST_FURNACE).build();

    public static final Advancement STEEL = SFTAdvancementBuilder.create("steel")
        .parent(PRIMITIVE_BLAST_FURNACE)
        .display(ChemicalHelper.get(TagPrefix.ingot, GTMaterials.Steel).getItem(), "Heavy Industry", "Mass steel making")
        .obtain(ChemicalHelper.get(TagPrefix.ingot, GTMaterials.Steel).getItem()).build();


    public static void init(RegistrateAdvancementProvider provider) {
        provider.accept(ROOT);
        provider.accept(BRONZE);
        provider.accept(PRIMITIVE_BLAST_FURNACE);
        provider.accept(STEEL);
    }
}
