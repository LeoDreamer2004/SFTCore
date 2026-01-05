package org.leodreamer.sftcore.util;

import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.multiblock.part.MultiblockPartMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.common.machine.multiblock.part.FluidHatchPartMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.part.ItemBusPartMachine;

import com.hepdd.gtmthings.common.block.machine.multiblock.part.HugeBusPartMachine;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class GTMachineUtils {

    public enum GuessResult {

        EXACT, // the machine has this recipe type, and exactly using it
        OK, // the machine has this recipe type, while not sure if using it
        MISMATCH, // the machine has this recipe type, but not using it
        NULL; // the machine does not have this recipe type

        public boolean ok() {
            return this == EXACT || this == OK;
        }
    }

    public static boolean supportRecipe(MachineDefinition definition, GTRecipeType recipeType) {
        for (var type : definition.getRecipeTypes()) {
            if (recipeType == type) return true;
        }
        return false;
    }

    public static GuessResult guessRecipe(MetaMachine machine, GTRecipeType recipeType) {
        for (var type : machine.getDefinition().getRecipeTypes()) {
            if (type != recipeType) continue;

            // find the recipe
            if (machine instanceof IRecipeLogicMachine rlMachine) {
                return rlMachine.getRecipeType() == recipeType ?
                    GuessResult.EXACT : GuessResult.MISMATCH;
            }
            return GuessResult.OK;
        }
        return GuessResult.NULL;
    }

    @Nullable
    public static IMultiController tryGetController(MetaMachine machine) {
        if (machine instanceof MultiblockPartMachine partMachine && partMachine.isFormed()) {
            return partMachine.getControllers().first();
        }
        return null;
    }

    public static boolean isIngredientIOPort(MetaMachine machine) {
        return machine instanceof ItemBusPartMachine || machine instanceof FluidHatchPartMachine ||
            machine instanceof HugeBusPartMachine;
    }

    public static MetaMachine thisOrController(MetaMachine machine, Predicate<MultiblockPartMachine> filter) {
        if (
            machine instanceof MultiblockPartMachine partMachine && partMachine.isFormed() && filter.test(partMachine)
        ) {
            return partMachine.getControllers().first().self();
        }
        return machine;
    }

    public static MetaMachine thisOrController(MetaMachine machine) {
        return thisOrController(machine, GTMachineUtils::isIngredientIOPort);
    }
}
