package org.leodreamer.sftcore.common.machine.multiblock;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.api.kinetics.WorkableKineticMultiblockMachine;
import org.leodreamer.sftcore.common.item.cepattern.CEPatternData;
import org.leodreamer.sftcore.common.machine.multiblock.part.MechanicalPatternHatchPartMachine;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.machine.trait.recipe.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MechanicalBoxMachine extends WorkableKineticMultiblockMachine {

    public MechanicalBoxMachine(BlockEntityCreationInfo info) {
        super(info, new MechanicalBoxRecipeLogic());
    }

    public void onPatternsChanged() {
        getRecipeLogic().markLastRecipeDirty();
        getRecipeLogic().updateTickSubscription();
    }

    public @Nullable MechanicalPatternHatchPartMachine getPatternHatch() {
        var hatches = getParts()
            .stream()
            .filter(MechanicalPatternHatchPartMachine.class::isInstance)
            .map(MechanicalPatternHatchPartMachine.class::cast)
            .toList();
        return hatches.size() == 1 ? hatches.get(0) : null;
    }

    private static final class MechanicalBoxRecipeLogic extends RecipeLogic {

        @Override
        public Iterator<GTRecipe> searchRecipe() {
            if (!(getMachine() instanceof MechanicalBoxMachine machine)) {
                return Collections.emptyIterator();
            }

            var hatch = machine.getPatternHatch();
            if (hatch == null) {
                return Collections.emptyIterator();
            }

            var level = machine.getLevel();
            if (level == null) {
                return Collections.emptyIterator();
            }

            var recipes = new ArrayList<GTRecipe>();
            var patterns = hatch.getEncodedPatterns();
            for (int i = 0; i < patterns.size(); i++) {
                var data = CEPatternData.read(patterns.get(i).getOrCreateTag());
                var recipe = data.compile(level).toGTRecipe(recipeId(i, data));
                if (recipe != null) {
                    recipes.add(recipe);
                }
            }
            return recipes.iterator();
        }

        @Override
        public void onMachineUnload() {
            lastFailedMatches = null;
            clearFailureReason();
            super.onMachineUnload();
        }

        private static ResourceLocation recipeId(int slot, CEPatternData data) {
            return SFTCore.id("%s/%s".formatted(slot, Integer.toUnsignedString(data.hashCode(), 16)));
        }
    }
}
