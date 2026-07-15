package org.leodreamer.sftcore.common.data.recipe;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.api.feature.IGTRecipeTypeGas;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.recipe.GTRecipeSerializer;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.gui.RecipeUIModifier;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.common.data.GTSoundEntries;
import com.gregtechceu.gtceu.common.mui.GTGuiTextures;
import com.gregtechceu.gtceu.common.recipe.gui.GTRecipeUIModifiers;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeType;

import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.GENERATOR;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.MULTIBLOCK;
import static org.leodreamer.sftcore.common.data.recipe.SFTRecipeUIModifiers.*;

public final class SFTRecipeTypes {

    public static final String KINETIC = "kinetic";

    public static void init() {}

    // create integration
    public static final GTRecipeType FISHBIG_MAKER_RECIPES = register("fishbig_maker", KINETIC)
        .setMaxIOSize(9, 1, 3, 0)
        .UI(
            builder -> builder.setProgressBar(GTGuiTextures.PROGRESS_ARROW)
                .addRecipeUIModifier(RecipeUIModifier.all(SHAFT_INFO, STRESS_INFO))
        )
        .setSound(GTSoundEntries.CHEMICAL)
        .setMaxTooltips(4);

    public static final GTRecipeType MECHANICAL_BOX_RECIPES = register("mechanical_box", KINETIC)
        .setMaxIOSize(18, 18, 9, 9)
        .UI(
            builder -> builder.setProgressBar(GTGuiTextures.PROGRESS_ARROW)
                .addRecipeUIModifier(RecipeUIModifier.all(SHAFT_INFO, STRESS_INFO))
        )
        .setSound(GTSoundEntries.ASSEMBLER)
        .setMaxTooltips(4);

    // ae2 integration
    public static final GTRecipeType CERTUS_QUARTZ_CHARGE_RECIPES = register("certus_quartz_charge", MULTIBLOCK)
        .setEUIO(IO.IN)
        .setMaxIOSize(2, 1, 1, 0)
        .UI(builder -> builder.setProgressBar(GTGuiTextures.PROGRESS_ARROW))
        .setSound(GTSoundEntries.ELECTROLYZER);

    public static final GTRecipeType LARGE_INSCRIBER = register("large_inscriber", MULTIBLOCK)
        .setEUIO(IO.IN)
        .setMaxIOSize(3, 1, 1, 0)
        .UI(builder -> builder.setProgressBar(GTGuiTextures.PROGRESS_ARROW))
        .setSound(GTSoundEntries.ASSEMBLER);

    // mekanism integration
    public static final GTRecipeType MEKANISM_NUCLEAR_REACTION_RECIPES = registerGas(
        "mekanism_nuclear_reaction",
        GENERATOR
    ).sftcore$setMaxIOSize(1, 0, 1, 1, 1, 1)
        .sftcore$gasUI(builder -> builder.setProgressBar(GTGuiTextures.PROGRESS_FUSION))
        .sftcore$self()
        .setEUIO(IO.OUT)
        .setSound(GTSoundEntries.TURBINE);

    public static final GTRecipeType MEKANISM_PROCESSING_RECIPES = registerGas("common_mekanism_processing", MULTIBLOCK)
        .sftcore$setMaxIOSize(3, 3, 3, 3, 3, 3)
        .sftcore$gasUI(builder -> builder.setProgressBar(GTGuiTextures.PROGRESS_ARROW))
        .sftcore$self()
        .setEUIO(IO.IN)
        .setSound(GTSoundEntries.MIXER);

    // GT recipe
    public static final GTRecipeType GREENHOUSE_RECIPES = register("greenhouse", MULTIBLOCK)
        .setEUIO(IO.IN)
        .setMaxIOSize(3, 4, 1, 0)
        .UI(builder -> builder.setProgressBar(GTGuiTextures.PROGRESS_ARROW))
        .setSound(GTSoundEntries.SCIENCE);

    public static final GTRecipeType OIL_DRILLING_RECIPES = register("oil_drilling_rig", MULTIBLOCK)
        .setEUIO(IO.IN)
        .setMaxIOSize(1, 1, 1, 1)
        .UI(builder -> builder.setProgressBar(GTGuiTextures.PROGRESS_ARROW))
        .setSound(GTSoundEntries.DRILL_TOOL);

    public static final GTRecipeType DESULFURIZE_RECIPES = register("desulfurize", MULTIBLOCK)
        .setEUIO(IO.IN)
        .setMaxIOSize(1, 2, 1, 2)
        .UI(builder -> builder.setProgressBar(GTGuiTextures.PROGRESS_ARROW_MULTIPLE))
        .setSound(GTSoundEntries.CHEMICAL);

    public static final GTRecipeType HURRY_UP_RECIPES = register("hurry_up", MULTIBLOCK)
        .setEUIO(IO.IN)
        .setMaxIOSize(2, 1, 2, 0)
        .UI(builder -> builder.setProgressBar(GTGuiTextures.PROGRESS_ARROW))
        .setSound(GTSoundEntries.ASSEMBLER);

    public static final GTRecipeType LARGE_GAS_COLLECTOR_RECIPES = register("large_gas_collector", MULTIBLOCK)
        .setEUIO(IO.IN)
        .setMaxIOSize(2, 0, 0, 1)
        .UI(builder -> builder.setProgressBar(GTGuiTextures.PROGRESS_GAS_COLLECTOR))
        .setSound(GTSoundEntries.COOLING);

    public static final GTRecipeType SEMICONDUCTOR_BLAST_RECIPES = register("semiconductor_blast_furnace", MULTIBLOCK)
        .setEUIO(IO.IN)
        .setMaxIOSize(3, 1, 1, 0)
        .UI(
            builder -> builder
                .setProgressBar(GTGuiTextures.PROGRESS_ARROW)
                .addRecipeUIModifier(GTRecipeUIModifiers.TEMP_COIL_INFO)
        )
        .setSound(GTSoundEntries.FURNACE);

    public static final GTRecipeType ORE_PROCESSING = register("ore_processing", MULTIBLOCK)
        .setEUIO(IO.IN)
        .setMaxIOSize(2, 12, 1, 0)
        .UI(builder -> builder.setProgressBar(GTGuiTextures.PROGRESS_ARROW))
        .setSound(GTSoundEntries.MACERATOR);

    @SuppressWarnings("deprecation")
    public static GTRecipeType register(String name, String group, RecipeType<?>... proxyRecipes) {
        var recipeType = new GTRecipeType(SFTCore.id(name), group, proxyRecipes);
        GTRegistries.register(BuiltInRegistries.RECIPE_TYPE, recipeType.registryName, recipeType);
        GTRegistries.register(
            BuiltInRegistries.RECIPE_SERIALIZER, recipeType.registryName, new GTRecipeSerializer()
        );
        GTRegistries.RECIPE_TYPES.register(recipeType.registryName, recipeType);
        return recipeType;
    }

    public static IGTRecipeTypeGas registerGas(String name, String group, RecipeType<?>... proxyRecipe) {
        return (IGTRecipeTypeGas) register(name, group, proxyRecipe);
    }
}
