package org.leodreamer.sftcore.common.data.recipe.misc;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.common.data.machines.GTMultiMachines;
import com.gregtechceu.gtceu.data.recipe.GTCraftingComponents;
import net.minecraft.data.recipes.FinishedRecipe;
import org.leodreamer.sftcore.SFTCore;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import static com.gregtechceu.gtceu.common.data.GTMachines.*;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ASSEMBLER_RECIPES;
import static com.hepdd.gtmthings.data.CustomItems.*;
import static com.hepdd.gtmthings.data.WirelessMachines.*;

public final class GTMTRecipes {
    public static void init(Consumer<FinishedRecipe> provider) {

        final var WIRELESS_ENERGY_RECEIVE_COVER = List.of(
                WIRELESS_ENERGY_RECEIVE_COVER_LV,
                WIRELESS_ENERGY_RECEIVE_COVER_LV,
                WIRELESS_ENERGY_RECEIVE_COVER_MV,
                WIRELESS_ENERGY_RECEIVE_COVER_HV,
                WIRELESS_ENERGY_RECEIVE_COVER_EV,
                WIRELESS_ENERGY_RECEIVE_COVER_IV,
                WIRELESS_ENERGY_RECEIVE_COVER_LUV,
                WIRELESS_ENERGY_RECEIVE_COVER_ZPM,
                WIRELESS_ENERGY_RECEIVE_COVER_UV
        );

        final var WIRELESS_ENERGY_RECEIVE_COVER_4A = List.of(
                WIRELESS_ENERGY_RECEIVE_COVER_LV_4A,
                WIRELESS_ENERGY_RECEIVE_COVER_LV_4A,
                WIRELESS_ENERGY_RECEIVE_COVER_MV_4A,
                WIRELESS_ENERGY_RECEIVE_COVER_HV_4A,
                WIRELESS_ENERGY_RECEIVE_COVER_EV_4A,
                WIRELESS_ENERGY_RECEIVE_COVER_IV_4A,
                WIRELESS_ENERGY_RECEIVE_COVER_LUV_4A,
                WIRELESS_ENERGY_RECEIVE_COVER_ZPM_4A,
                WIRELESS_ENERGY_RECEIVE_COVER_UV_4A
        );

        for (int tier = GTValues.LV; tier <= GTValues.UV; tier++) {
            var wirelessHatch = WIRELESS_ENERGY_INPUT_HATCH_256A[tier];
            var hatch = LASER_INPUT_HATCH_256[tier];
            if (hatch == null || wirelessHatch == null) continue;

            ASSEMBLER_RECIPES.recipeBuilder(SFTCore.id(GTValues.VN[tier].toLowerCase(Locale.ROOT) + "_256a_wireless_laser_target_hatch"))
                    .inputItems(hatch)
                    .inputItems(GTCraftingComponents.SENSOR.get(tier), 2)
                    .inputItems(WIRELESS_ENERGY_RECEIVE_COVER_4A.get(tier))
                    .circuitMeta(1)
                    .outputItems(wirelessHatch)
                    .duration(300)
                    .EUt(GTValues.VA[tier])
                    .save(provider);
        }

        for (int tier = GTValues.LV; tier <= GTValues.UV; tier++) {
            var wirelessHatch = WIRELESS_ENERGY_OUTPUT_HATCH_256A[tier];
            var hatch = LASER_OUTPUT_HATCH_256[tier];
            if (hatch == null || wirelessHatch == null) continue;

            ASSEMBLER_RECIPES.recipeBuilder(SFTCore.id(GTValues.VN[tier].toLowerCase(Locale.ROOT) + "_256a_wireless_laser_source_hatch"))
                    .inputItems(hatch)
                    .inputItems(GTCraftingComponents.SENSOR.get(tier), 2)
                    .inputItems(WIRELESS_ENERGY_RECEIVE_COVER_4A.get(tier))
                    .circuitMeta(1)
                    .outputItems(wirelessHatch)
                    .duration(300)
                    .EUt(GTValues.VA[tier])
                    .save(provider);
        }

        for (int tier = GTValues.LV; tier <= GTValues.UV; tier++) {
            var wirelessHatch = WIRELESS_ENERGY_INPUT_HATCH_1024A[tier];
            var hatch = LASER_INPUT_HATCH_1024[tier];
            if (hatch == null || wirelessHatch == null) continue;

            ASSEMBLER_RECIPES.recipeBuilder(SFTCore.id(GTValues.VN[tier].toLowerCase(Locale.ROOT) + "_1024a_wireless_laser_target_hatch"))
                    .inputItems(hatch)
                    .inputItems(GTCraftingComponents.SENSOR.get(tier), 4)
                    .inputItems(GTCraftingComponents.EMITTER.get(tier), 2)
                    .inputItems(GTCraftingComponents.CABLE.get(tier), 2)
                    .inputItems(WIRELESS_ENERGY_RECEIVE_COVER_4A.get(tier), 4)
                    .inputItems(GTMultiMachines.ACTIVE_TRANSFORMER)
                    .circuitMeta(2)
                    .outputItems(wirelessHatch)
                    .duration(300)
                    .EUt(GTValues.VA[tier])
                    .save(provider);
        }

        for (int tier = GTValues.LV; tier <= GTValues.UV; tier++) {
            var wirelessHatch = WIRELESS_ENERGY_OUTPUT_HATCH_1024A[tier];
            var hatch = LASER_OUTPUT_HATCH_1024[tier];
            if (hatch == null || wirelessHatch == null) continue;

            ASSEMBLER_RECIPES.recipeBuilder(SFTCore.id(GTValues.VN[tier].toLowerCase(Locale.ROOT) + "_1024a_wireless_laser_source_hatch"))
                    .inputItems(hatch)
                    .inputItems(GTCraftingComponents.SENSOR.get(tier), 4)
                    .inputItems(GTCraftingComponents.EMITTER.get(tier), 2)
                    .inputItems(GTCraftingComponents.CABLE.get(tier), 2)
                    .inputItems(WIRELESS_ENERGY_RECEIVE_COVER_4A.get(tier), 4)
                    .inputItems(GTMultiMachines.ACTIVE_TRANSFORMER)
                    .circuitMeta(2)
                    .outputItems(wirelessHatch)
                    .duration(300)
                    .EUt(GTValues.VA[tier])
                    .save(provider);
        }

        for (int tier = GTValues.LV; tier <= GTValues.UV; tier++) {
            var wirelessHatch = WIRELESS_ENERGY_INPUT_HATCH_4096A[tier];
            var hatch = LASER_INPUT_HATCH_4096[tier];
            if (hatch == null || wirelessHatch == null) continue;

            ASSEMBLER_RECIPES.recipeBuilder(SFTCore.id(GTValues.VN[tier].toLowerCase(Locale.ROOT) + "_4096a_wireless_laser_target_hatch"))
                    .inputItems(hatch)
                    .inputItems(GTCraftingComponents.SENSOR.get(tier), 8)
                    .inputItems(GTCraftingComponents.EMITTER.get(tier), 4)
                    .inputItems(GTCraftingComponents.CABLE_DOUBLE.get(tier), 4)
                    .inputItems(WIRELESS_ENERGY_RECEIVE_COVER_4A.get(tier), 16)
                    .inputItems(GTMultiMachines.ACTIVE_TRANSFORMER)
                    .circuitMeta(3)
                    .outputItems(wirelessHatch)
                    .duration(300)
                    .EUt(GTValues.VA[tier])
                    .save(provider);
        }

        for (int tier = GTValues.LV; tier <= GTValues.UV; tier++) {
            var wirelessHatch = WIRELESS_ENERGY_OUTPUT_HATCH_4096A[tier];
            var hatch = LASER_OUTPUT_HATCH_4096[tier];
            if (hatch == null || wirelessHatch == null) continue;

            ASSEMBLER_RECIPES.recipeBuilder(SFTCore.id(GTValues.VN[tier].toLowerCase(Locale.ROOT) + "_4096a_wireless_laser_source_hatch"))
                    .inputItems(hatch)
                    .inputItems(GTCraftingComponents.SENSOR.get(tier), 8)
                    .inputItems(GTCraftingComponents.EMITTER.get(tier), 4)
                    .inputItems(GTCraftingComponents.CABLE_DOUBLE.get(tier), 4)
                    .inputItems(WIRELESS_ENERGY_RECEIVE_COVER_4A.get(tier), 16)
                    .inputItems(GTMultiMachines.ACTIVE_TRANSFORMER)
                    .circuitMeta(3)
                    .outputItems(wirelessHatch)
                    .duration(300)
                    .EUt(GTValues.VA[tier])
                    .save(provider);
        }
    }
}
