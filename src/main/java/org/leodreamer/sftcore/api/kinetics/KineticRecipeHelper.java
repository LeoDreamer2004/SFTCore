package org.leodreamer.sftcore.api.kinetics;

import org.leodreamer.sftcore.common.machine.multiblock.part.KineticInputPartMachine;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.utils.FormattingUtil;

import java.util.List;
import java.util.Locale;

public final class KineticRecipeHelper {

    private KineticRecipeHelper() {}

    public static List<KineticInputPartMachine> getInputParts(RecipeLogic recipeLogic) {
        if (!(recipeLogic.getMachine() instanceof WorkableMultiblockMachine controller)) {
            return List.of();
        }

        return controller.getParts()
            .stream()
            .filter(KineticInputPartMachine.class::isInstance)
            .map(KineticInputPartMachine.class::cast)
            .filter(part -> part.getIO() == IO.IN)
            .toList();
    }

    public static float getMaxInputRPM(RecipeLogic recipeLogic) {
        return (float) getInputParts(recipeLogic)
            .stream()
            .mapToDouble(part -> Math.abs(part.getShaftSpeed()))
            .max()
            .orElse(0.0);
    }

    public static float getTotalAvailableStress(RecipeLogic recipeLogic) {
        return (float) getInputParts(recipeLogic)
            .stream()
            .mapToDouble(KineticInputPartMachine::getAvailableStress)
            .sum();
    }

    public static String format(float value) {
        if (!Float.isFinite(value)) {
            return "0";
        }

        long rounded = Math.round(value);
        if (Math.abs(value - rounded) < 0.001F) {
            return FormattingUtil.formatNumbers(rounded);
        }
        return String.format(Locale.ROOT, "%.2f", value);
    }
}
