package org.leodreamer.sftcore.common.data.recipe;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.api.feature.IGTRecipeTypeGas;
import org.leodreamer.sftcore.api.kinetics.KineticRecipeHelper;
import org.leodreamer.sftcore.common.data.recipe.builder.SFTRecipeBuilder;
import org.leodreamer.sftcore.common.recipe.condition.RPMCondition;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.recipe.GTRecipeSerializer;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.gui.GTRecipeViewerWidget;
import com.gregtechceu.gtceu.api.recipe.gui.RecipeUIModifier;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.common.data.GTSoundEntries;
import com.gregtechceu.gtceu.common.mui.GTGuiTextures;
import com.gregtechceu.gtceu.common.recipe.gui.GTRecipeUIModifiers;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeType;

import brachy.modularui.api.drawable.IDrawable;
import brachy.modularui.api.drawable.Text;
import brachy.modularui.integration.recipeviewer.RecipeSlotRole;
import brachy.modularui.integration.recipeviewer.RecipeViewerSlotWidget;
import brachy.modularui.integration.recipeviewer.entry.item.ItemStackList;
import com.simibubi.create.AllBlocks;

import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.GENERATOR;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.MULTIBLOCK;

@DataGenScanned
public final class SFTRecipeTypes {

    public static final String KINETIC = "kinetic";

    public static void init() {}

    @RegisterLanguage("Requires Total Stress: %ssu")
    private static final String ACQUIRE_STRESS = "sftcore.recipe.input_stress";

    private static final RecipeUIModifier CONDITIONAL_SHAFT_INFO = (recipe, widget) -> {
        if (recipe.conditions.stream().anyMatch(RPMCondition.class::isInstance)) {
            addShaftInfo(widget);
        }
    };

    private static final RecipeUIModifier SHAFT_INFO = (recipe, widget) -> addShaftInfo(widget);
    private static final RecipeUIModifier STRESS_INFO = (recipe, widget) -> {
        if (recipe.data.contains(SFTRecipeBuilder.INPUT_STRESS)) {
            float stress = recipe.data.getFloat(SFTRecipeBuilder.INPUT_STRESS);
            widget.textComponents.child(Text.lang(ACQUIRE_STRESS, KineticRecipeHelper.format(stress)).asWidget());
        }
    };

    // create integration
    public static final GTRecipeType FISHBIG_MAKER_RECIPES = register("fishbig_maker", KINETIC)
        .setMaxIOSize(9, 1, 3, 0)
        .UI(
            builder -> builder.setProgressBar(GTGuiTextures.PROGRESS_ARROW)
                .addRecipeUIModifier(RecipeUIModifier.all(CONDITIONAL_SHAFT_INFO, STRESS_INFO))
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
        .setEUIO(IO.OUT)
        .UI(builder -> builder.setProgressBar(GTGuiTextures.PROGRESS_FUSION))
        .setSound(GTSoundEntries.TURBINE);

    public static final GTRecipeType MEKANISM_PROCESSING_RECIPES = registerGas("common_mekanism_processing", MULTIBLOCK)
        .sftcore$setMaxIOSize(3, 3, 3, 3, 3, 3)
        .setEUIO(IO.IN)
        .UI(builder -> builder.setProgressBar(GTGuiTextures.PROGRESS_ARROW))
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

    private static void addShaftInfo(GTRecipeViewerWidget widget) {
        widget.textComponents.child(
            RecipeViewerSlotWidget.create()
                .recipeSlotRole(RecipeSlotRole.RENDER_ONLY)
                .value(ItemStackList.of(AllBlocks.SHAFT.asStack()))
                .background(IDrawable.EMPTY)
        );
    }

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
