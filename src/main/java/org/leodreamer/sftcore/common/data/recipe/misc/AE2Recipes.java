package org.leodreamer.sftcore.common.data.recipe.misc;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.datagen.providers.tags.ConventionTags;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.simibubi.create.AllBlocks;
import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAItems;
import mekanism.common.registries.MekanismItems;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.pedroksl.advanced_ae.common.definitions.AAEBlocks;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;
import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.common.data.SFTRecipes;
import org.leodreamer.sftcore.common.data.recipe.utils.SFTVanillaRecipeHelper;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.*;
import static com.gregtechceu.gtceu.common.data.GTMachines.HULL;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ASSEMBLER_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.MIXER_RECIPES;
import static org.leodreamer.sftcore.common.data.recipe.SFTRecipeTypes.CERTUS_QUARTZ_CHARGE_RECIPES;
import static org.leodreamer.sftcore.common.data.recipe.SFTRecipeTypes.LARGE_INSCRIBER;

public final class AE2Recipes {
    public static void init(Consumer<FinishedRecipe> provider) {
        CERTUS_QUARTZ_CHARGE_RECIPES.recipeBuilder("certus_quartz_charge")
                .inputItems(AEItems.CERTUS_QUARTZ_CRYSTAL.asItem(), 32)
                .inputFluids(GTMaterials.Water.getFluid(1000))
                .outputItems(AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED.asItem(), 32)
                .duration(50)
                .EUt(VA[MV])
                .save(provider);

        MIXER_RECIPES.recipeBuilder(SFTCore.id("fluix_crystal"))
                .outputItems(AEItems.FLUIX_CRYSTAL.asItem(), 2)
                .inputItems(AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED.asItem())
                .inputItems(dust, Redstone)
                .inputItems(gem, NetherQuartz)
                .inputFluids(Water, 50)
                .duration(20)
                .EUt(VA[ULV])
                .save(provider);

        MIXER_RECIPES.recipeBuilder(SFTCore.id("sky_steel_ingot"))
                .outputItems(MEGAItems.SKY_STEEL_INGOT.asItem(), 2)
                .inputItems(AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED.asItem())
                .inputItems(ingot, Iron)
                .inputItems(AEBlocks.SKY_STONE_BLOCK.asItem())
                .inputFluids(Lava, 50)
                .duration(20)
                .EUt(VA[ULV])
                .save(provider);

        SFTVanillaRecipeHelper.addShapedRecipe("wireless_connect")
                .pattern("ABA", "CDC", "ABA")
                .arg('A', AEItems.SKY_DUST)
                .arg('B', AEItems.WIRELESS_RECEIVER)
                .arg('C', ConventionTags.SMART_DENSE_CABLE)
                .arg('D', HULL[MV])
                .output(new ItemStack(EPPItemAndBlock.WIRELESS_CONNECTOR, 4))
                .save(provider);

        SFTVanillaRecipeHelper.addShapedRecipe("reaction_chamber")
                .pattern("ABA", "ACA", "DED")
                .arg('A', AEItems.FLUIX_DUST)
                .arg('B', AEItems.CELL_COMPONENT_256K)
                .arg('C', AEBlocks.VIBRATION_CHAMBER)
                .arg('D', Items.GLOWSTONE)
                .arg('E', Items.BUCKET)
                .output(AAEBlocks.REACTION_CHAMBER.stack())
                .save(provider);

        SFTVanillaRecipeHelper.addShapedRecipe("creative_energy_cell")
                .pattern(" A ", "ABA", " A ")
                .arg('A', MEGABlocks.MEGA_ENERGY_CELL)
                .arg('B', MEGAItems.ACCUMULATION_PROCESSOR)
                .output(AEBlocks.CREATIVE_ENERGY_CELL.stack())
                .save(provider);

        inscribe(provider, "calculation", AEItems.CERTUS_QUARTZ_CRYSTAL.asItem(), AEItems.CALCULATION_PROCESSOR.asItem());
        inscribe(provider, "logic", Items.GOLD_INGOT, AEItems.LOGIC_PROCESSOR.asItem());
        inscribe(provider, "engineering", Items.DIAMOND, AEItems.ENGINEERING_PROCESSOR.asItem());
        inscribe(provider, "accumulation", MEGAItems.SKY_STEEL_INGOT.asItem(), MEGAItems.ACCUMULATION_PROCESSOR.asItem());
        inscribe(provider, "quantum", AAEItems.QUANTUM_ALLOY.asItem(), AAEItems.QUANTUM_PROCESSOR.asItem());

        var sink = SFTRecipes.getItemById("cookingforblockheads", "sink");

        if (sink != null) {
            var waterCell = getInfinityCell('f', "minecraft:water");
            ASSEMBLER_RECIPES.recipeBuilder(SFTCore.id("water_cell"))
                    .outputItems(waterCell)
                    .inputItems(sink, 4)
                    .inputItems(AEItems.CELL_COMPONENT_4K.asItem())
                    .inputItems(AEItems.FLUID_CELL_HOUSING.asItem())
                    .inputFluids(Glue.getFluid(500))
                    .duration(40)
                    .EUt(VA[LV])
                    .save(provider);
        }

        var cobblestoneCell = getInfinityCell('i', "minecraft:cobblestone");
        ASSEMBLER_RECIPES.recipeBuilder(SFTCore.id("cobblestone_cell"))
                .outputItems(cobblestoneCell)
                .inputItems(Items.COBBLESTONE, 4)
                .inputItems(MekanismItems.POLONIUM_PELLET)
                .inputItems(AEItems.CELL_COMPONENT_16K.asItem())
                .inputItems(AEItems.ITEM_CELL_HOUSING.asItem())
                .inputFluids(Glue.getFluid(1000))
                .duration(80)
                .EUt(VA[LV])
                .save(provider);

        var lavaCell = getInfinityCell('f', "minecraft:lava");
        ASSEMBLER_RECIPES.recipeBuilder(SFTCore.id("lava_cell"))
                .outputItems(lavaCell)
                .inputItems(AllBlocks.FLUID_TANK.asItem(), 36)
                .inputItems(AllBlocks.HOSE_PULLEY.asItem())
                .inputItems(AllBlocks.LARGE_WATER_WHEEL.asItem(), 2)
                .inputItems(AEBlocks.QUANTUM_LINK.asItem(), 2)
                .inputItems(AEBlocks.QUANTUM_RING.asItem(), 16)
                .inputItems(AEItems.QUANTUM_ENTANGLED_SINGULARITY.asItem(), 2)
                .inputItems(AEItems.CELL_COMPONENT_64K.asItem())
                .inputItems(AEItems.FLUID_CELL_HOUSING.asItem())
                .inputFluids(Glue.getFluid(4000))
                .duration(80)
                .EUt(VA[MV])
                .save(provider);
    }

    private static void inscribe(Consumer<FinishedRecipe> provider, String id, Item ingredient, Item processor) {
        LARGE_INSCRIBER.recipeBuilder(SFTCore.id("inscriber/" + id))
                .inputItems(ingredient, 16)
                .inputItems(AEItems.SILICON.asItem(), 12)
                .inputFluids(Redstone.getFluid(16 * GTValues.L))
                .outputItems(processor, 16)
                .duration(100)
                .EUt(VA[MV])
                .save(provider);
    }

    private static ItemStack getInfinityCell(char type, String id) {
        var cell = new ItemStack(EPPItemAndBlock.INFINITY_CELL);
        var record = new CompoundTag();
        record.putString("#c", "ae2:" + type);
        record.putString("id", id);
        var nbt = new CompoundTag();
        nbt.put("record", record);
        cell.setTag(nbt);
        return cell;
    }
}
