package org.leodreamer.sftcore.common.data.recipe;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.CoilWorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.OverclockingLogic;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import org.jetbrains.annotations.NotNull;
import org.leodreamer.sftcore.common.machine.multiblock.CommonFactoryMachine;

import static com.gregtechceu.gtceu.api.recipe.OverclockingLogic.STD_VOLTAGE_FACTOR;
import static com.gregtechceu.gtceu.api.recipe.OverclockingLogic.create;
import static com.gregtechceu.gtceu.common.data.GTRecipeModifiers.ELECTRIC_OVERCLOCK;

public final class SFTRecipeModifiers {

    public static final OverclockingLogic HALF_PERFECT_OVERCLOCK = create(0.33, 4.0, false);
    public static final RecipeModifier OC_HALF_PERFECT = ELECTRIC_OVERCLOCK.apply(HALF_PERFECT_OVERCLOCK);

    public static double HALF_DURATION_FACTOR = 0.33;

    public static final OverclockingLogic HALF_PERFECT_OVERCLOCK_SUBTICK = create(HALF_DURATION_FACTOR, STD_VOLTAGE_FACTOR, true);
    public static final RecipeModifier OC_HALF_PERFECT_SUBTICK =
            ELECTRIC_OVERCLOCK.apply(HALF_PERFECT_OVERCLOCK_SUBTICK);

    public record SimpleMultiplierModifier(double eutMultiplier,
                                           double durationMultiplier) implements RecipeModifier {
        @Override
        public @NotNull ModifierFunction getModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
            return ModifierFunction.builder().eutMultiplier(eutMultiplier).durationMultiplier(durationMultiplier).build();
        }
    }

    public static final double GCYM_EUT_MULTIPLIER = 0.8;
    public static final double GCYM_DURATION_MULTIPLIER = 0.6;
    public static final RecipeModifier GCYM_MACHINE_REDUCE =
            new SFTRecipeModifiers.SimpleMultiplierModifier(GCYM_EUT_MULTIPLIER, GCYM_DURATION_MULTIPLIER);

    public record CoilReductionModifier(int coilTempLevel, double eutMultiplier,
                                        double durationMultiplier) implements RecipeModifier {
        @Override
        public @NotNull ModifierFunction getModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
            if (!(machine instanceof CoilWorkableElectricMultiblockMachine coilMachine))
                return RecipeModifier.nullWrongType(CoilWorkableElectricMultiblockMachine.class, machine);
            var temp = coilMachine.getCoilType().getCoilTemperature();
            var tier = Math.floor((float) temp / coilTempLevel);

            return ModifierFunction.builder()
                    .eutMultiplier(Math.pow(eutMultiplier, tier))
                    .durationMultiplier(Math.pow(durationMultiplier, tier))
                    .build();
        }
    }

    public static final int MEGA_COIL_TEMP_LEVEL = 1200;
    public static final double MEGA_COIL_EUT_MULTIPLIER = 0.9;
    public static final double MEGA_COIL_DURATION_MULTIPLIER = 0.75;
    public static final RecipeModifier MEGA_COIL_MACHINE_REDUCE =
            new CoilReductionModifier(MEGA_COIL_TEMP_LEVEL, MEGA_COIL_EUT_MULTIPLIER, MEGA_COIL_DURATION_MULTIPLIER);

    @NotNull
    public static ModifierFunction largeCrackerOverlock(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (!(machine instanceof CoilWorkableElectricMultiblockMachine coilMachine)) {
            return RecipeModifier.nullWrongType(CoilWorkableElectricMultiblockMachine.class, machine);
        }

        return ModifierFunction.builder()
                .eutMultiplier(1.0 - coilMachine.getCoilTier() * 0.1)
                .durationMultiplier(1.0 - coilMachine.getCoilTier() * 0.1)
                .build();
    }

    @NotNull
    public static ModifierFunction commonFactoryParallel(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (!(machine instanceof CommonFactoryMachine cMachine)) {
            return RecipeModifier.nullWrongType(CommonFactoryMachine.class, machine);
        }
        if (!cMachine.isVoltageValid()) {
            return ModifierFunction.NULL;
        }

        int maxParallels = cMachine.getMaxParallels();
        int parallels = ParallelLogic.getParallelAmount(machine, recipe, maxParallels);

        if (parallels == 1) return ModifierFunction.IDENTITY;

        return ModifierFunction.builder()
                .modifyAllContents(ContentModifier.multiplier(parallels))
                .eutMultiplier(parallels)
                .parallels(parallels)
                .build();
    }

    @NotNull
    public static ModifierFunction gasCollectorParallel(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        int parallels = ParallelLogic.getParallelAmount(machine, recipe, Integer.MAX_VALUE);
        return ModifierFunction.builder()
                .modifyAllContents(ContentModifier.multiplier(parallels))
                .eutMultiplier(parallels)
                .parallels(parallels)
                .build();
    }
}
