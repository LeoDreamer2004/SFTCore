package org.leodreamer.sftcore.common.data.recipe.misc;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.common.data.recipe.SFTRecipeTypes;

import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.*;
import static com.gregtechceu.gtceu.common.data.GTItems.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;

public final class SemiconductorRecipes {

    public static void init(Consumer<FinishedRecipe> provider) {
        SFTRecipeTypes.SEMICONDUCTOR_BLAST_RECIPES
                .recipeBuilder(SFTCore.id("semiconductor/boule"))
                .inputItems(dust, Silicon, 56)
                .inputItems(dustSmall, GalliumArsenide)
                .outputItems(SILICON_BOULE, 2)
                .circuitMeta(2)
                .blastFurnaceTemp(1784)
                .duration(6000)
                .EUt(VA[MV])
                .save(provider);

        SFTRecipeTypes.SEMICONDUCTOR_BLAST_RECIPES
                .recipeBuilder(SFTCore.id("semiconductor/phosphorus_boule"))
                .inputItems(block, Silicon, 14)
                .inputItems(dust, Phosphorus, 8)
                .inputItems(dustSmall, GalliumArsenide, 2)
                .inputFluids(Nitrogen.getFluid(8000))
                .outputItems(PHOSPHORUS_BOULE, 2)
                .blastFurnaceTemp(2484)
                .duration(8000)
                .EUt(VA[HV])
                .save(provider);

        SFTRecipeTypes.SEMICONDUCTOR_BLAST_RECIPES
                .recipeBuilder(SFTCore.id("semiconductor/naquadah_boule"))
                .inputItems(block, Silicon, 28)
                .inputItems(ingot, Naquadah)
                .inputItems(dust, GalliumArsenide)
                .inputFluids(Argon.getFluid(8000))
                .outputItems(NAQUADAH_BOULE, 2)
                .blastFurnaceTemp(5400)
                .duration(10000)
                .EUt(VA[EV])
                .save(provider);

        SFTRecipeTypes.SEMICONDUCTOR_BLAST_RECIPES
                .recipeBuilder(SFTCore.id("semiconductor/neutronium_boule"))
                .inputItems(block, Silicon, 56)
                .inputItems(ingot, Neutronium, 4)
                .inputItems(dust, GalliumArsenide, 2)
                .inputItems(Xenon.getFluid(8000))
                .outputItems(NEUTRONIUM_BOULE, 2)
                .blastFurnaceTemp(6484)
                .duration(12000)
                .EUt(VA[IV])
                .save(provider);
    }
}
