package org.leodreamer.sftcore.common.machine.multiblock;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.api.kinetics.WorkableKineticMultiblockMachine;
import org.leodreamer.sftcore.common.item.cepattern.CEPatternData;
import org.leodreamer.sftcore.common.machine.multiblock.part.MechanicalPatternHatchPartMachine;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.machine.trait.recipe.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeSerializer;
import com.gregtechceu.gtceu.api.sync_system.ClassSyncData;
import com.gregtechceu.gtceu.api.sync_system.data_transformers.ValueTransformer;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
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
            failureReasonMap.clear();
            super.onMachineUnload();
        }

        private static ResourceLocation recipeId(int slot, CEPatternData data) {
            return SFTCore.id("%s/%s".formatted(slot, Integer.toUnsignedString(data.hashCode(), 16)));
        }

        static {
            ClassSyncData.getClassData(MechanicalBoxRecipeLogic.class)
                .setCustomTransformerForField("lastRecipe", MECHANICAL_RECIPE_TRANSFORMER);
            ClassSyncData.getClassData(MechanicalBoxRecipeLogic.class)
                .setCustomTransformerForField("lastOriginRecipe", MECHANICAL_RECIPE_TRANSFORMER);
        }
    }

    private static final class MechanicalRecipeTransformer implements ValueTransformer<GTRecipe> {

        @Override
        public Tag serializeNBT(GTRecipe value, TransformerContext<GTRecipe> context) {
            var tag = new CompoundTag();
            tag.putString("id", value.id.toString());
            tag.put(
                "recipe", GTRecipeSerializer.CODEC.encodeStart(NbtOps.INSTANCE, value).result()
                    .orElse(new CompoundTag())
            );
            tag.putInt("parallels", value.parallels);
            tag.putInt("subtickParallels", value.subtickParallels);
            tag.putInt("batchParallels", value.batchParallels);
            tag.putInt("ocLevel", value.ocLevel);
            return tag;
        }

        @Override
        public @Nullable GTRecipe deserializeNBT(Tag tag, TransformerContext<GTRecipe> context) {
            if (!(tag instanceof CompoundTag compound) || compound.isEmpty()) {
                return null;
            }

            var recipe = GTRecipeSerializer.CODEC.parse(NbtOps.INSTANCE, compound.get("recipe")).result().orElse(null);
            if (recipe == null) {
                return null;
            }
            var id = ResourceLocation.tryParse(compound.getString("id"));
            if (id == null) {
                return null;
            }
            recipe.id = id;
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
