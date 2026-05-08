package org.leodreamer.sftcore.common.data;

import org.leodreamer.sftcore.SFTCore;

import net.minecraft.data.recipes.FinishedRecipe;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.plate;
import static com.gregtechceu.gtceu.common.data.GTMachines.HULL;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ASSEMBLER_RECIPES;

public class SFTRecipes {

    public static void init(Consumer<FinishedRecipe> provider) {
        ASSEMBLER_RECIPES.recipeBuilder(SFTCore.id("wildcard_pattern"))
            .outputItems(SFTItems.WILDCARD_PATTERN)
            .inputItems(AEItems.BLANK_PATTERN.asItem(), 16)
            .inputItems(plate, Polyethylene, 4)
            .duration(200)
            .EUt(VA[MV])
            .save(provider);

        ASSEMBLER_RECIPES.recipeBuilder(SFTCore.id("wireless_controller_sbr"))
            .outputItems(SFTMachines.WIRELESS_CONTROLLER)
            .inputItems(HULL[IV])
            .inputItems(AEBlocks.INTERFACE.asItem(), 4)
            .inputItems(AEBlocks.PATTERN_PROVIDER.asItem(), 4)
            .inputItems(AEItems.WIRELESS_BOOSTER.asItem(), 64)
            .inputFluids(StyreneButadieneRubber.getFluid(L))
            .duration(1200)
            .EUt(VA[EV])
            .save(provider);

        ASSEMBLER_RECIPES.recipeBuilder(SFTCore.id("wireless_controller_sr"))
            .outputItems(SFTMachines.WIRELESS_CONTROLLER)
            .inputItems(HULL[IV])
            .inputItems(AEBlocks.INTERFACE.asItem(), 4)
            .inputItems(AEBlocks.PATTERN_PROVIDER.asItem(), 4)
            .inputItems(AEItems.WIRELESS_BOOSTER.asItem(), 64)
            .inputFluids(SiliconeRubber.getFluid(2 * L))
            .duration(1200)
            .EUt(VA[EV])
            .save(provider);
    }
}
