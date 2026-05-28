package org.leodreamer.sftcore.common.data.recipe;

import org.leodreamer.sftcore.api.recipe.remove.RecipeFilter;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.data.machines.GCYMMachines;
import com.gregtechceu.gtceu.common.data.machines.GTAEMachines;

import net.minecraft.world.level.ItemLike;

import appeng.core.definitions.AEItems;
import cn.qiuye.gtmoremachine.common.data.GTMMItems;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import gripe._90.megacells.definition.MEGAItems;
import mekanism.common.registries.MekanismBlocks;
import mekanism.generators.common.registries.GeneratorsBlocks;
import net.pedroksl.advanced_ae.common.definitions.AAEBlocks;

import java.util.function.Consumer;

import static cn.qiuye.gtmoremachine.common.data.machines.WirelessMachines.*;
import static org.leodreamer.sftcore.api.recipe.remove.RecipeFilters.*;
import static org.leodreamer.sftcore.integration.IntegrateMods.*;
import static org.leodreamer.sftcore.util.RLUtils.getItemById;

public final class SFTRecipeRemovals {

    public static void init(Consumer<RecipeFilter> registry) {
        ItemLike[] ITEMS = new ItemLike[] {
            GTMachines.HULL[GTValues.LV].getItem(),
            GTMachines.CLEANING_MAINTENANCE_HATCH.getItem(),
            GTAEMachines.ME_PATTERN_BUFFER.getItem(),
            GCYMMachines.MEGA_BLAST_FURNACE.getItem(),
            GCYMMachines.MEGA_VACUUM_FREEZER.getItem(),
            GTItems.TERMINAL,
            GTItems.NAQUADAH_BOULE,
            GTItems.NEUTRONIUM_BOULE,
            GTMMItems.ADVANCED_TERMINAL,
            EPPItemAndBlock.INFINITY_CELL,
            EPPItemAndBlock.WIRELESS_CONNECTOR,
            EPPItemAndBlock.FISHBIG,
            MekanismBlocks.SPS_CASING,
            GeneratorsBlocks.TURBINE_CASING,
            getItemById(TORCHERINO, "torcherino"),
            getItemById(IDS, "facade")
        };

        for (var item : ITEMS) {
            registry.accept(output(item));
        }

        MachineDefinition[][] laserHatches = new MachineDefinition[][] {
            WIRELESS_ENERGY_INPUT_HATCH_256A,
            WIRELESS_ENERGY_OUTPUT_HATCH_256A,
            WIRELESS_ENERGY_INPUT_HATCH_1024A,
            WIRELESS_ENERGY_OUTPUT_HATCH_1024A,
            WIRELESS_ENERGY_INPUT_HATCH_4096A,
            WIRELESS_ENERGY_OUTPUT_HATCH_4096A
        };

        for (var hatches : laserHatches) {
            for (var item : hatches) {
                if (item != null) {
                    registry.accept(output(item.getItem()));
                }
            }
        }

        registry.accept(input(GTItems.NAN_CERTIFICATE));

        ItemLike[] AAE_ITEMS = new ItemLike[] {
            AEItems.FLUIX_CRYSTAL,
            MEGAItems.SKY_STEEL_INGOT,
            AEItems.CERTUS_QUARTZ_CRYSTAL,
            AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED,
            AEItems.CALCULATION_PROCESSOR,
            AEItems.LOGIC_PROCESSOR,
            AEItems.ENGINEERING_PROCESSOR,
            MEGAItems.ACCUMULATION_PROCESSOR,
            AAEBlocks.REACTION_CHAMBER
        };

        for (var item : AAE_ITEMS) {
            registry.accept(output(item).and(mod(AAE)));
        }
    }
}
