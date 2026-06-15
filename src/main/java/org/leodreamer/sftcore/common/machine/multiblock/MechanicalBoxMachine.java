package org.leodreamer.sftcore.common.machine.multiblock;

import org.leodreamer.sftcore.api.kinetics.WorkableKineticMultiblockMachine;
import org.leodreamer.sftcore.common.item.cepattern.CEPatternData;
import org.leodreamer.sftcore.common.item.cepattern.CEPatternLogic;
import org.leodreamer.sftcore.common.machine.multiblock.part.MechanicalPatternHatchPartMachine;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.sync_system.ClassSyncData;
import com.gregtechceu.gtceu.api.sync_system.data_transformers.ValueTransformer;
import com.gregtechceu.gtceu.api.sync_system.data_transformers.gtceu.GTRecipeTransformer;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

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

        private static final ValueTransformer<GTRecipe> MECHANICAL_RECIPE_TRANSFORMER = new MechanicalRecipeTransformer();

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
                var recipe = CEPatternLogic.buildRecipe(level, data, i);
                if (recipe != null) {
                    recipes.add(recipe);
                }
            }
            return recipes.iterator();
        }

        @Override
        public void onMachineLoad() {
            super.onMachineLoad();
            if (lastRecipe != null) {
                var rebuilt = rebuildRecipe(lastRecipe);
                lastRecipe = rebuilt;
                if (rebuilt == null) {
                    lastOriginRecipe = null;
                    resetRecipeLogic();
                }
            }
        }

        @Override
        public void onMachineUnload() {
            lastFailedMatches = null;
            failureReasonMap.clear();
            super.onMachineUnload();
        }

        private @Nullable GTRecipe rebuildRecipe(GTRecipe savedRecipe) {
            var data = CEPatternData.read(savedRecipe.data);
            int slot = savedRecipe.data.getInt(CEPatternLogic.SLOT);
            return rebuildRecipe(data, slot, savedRecipe);
        }

        private @Nullable GTRecipe rebuildRecipe(
            CEPatternData data,
            int slot,
            @Nullable GTRecipe savedRecipe
        ) {
            MechanicalBoxMachine machine;
            try {
                if (!(getMachine() instanceof MechanicalBoxMachine mechanicalBox)) {
                    return null;
                }
                machine = mechanicalBox;
            } catch (IllegalStateException ignored) {
                return null;
            }
            var level = machine.getLevel();
            if (level == null) {
                return null;
            }

            var rebuilt = CEPatternLogic.buildRecipe(level, data, slot);
            if (rebuilt == null) {
                return null;
            }
            if (savedRecipe != null) {
                rebuilt.parallels = savedRecipe.parallels;
                rebuilt.subtickParallels = savedRecipe.subtickParallels;
                rebuilt.batchParallels = savedRecipe.batchParallels;
                rebuilt.ocLevel = savedRecipe.ocLevel;
            }
            return rebuilt;
        }

        static {
            ClassSyncData.getClassData(MechanicalBoxRecipeLogic.class)
                .setCustomTransformerForField("lastRecipe", MECHANICAL_RECIPE_TRANSFORMER);
            ClassSyncData.getClassData(MechanicalBoxRecipeLogic.class)
                .setCustomTransformerForField("lastOriginRecipe", MECHANICAL_RECIPE_TRANSFORMER);
        }
    }

    private static final class MechanicalRecipeTransformer implements ValueTransformer<GTRecipe> {

        private static final GTRecipeTransformer FULL_RECIPE_TRANSFORMER = new GTRecipeTransformer();

        @Override
        public Tag serializeNBT(GTRecipe value, TransformerContext<GTRecipe> context) {
            if (
                context.isClientSync() || !(context.holder() instanceof MechanicalBoxMachine.MechanicalBoxRecipeLogic)
            ) {
                return FULL_RECIPE_TRANSFORMER.serializeNBT(value, context);
            }

            var data = CEPatternData.read(value.data);
            if (data.recipeIds().isEmpty()) {
                return FULL_RECIPE_TRANSFORMER.serializeNBT(value, context);
            }

            var tag = data.write();
            tag.putInt(CEPatternLogic.SLOT, value.data.getInt(CEPatternLogic.SLOT));
            tag.putInt("parallels", value.parallels);
            tag.putInt("subtickParallels", value.subtickParallels);
            tag.putInt("batchParallels", value.batchParallels);
            tag.putInt("ocLevel", value.ocLevel);
            return tag;
        }

        @Override
        public @Nullable GTRecipe deserializeNBT(Tag tag, TransformerContext<GTRecipe> context) {
            if (
                context.isClientSync() ||
                    !(context.holder() instanceof MechanicalBoxMachine.MechanicalBoxRecipeLogic logic)
            ) {
                return FULL_RECIPE_TRANSFORMER.deserializeNBT(tag, context);
            }
            if (!(tag instanceof CompoundTag compound) || compound.isEmpty()) {
                return null;
            }
            var data = CEPatternData.read(compound);
            if (data.recipeIds().isEmpty()) {
                return FULL_RECIPE_TRANSFORMER.deserializeNBT(tag, context);
            }

            int slot = compound.getInt(CEPatternLogic.SLOT);
            var recipe = logic.rebuildRecipe(data, slot, null);
            if (recipe == null) {
                return null;
            }
            recipe.parallels = compound.contains("parallels") ? compound.getInt("parallels") : 1;
            recipe.subtickParallels = compound.contains("subtickParallels") ?
                compound.getInt("subtickParallels") : 1;
            recipe.batchParallels = compound.contains("batchParallels") ?
                compound.getInt("batchParallels") : 1;
            recipe.ocLevel = compound.getInt("ocLevel");
            return recipe;
        }
    }
}
