package org.leodreamer.sftcore.common.data.recipe;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.block.ICoilType;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.recipe.GTRecipeSerializer;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.common.data.GTSoundEntries;
import com.lowdragmc.lowdraglib.gui.texture.ProgressTexture;
import com.lowdragmc.lowdraglib.gui.widget.SlotWidget;
import com.lowdragmc.lowdraglib.utils.CycleItemStackHandler;
import com.lowdragmc.lowdraglib.utils.LocalizationUtils;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import org.leodreamer.sftcore.SFTCore;

import java.util.List;

import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.GENERATOR;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.MULTIBLOCK;

public final class SFTRecipeTypes {
    public static void init() {
    }

    // create integration
    public static final GTRecipeType FISHBIG_MAKER_RECIPES = register("fishbig_maker", MULTIBLOCK)
            .setEUIO(IO.IN)
            .setMaxIOSize(9, 1, 3, 0)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.CHEMICAL);

    // ae2 integration
    public static final GTRecipeType CERTUS_QUARTZ_CHARGE_RECIPES = register("certus_quartz_charge", MULTIBLOCK)
            .setEUIO(IO.IN)
            .setMaxIOSize(2, 1, 1, 0)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.ELECTROLYZER);

    public static final GTRecipeType LARGE_INSCRIBER = register("large_inscriber", MULTIBLOCK)
            .setEUIO(IO.IN)
            .setMaxIOSize(3, 1, 1, 0)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.ASSEMBLER);

    // mekanism integration
    public static final GTRecipeType MEKANISM_NUCLEAR_REACTION_RECIPES = register("mekanism_nuclear_reaction", GENERATOR)
            .setEUIO(IO.OUT)
            .setMaxIOSize(1, 0, 1, 1)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.TURBINE);

    public static final GTRecipeType MEKANISM_PROCESSING_RECIPES = register("common_mekanism_processing", MULTIBLOCK)
            .setEUIO(IO.IN)
            .setMaxIOSize(6, 3, 3, 3)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.MIXER);

    // GT recipe
    public static final GTRecipeType GREENHOUSE_RECIPES = register("greenhouse", MULTIBLOCK)
            .setEUIO(IO.IN)
            .setMaxIOSize(3, 4, 1, 0)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.SCIENCE);

    public static final GTRecipeType OIL_DRILLING_RECIPES = register("oil_drilling_rig", MULTIBLOCK)
            .setEUIO(IO.IN)
            .setMaxIOSize(1, 1, 1, 1)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.DRILL_TOOL);

    public static final GTRecipeType DESULFURIZE_RECIPES = register("desulfurize", MULTIBLOCK)
            .setEUIO(IO.IN)
            .setMaxIOSize(1, 2, 1, 2)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.CHEMICAL);

    public static final GTRecipeType HURRY_UP_RECIPES = register("hurry_up", MULTIBLOCK)
            .setEUIO(IO.IN)
            .setMaxIOSize(2, 1, 2, 0)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.ASSEMBLER);

    public static final GTRecipeType SEMICONDUCTOR_BLAST_RECIPES = register("semiconductor_blast_furnace", MULTIBLOCK)
            .setEUIO(IO.IN)
            .setMaxIOSize(3, 1, 1, 0)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .addDataInfo(data -> {
                int temp = data.getInt("ebf_temp");
                return LocalizationUtils.format("gtceu.recipe.temperature", temp);
            })
            .addDataInfo(data -> {
                int temp = data.getInt("ebf_temp");
                ICoilType requiredCoil = ICoilType.getMinRequiredType(temp);

                if (requiredCoil != null && !requiredCoil.getMaterial().isNull()) {
                    return LocalizationUtils.format("gtceu.recipe.coil.tier",
                            I18n.get(requiredCoil.getMaterial().getUnlocalizedName()));
                }
                return "";
            })
            .setUiBuilder(
                    ((recipe, widgetGroup) -> {
                        var temp = recipe.data.getInt("ebf_temp");
                        var item = GTCEuAPI.HEATING_COILS.entrySet().stream().filter((coil) -> (coil.getKey().getCoilTemperature() >= temp))
                                .map((coil) -> new ItemStack(coil.getValue().get())).toList();
                        var items = List.of(item);
                        widgetGroup.addWidget(new SlotWidget(new CycleItemStackHandler(items), 0, widgetGroup.getSize().width - 25, widgetGroup.getSize().height - 32, false, false));
                    })
            )
            .setSound(GTSoundEntries.FURNACE);

    public static GTRecipeType register(String name, String group, RecipeType<?>... proxyRecipes) {
        var recipeType = new GTRecipeType(SFTCore.id(name), group, proxyRecipes);
        GTRegistries.register(BuiltInRegistries.RECIPE_TYPE, recipeType.registryName, recipeType);
        GTRegistries.register(BuiltInRegistries.RECIPE_SERIALIZER, recipeType.registryName, new GTRecipeSerializer());
        GTRegistries.RECIPE_TYPES.register(recipeType.registryName, recipeType);
        return recipeType;
    }
}
