package org.leodreamer.sftcore.common.data.recipe.misc;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.OreProperty;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;

import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.HIGH_SIFTER_OUTPUT;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Stone;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Water;
import static org.leodreamer.sftcore.common.data.recipe.SFTRecipeTypes.ORE_PROCESSING;

/**
 * These codes are from <a href=
 * "https://github.com/GregTech-Odyssey/GTOCore/blob/main/src/main/java/com/gtocore/data/recipe/generated/GTOOreRecipeHandler.java">GregTech
 * Odyssey</a>
 */
public class OreProcessRecipes {

    public static void init(Consumer<FinishedRecipe> provider) {
        for (Material material : GTCEuAPI.materialManager.getRegisteredMaterials()) {
            var property = material.getProperty(PropertyKey.ORE);
            if (property == null || !material.shouldGenerateRecipesFor(ore)) {
                continue;
            }
            processOre(material, property, provider);
        }
    }

    private static void processOre(Material material, OreProperty property, Consumer<FinishedRecipe> provider) {
        var crushedStack = ChemicalHelper.get(crushed, material);
        if (crushedStack.isEmpty()) return;

        int oreMultiplier = property.getOreMultiplier();
        long mass = material.getMass();
        int dur = (int) Math.max(4, Math.sqrt(mass));
        crushedStack.setCount(Math.max(oreMultiplier * 6, 1));
        var tag = TagPrefix.ore.getItemTags(material)[0];
        var byproductMaterial = property.getOreByProduct(0, material);
        var byproductStack = ChemicalHelper.get(gem, byproductMaterial).isEmpty() ?
            ChemicalHelper.get(dust, byproductMaterial) : ChemicalHelper.get(gem, byproductMaterial);
        int crushedAmount = crushedStack.getCount();

        /////////////////////////////////////////
        // 1 macerate-macerate-centrifuge
        /////////////////////////////////////////
        var op1 = ORE_PROCESSING
            .recipeBuilder("processor_1_" + material.getName())
            .circuitMeta(1)
            .inputItems(tag)
            .outputItems(dust, material, crushedAmount)
            .chancedOutput(byproductStack, 1400, 850)
            .chancedOutput(dust, byproductMaterial, property.getByProductMultiplier() * crushedAmount, 1400, 850)
            .duration((int) (dur + dur + mass) / 2)
            .EUt(12);
        attachSecondaryMaterial(op1, crushedAmount);
        if (byproductMaterial.hasProperty(PropertyKey.DUST)) {
            op1.chancedOutput(dust, byproductMaterial, crushedAmount, 1111, 0);
        }
        op1.save(provider);

        /////////////////////////////////////////
        // 2 macerate-washing-thermal_centrifuge-macerate
        /////////////////////////////////////////
        var byproductMaterial1 = property.getOreByProduct(1, material);
        var byproductStack2 = ChemicalHelper.get(dust, property.getOreByProduct(2, material), crushedAmount);
        var crushedCentrifugedStack = ChemicalHelper.get(crushedRefined, material);
        if (!crushedCentrifugedStack.isEmpty()) {
            var op2 = ORE_PROCESSING
                .recipeBuilder("processor_2_" + material.getName())
                .circuitMeta(2)
                .inputItems(tag)
                .inputFluids(Water.getFluid(100 * crushedAmount))
                .outputItems(dust, material, crushedAmount)
                .chancedOutput(byproductStack, 1400, 850)
                .chancedOutput(dust, byproductMaterial, crushedAmount, 3333, 0)
                .outputItems(dust, Stone, crushedAmount)
                .chancedOutput(dust, byproductMaterial1, crushedAmount, 3333, 0)
                .chancedOutput(byproductStack2, 1400, 850)
                .duration(dur + (200 + 200 + dur) / 2)
                .EUt(12);
            attachSecondaryMaterial(op2, crushedAmount);
            op2.save(provider);
        }

        /////////////////////////////////////////
        // 3 macerate-washing-macerate-centrifuge
        /////////////////////////////////////////
        var byproductStack1 = ChemicalHelper.get(dust, byproductMaterial1, crushedAmount);
        var op3 = ORE_PROCESSING
            .recipeBuilder("processor_3_" + material.getName())
            .circuitMeta(3)
            .inputItems(tag)
            .inputFluids(Water.getFluid(100 * crushedAmount))
            .outputItems(dust, material, crushedAmount)
            .chancedOutput(byproductStack, 1400, 850)
            .chancedOutput(dust, byproductMaterial, crushedAmount, 3333, 0)
            .outputItems(dust, Stone, crushedAmount)
            .chancedOutput(byproductStack1, 1400, 850)
            .chancedOutput(dust, byproductMaterial1, crushedAmount, 1111, 0)
            .duration(dur + (200 + dur + 16) / 2)
            .EUt(12);
        attachSecondaryMaterial(op3, crushedAmount);
        op3.save(provider);

        /////////////////////////////////////////
        // 4 macerate-washing-sifter-centrifuge
        /////////////////////////////////////////
        if (material.hasProperty(PropertyKey.GEM)) {
            var exquisiteStack = ChemicalHelper.get(gemExquisite, material);
            var flawlessStack = ChemicalHelper.get(gemFlawless, material);
            var gemStack = ChemicalHelper.get(gem, material);
            if (material.hasFlag(HIGH_SIFTER_OUTPUT)) {
                var op4 = ORE_PROCESSING
                    .recipeBuilder("processor_4_" + material.getName())
                    .circuitMeta(4)
                    .inputItems(tag)
                    .inputFluids(Water.getFluid(100 * crushedAmount))
                    .outputItems(dust, material, crushedAmount)
                    .chancedOutput(byproductStack, 1400, 850)
                    .chancedOutput(dust, byproductMaterial, crushedAmount, 3333, 0)
                    .outputItems(dust, Stone, crushedAmount)
                    .chancedOutput(exquisiteStack, 500, 150)
                    .chancedOutput(flawlessStack, 1500, 200)
                    .chancedOutput(gemStack, 5000, 1000)
                    .chancedOutput(dust, byproductMaterial1, crushedAmount, 1111, 0)
                    .duration(dur + (200 + 210 + 16) / 2)
                    .EUt(12);
                attachSecondaryMaterial(op4, crushedAmount);
                op4.save(provider);
            } else {
                var op4 = ORE_PROCESSING
                    .recipeBuilder("processor_4_" + material.getName())
                    .circuitMeta(4)
                    .inputItems(tag)
                    .inputFluids(Water.getFluid(100 * crushedAmount))
                    .outputItems(dust, material, crushedAmount)
                    .chancedOutput(byproductStack, 1400, 850)
                    .chancedOutput(dust, byproductMaterial, crushedAmount, 3333, 0)
                    .outputItems(dust, Stone, crushedAmount)
                    .chancedOutput(exquisiteStack, 300, 100)
                    .chancedOutput(flawlessStack, 1000, 150)
                    .chancedOutput(gemStack, 3500, 500)
                    .chancedOutput(dust, byproductMaterial1, crushedAmount, 1111, 0)
                    .duration(dur + (200 + 210 + 16) / 2)
                    .EUt(12);
                attachSecondaryMaterial(op4, crushedAmount);
                op4.save(provider);
            }
        }

        if (property.getWashedIn().first().isNull()) {
            return;
        }

        /// Bathing recipes ///

        var washingByproduct = property.getOreByProduct(3, material);
        var washedInTuple = property.getWashedIn();
        /////////////////////////////////////////
        // 5 macerate-bathing-thermal_centrifuge-macerate
        /////////////////////////////////////////
        if (!crushedCentrifugedStack.isEmpty()) {
            var op5 = ORE_PROCESSING
                .recipeBuilder("processor_5_" + material.getName())
                .circuitMeta(5)
                .inputItems(tag)
                .inputFluids(washedInTuple.first().getFluid(washedInTuple.rightInt() * crushedAmount))
                .outputItems(dust, material, crushedAmount)
                .chancedOutput(byproductStack, 1400, 850)
                .chancedOutput(dust, washingByproduct, property.getByProductMultiplier() * crushedAmount, 7000, 580)
                .chancedOutput(dust, Stone, crushedAmount, 4000, 650)
                .chancedOutput(dust, byproductMaterial1, crushedAmount, 3333, 0)
                .chancedOutput(byproductStack2, 1400, 850)
                .duration(dur + (200 + 200 + dur) / 2)
                .EUt(12);
            attachSecondaryMaterial(op5, crushedAmount);
            op5.save(provider);
        }

        /////////////////////////////////////////
        // 6 macerate-bathing-macerate-centrifuge
        /////////////////////////////////////////
        var op6 = ORE_PROCESSING
            .recipeBuilder("processor_6_" + material.getName())
            .circuitMeta(6)
            .inputItems(tag)
            .inputFluids(washedInTuple.first().getFluid(washedInTuple.rightInt() * crushedAmount))
            .outputItems(dust, material, crushedAmount)
            .chancedOutput(byproductStack, 1400, 850)
            .chancedOutput(dust, washingByproduct, property.getByProductMultiplier() * crushedAmount, 7000, 580)
            .chancedOutput(dust, Stone, crushedAmount, 4000, 650)
            .chancedOutput(byproductStack1, 1400, 850)
            .chancedOutput(dust, byproductMaterial1, crushedAmount, 1111, 0)
            .duration(dur + (200 + dur + 16) / 2)
            .EUt(12);
        attachSecondaryMaterial(op6, crushedAmount);
        op6.save(provider);

        /////////////////////////////////////////
        // 7 macerate-bathing-sifter-centrifuge
        /////////////////////////////////////////
        if (material.hasProperty(PropertyKey.GEM)) {
            var exquisiteStack = ChemicalHelper.get(gemExquisite, material);
            var flawlessStack = ChemicalHelper.get(gemFlawless, material);
            var gemStack = ChemicalHelper.get(gem, material);
            if (material.hasFlag(HIGH_SIFTER_OUTPUT)) {
                var op7 = ORE_PROCESSING
                    .recipeBuilder("processor_7_" + material.getName())
                    .circuitMeta(7)
                    .inputItems(tag)
                    .inputFluids(washedInTuple.first().getFluid(washedInTuple.rightInt() * crushedAmount))
                    .outputItems(dust, material, crushedAmount)
                    .chancedOutput(byproductStack, 1400, 850)
                    .chancedOutput(
                        dust, washingByproduct, property.getByProductMultiplier() * crushedAmount, 7000, 580
                    )
                    .chancedOutput(dust, Stone, crushedAmount, 4000, 650)
                    .chancedOutput(exquisiteStack, 500, 150)
                    .chancedOutput(flawlessStack, 1500, 200)
                    .chancedOutput(gemStack, 5000, 1000)
                    .chancedOutput(dust, byproductMaterial1, crushedAmount, 1111, 0)
                    .duration(dur + (200 + 210 + 16) / 2)
                    .EUt(12);
                attachSecondaryMaterial(op7, crushedAmount);
                op7.save(provider);
            } else {
                var op7 = ORE_PROCESSING
                    .recipeBuilder("processor_7_" + material.getName())
                    .circuitMeta(7)
                    .inputItems(tag)
                    .inputFluids(washedInTuple.first().getFluid(washedInTuple.rightInt() * crushedAmount))
                    .outputItems(dust, material, crushedAmount)
                    .chancedOutput(byproductStack, 1400, 850)
                    .chancedOutput(
                        dust, washingByproduct, property.getByProductMultiplier() * crushedAmount, 7000, 580
                    )
                    .chancedOutput(dust, Stone, crushedAmount, 4000, 650)
                    .chancedOutput(exquisiteStack, 300, 100)
                    .chancedOutput(flawlessStack, 1000, 150)
                    .chancedOutput(gemStack, 3500, 500)
                    .chancedOutput(dust, byproductMaterial1, crushedAmount, 1111, 0)
                    .duration(dur + (200 + 210 + 16) / 2)
                    .EUt(12);
                attachSecondaryMaterial(op7, crushedAmount);
                op7.save(provider);
            }
        }
    }

    private static void attachSecondaryMaterial(GTRecipeBuilder builder, int amount) {
        for (var secondaryMaterial : TagPrefix.ore.secondaryMaterials()) {
            if (secondaryMaterial.material().hasProperty(PropertyKey.DUST)) {
                builder.chancedOutput(
                    ChemicalHelper.getGem(secondaryMaterial).copyWithCount(amount), 6700, 800
                );
            }
        }
    }
}
