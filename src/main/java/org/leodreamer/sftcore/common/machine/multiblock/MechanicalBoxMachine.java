package org.leodreamer.sftcore.common.machine.multiblock;

import org.leodreamer.sftcore.api.kinetics.WorkableKineticMultiblockMachine;
import org.leodreamer.sftcore.common.data.recipe.SFTRecipeTypes;
import org.leodreamer.sftcore.common.item.mechanical.MechanicalEncapsulationPatternLogic;
import org.leodreamer.sftcore.common.machine.multiblock.part.MechanicalPatternHatchPartMachine;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.sync_system.ClassSyncData;
import com.gregtechceu.gtceu.api.sync_system.data_transformers.ValueTransformer;
import com.gregtechceu.gtceu.api.sync_system.data_transformers.gtceu.GTRecipeTransformer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

        private static final GTRecipeTransformer FULL_RECIPE_TRANSFORMER = new GTRecipeTransformer();
        private static final ValueTransformer<GTRecipe> MECHANICAL_RECIPE_TRANSFORMER = new MechanicalRecipeTransformer();
        private static final String SAVED_RECIPE_IDS = "mechanical_recipe_ids";
        private static final String SAVED_RECIPE_MULTIPLIERS = "mechanical_recipe_multipliers";
        private static final String SAVED_SLOT = "mechanical_slot";

        @Override
        public Iterator<GTRecipe> searchRecipe() {
            if (!(getMachine() instanceof MechanicalBoxMachine machine)) {
                return List.<GTRecipe>of().iterator();
            }

            var hatch = machine.getPatternHatch();
            if (hatch == null) {
                return List.<GTRecipe>of().iterator();
            }

            var level = machine.getLevel();
            if (level == null) {
                return List.<GTRecipe>of().iterator();
            }

            var recipes = new ArrayList<GTRecipe>();
            var patterns = hatch.getEncodedPatterns();
            for (int i = 0; i < patterns.size(); i++) {
                var recipe = MechanicalEncapsulationPatternLogic.buildRecipe(
                    level,
                    patterns.get(i),
                    SFTRecipeTypes.MECHANICAL_BOX_RECIPES,
                    i
                );
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
            return rebuildRecipe(MechanicalEncapsulationPatternLogic.readRecipeIds(savedRecipe),
                MechanicalEncapsulationPatternLogic.readRecipeMultipliers(savedRecipe),
                savedRecipe.data.getInt(SAVED_SLOT),
                savedRecipe);
        }

        private @Nullable GTRecipe rebuildRecipe(List<ResourceLocation> recipeIds, int slot, @Nullable GTRecipe savedRecipe) {
            return rebuildRecipe(recipeIds, List.of(), slot, savedRecipe);
        }

        private @Nullable GTRecipe rebuildRecipe(
            List<ResourceLocation> recipeIds,
            List<Integer> multipliers,
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

            var steps = MechanicalEncapsulationPatternLogic.resolveSteps(level, recipeIds);
            if (steps.isEmpty()) {
                return null;
            }

            var rebuilt = MechanicalEncapsulationPatternLogic.buildRecipe(
                recipeIds,
                steps,
                multipliers,
                SFTRecipeTypes.MECHANICAL_BOX_RECIPES,
                slot
            );
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

        private static final class MechanicalRecipeTransformer implements ValueTransformer<GTRecipe> {

            @Override
            public Tag serializeNBT(GTRecipe value, TransformerContext<GTRecipe> context) {
                if (context.isClientSync() || !(context.holder() instanceof MechanicalBoxRecipeLogic)) {
                    return FULL_RECIPE_TRANSFORMER.serializeNBT(value, context);
                }

                var recipeIds = MechanicalEncapsulationPatternLogic.readRecipeIds(value);
                if (recipeIds.isEmpty()) {
                    return FULL_RECIPE_TRANSFORMER.serializeNBT(value, context);
                }
                var multipliers = MechanicalEncapsulationPatternLogic.readRecipeMultipliers(value);

                var tag = new CompoundTag();
                var list = new ListTag();
                recipeIds.stream()
                    .map(ResourceLocation::toString)
                    .map(StringTag::valueOf)
                    .forEach(list::add);
                tag.put(SAVED_RECIPE_IDS, list);
                tag.putIntArray(SAVED_RECIPE_MULTIPLIERS, multipliers);
                tag.putInt(SAVED_SLOT, value.data.getInt(SAVED_SLOT));
                tag.putInt("parallels", value.parallels);
                tag.putInt("subtickParallels", value.subtickParallels);
                tag.putInt("batchParallels", value.batchParallels);
                tag.putInt("ocLevel", value.ocLevel);
                return tag;
            }

            @Override
            public @Nullable GTRecipe deserializeNBT(Tag tag, TransformerContext<GTRecipe> context) {
                if (context.isClientSync() || !(context.holder() instanceof MechanicalBoxRecipeLogic logic)) {
                    return FULL_RECIPE_TRANSFORMER.deserializeNBT(tag, context);
                }
                if (!(tag instanceof CompoundTag compound) || compound.isEmpty()) {
                    return null;
                }
                if (!compound.contains(SAVED_RECIPE_IDS)) {
                    return FULL_RECIPE_TRANSFORMER.deserializeNBT(tag, context);
                }

                var idsTag = compound.getList(SAVED_RECIPE_IDS, Tag.TAG_STRING);
                var recipeIds = new ArrayList<ResourceLocation>(idsTag.size());
                for (int i = 0; i < idsTag.size(); i++) {
                    var id = ResourceLocation.tryParse(idsTag.getString(i));
                    if (id != null) {
                        recipeIds.add(id);
                    }
                }
                if (recipeIds.isEmpty()) {
                    return null;
                }
                var multipliers = new ArrayList<Integer>(recipeIds.size());
                var multiplierArray = compound.getIntArray(SAVED_RECIPE_MULTIPLIERS);
                for (int i = 0; i < recipeIds.size(); i++) {
                    multipliers.add(MechanicalEncapsulationPatternLogic.sanitizeMultiplier(
                        i < multiplierArray.length ? multiplierArray[i] : 1
                    ));
                }

                int slot = compound.getInt(SAVED_SLOT);
                var recipe = logic.rebuildRecipe(recipeIds, multipliers, slot, null);
                if (recipe == null) {
                    recipe = MechanicalEncapsulationPatternLogic.buildRecipeStub(
                        recipeIds,
                        multipliers,
                        SFTRecipeTypes.MECHANICAL_BOX_RECIPES,
                        slot
                    );
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
}
