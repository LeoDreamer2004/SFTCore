package org.leodreamer.sftcore.common.data.recipe;

import appeng.core.definitions.AEItems;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.hepdd.gtmthings.data.CustomItems;
import gripe._90.megacells.definition.MEGAItems;
import mekanism.common.registries.MekanismBlocks;
import mekanism.generators.common.registries.GeneratorsBlocks;
import net.minecraft.world.level.ItemLike;
import net.pedroksl.advanced_ae.common.definitions.AAEBlocks;
import org.leodreamer.sftcore.api.recipe.remove.RecipeFilter;

import java.util.function.Consumer;

import static org.leodreamer.sftcore.api.recipe.remove.RecipeFilters.*;
import static org.leodreamer.sftcore.integration.IntegrateMods.*;
import static org.leodreamer.sftcore.integration.RLUtils.getItemById;

public final class SFTRecipeRemovals {

    public static void init(Consumer<RecipeFilter> registry) {
        ItemLike[] GT_ITEMS = new ItemLike[]{
                GTMachines.HULL[GTValues.LV].getItem(),
                GTMachines.CLEANING_MAINTENANCE_HATCH.getItem(),
                GTItems.TERMINAL,
                GTItems.NAQUADAH_BOULE,
                GTItems.NEUTRONIUM_BOULE
        };

        for (ItemLike item : GT_ITEMS) {
            registry.accept(output(item).and(mod(GTM)));
        }

        registry.accept(input(GTItems.NAN_CERTIFICATE));
        registry.accept(output(CustomItems.ADVANCED_TERMINAL).and(mod(GTMT)));

        ItemLike[] EPP_ITEMS = new ItemLike[]{
                EPPItemAndBlock.INFINITY_CELL,
                EPPItemAndBlock.WIRELESS_CONNECTOR,
                EPPItemAndBlock.FISHBIG
        };

        for (ItemLike item : EPP_ITEMS) {
            registry.accept(output(item).and(mod(EAE)));
        }

        ItemLike[] AAE_ITEMS = new ItemLike[]{
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

        for (ItemLike item : AAE_ITEMS) {
            registry.accept(output(item).and(mod(AAE)));
        }

        registry.accept(output(MekanismBlocks.SPS_CASING).and(mod(MEK)));
        registry.accept(output(GeneratorsBlocks.TURBINE_CASING).and(mod(MEKG)));
        registry.accept(output(getItemById(TORCHERINO, "torcherino")).and(mod(TORCHERINO)));
        registry.accept(output(getItemById(IDS, "facade")).and(mod(IDS)));
    }
}
