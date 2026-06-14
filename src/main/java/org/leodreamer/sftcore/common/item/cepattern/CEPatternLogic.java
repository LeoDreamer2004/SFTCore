package org.leodreamer.sftcore.common.item.cepattern;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.api.recipe.capability.StressRecipeCapability;
import org.leodreamer.sftcore.common.data.recipe.SFTRecipeTypes;
import org.leodreamer.sftcore.common.data.recipe.builder.SFTRecipeBuilder;
import org.leodreamer.sftcore.common.recipe.condition.RPMCondition;

import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.chance.logic.ChanceLogic;
import com.gregtechceu.gtceu.api.recipe.ingredient.SizedIngredient;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import appeng.crafting.pattern.AEProcessingPattern;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class CEPatternLogic {

    public static final int MAX_STEPS = 9;
    public static final int MIN_MULTIPLIER = 1;
    public static final int MAX_MULTIPLIER = 999;
    public static final float REQUIRED_RPM = 64.0F;
    public static final float STRESS_PER_STEP = 8192.0F;
    public static final String SLOT = "ce_slot";

    private CEPatternLogic() {
    }

    public static List<CERecipeStep> resolveSteps(Level level, List<ResourceLocation> recipeIds) {
        if (recipeIds.isEmpty()) {
            return List.of();
        }
        var recipes = new ArrayList<CERecipeStep>(recipeIds.size());
        for (var id : recipeIds) {
            var recipe = getCERecipe(level, id);
            if (recipe.isEmpty()) {
                return List.of();
            }
            recipes.add(recipe.get());
        }
        return recipes;
    }

    public static boolean canEncode(Level level, ResourceLocation id) {
        return getCERecipe(level, id).isPresent();
    }

    public static Optional<CERecipeStep> getCERecipe(Level level, ResourceLocation id) {
        var recipe = level.getRecipeManager().byKey(id);
        return recipe.flatMap(CERecipeStep::fromRecipe);
    }

    public static @Nullable GTRecipe buildRecipe(Level level, CEPatternData data, int slot) {
        var steps = resolveSteps(level, data.recipeIds());
        if (steps.isEmpty()) {
            return null;
        }
        var net = CompiledRecipe.create(steps, data.multipliers());
        if (net.isEmpty()) {
            return null;
        }

        var builder = GTRecipeBuilder.of(
            SFTCore.id("%s/%s".formatted(slot, Integer.toUnsignedString(data.hashCode(), 16))),
            SFTRecipeTypes.MECHANICAL_BOX_RECIPES
        );

        net.itemInputs().values().forEach(ingredient -> builder.input(ItemRecipeCapability.CAP, SizedIngredient.copy(ingredient)));
        net.fluidInputs().values().forEach(ingredient -> builder.input(FluidRecipeCapability.CAP, ingredient.copy()));

        for (var output : net.itemOutputs().values()) {
            builder.outputItems(output.copy());
        }
        for (var output : net.chancedItemOutputs()) {
            builder.chancedOutput(output.stack().copy(), output.chance(), 0);
        }
        if (net.itemOutputChanceLogic() != ChanceLogic.OR) {
            builder.chancedItemOutputLogic(net.itemOutputChanceLogic());
        }
        for (var stack : net.fluidOutputs().values()) {
            builder.outputFluids(stack.copy());
        }

        builder.duration(net.duration());
        builder.addCondition(new RPMCondition(REQUIRED_RPM));
        boolean previousPerTick = builder.perTick;
        builder.perTick(true);
        builder.input(StressRecipeCapability.CAP, STRESS_PER_STEP * net.weightedSteps());
        builder.perTick(previousPerTick);
        builder.data.putFloat(SFTRecipeBuilder.INPUT_RPM, REQUIRED_RPM);
        builder.data.putFloat(SFTRecipeBuilder.INPUT_STRESS, STRESS_PER_STEP * net.weightedSteps());
        builder.data.merge(data.write());
        builder.data.putInt(SLOT, slot);

        return builder.buildRawRecipe();
    }

    public static ItemStack makeEncodedProcessingPattern(
        Level level,
        List<ResourceLocation> recipeIds,
        List<Integer> multipliers
    ) {
        var steps = resolveSteps(level, recipeIds);
        if (steps.isEmpty()) {
            return ItemStack.EMPTY;
        }

        if (recipeIds.size() != multipliers.size()) {
            return ItemStack.EMPTY;
        }

        var net = CompiledRecipe.create(steps, multipliers);
        if (net.isEmpty()) {
            return ItemStack.EMPTY;
        }

        var inputs = net.toPatternInputs();
        var outputs = net.toPatternOutputs();
        if (inputs.length == 0 || outputs.length == 0 || inputs.length > AEProcessingPattern.MAX_INPUT_SLOTS ||
            outputs.length > AEProcessingPattern.MAX_OUTPUT_SLOTS) {
            return ItemStack.EMPTY;
        }

        try {
            return PatternDetailsHelper.encodeProcessingPattern(inputs, outputs);
        } catch (IllegalArgumentException ignored) {
            return ItemStack.EMPTY;
        }
    }

    @Accessors(fluent = true)
    @Getter
    static final class CompiledRecipe {

        private final Map<AEItemKey, SizedIngredient> itemInputs = new LinkedHashMap<>();
        private final Map<AEItemKey, ItemStack> itemOutputs = new LinkedHashMap<>();
        private final List<ChancedItemOutput> chancedItemOutputs = new ArrayList<>();
        private final Map<AEFluidKey, com.gregtechceu.gtceu.api.recipe.ingredient.FluidIngredient> fluidInputs =
            new LinkedHashMap<>();
        private final Map<AEFluidKey, FluidStack> fluidOutputs = new LinkedHashMap<>();
        private ChanceLogic itemOutputChanceLogic = ChanceLogic.OR;
        private int duration;
        private int weightedSteps;

        private CompiledRecipe() {
        }

        static CompiledRecipe create(List<CERecipeStep> recipes, List<Integer> multipliers) {
            var net = new CompiledRecipe();
            if (recipes.size() != multipliers.size()) {
                return net;
            }
            for (int i = 0; i < recipes.size(); i++) {
                var recipe = recipes.get(i);
                int multiplier = multipliers.get(i);
                net.weightedSteps += multiplier;
                for (int repeated = 0; repeated < multiplier; repeated++) {
                    net.duration += Math.max(1, recipe.duration());
                    net.consumeItems(recipe.itemInputs());
                    net.consumeFluids(recipe.fluidInputs());
                    net.produceItems(recipe.itemOutputs());
                    net.produceFluids(recipe.fluidOutputs());
                }
            }
            if (recipes.size() == 1 && recipes.get(0).itemOutputChanceLogic() == ChanceLogic.XOR) {
                net.itemOutputChanceLogic = ChanceLogic.XOR;
            }
            if (net.duration <= 0) {
                net.duration = recipes.size() * 20;
            }
            return net;
        }

        boolean isEmpty() {
            return itemInputs.isEmpty() && itemOutputs.isEmpty() && chancedItemOutputs.isEmpty() &&
                fluidInputs.isEmpty() && fluidOutputs.isEmpty();
        }

        private void consumeItems(List<Ingredient> ingredients) {
            for (var ingredient : ingredients) {
                if (ingredient.isEmpty()) {
                    continue;
                }
                var stacks = ingredient.getItems();
                if (stacks.length == 0 || stacks[0].isEmpty()) {
                    continue;
                }
                int amount = ingredient instanceof SizedIngredient sized ? sized.getAmount() : Math.max(1, stacks[0].getCount());
                var key = AEItemKey.of(stacks[0]);
                if (key == null) {
                    continue;
                }
                int remaining = consumeItemOutput(key, amount);
                if (remaining > 0) {
                    var copied = SizedIngredient.create(ingredient, remaining);
                    itemInputs.merge(key, copied, (oldValue, newValue) -> {
                        oldValue.setAmount(oldValue.getAmount() + newValue.getAmount());
                        return oldValue;
                    });
                }
            }
        }

        private int consumeItemOutput(AEItemKey key, int amount) {
            var output = itemOutputs.get(key);
            if (output == null || output.isEmpty()) {
                return amount;
            }
            int consumed = Math.min(amount, output.getCount());
            output.shrink(consumed);
            if (output.isEmpty()) {
                itemOutputs.remove(key);
            }
            return amount - consumed;
        }

        private void consumeFluids(List<FluidIngredient> ingredients) {
            for (var ingredient : ingredients) {
                var stacks = ingredient.getMatchingFluidStacks();
                if (stacks.isEmpty() || stacks.get(0).isEmpty()) {
                    continue;
                }
                var stack = stacks.get(0).copy();
                stack.setAmount(ingredient.getRequiredAmount());
                var key = AEFluidKey.of(stack);
                if (key == null) {
                    continue;
                }
                int remaining = consumeFluidOutput(key, stack.getAmount());
                if (remaining > 0) {
                    var remainingStack = stack.copy();
                    remainingStack.setAmount(remaining);
                    var input = com.gregtechceu.gtceu.api.recipe.ingredient.FluidIngredient.of(remainingStack);
                    fluidInputs.merge(key, input, (oldValue, newValue) -> {
                        oldValue.setAmount(oldValue.getAmount() + newValue.getAmount());
                        return oldValue;
                    });
                }
            }
        }

        private int consumeFluidOutput(AEFluidKey key, int amount) {
            var output = fluidOutputs.get(key);
            if (output == null || output.isEmpty()) {
                return amount;
            }
            int consumed = Math.min(amount, output.getAmount());
            output.shrink(consumed);
            if (output.isEmpty()) {
                fluidOutputs.remove(key);
            }
            return amount - consumed;
        }

        private void produceItems(List<ProcessingOutput> outputs) {
            for (var output : outputs) {
                var stack = output.getStack();
                if (stack.isEmpty()) {
                    continue;
                }
                float chance = output.getChance();
                if (chance >= 0.9999F) {
                    var copied = stack.copy();
                    var key = AEItemKey.of(copied);
                    if (key == null) {
                        continue;
                    }
                    itemOutputs.merge(key, copied, (oldValue, newValue) -> {
                        oldValue.grow(newValue.getCount());
                        return oldValue;
                    });
                } else if (chance > 0.0F) {
                    int gtChance = Math.max(1, Math.round(chance * ChanceLogic.getMaxChancedValue()));
                    chancedItemOutputs.add(new ChancedItemOutput(stack.copy(), gtChance));
                }
            }
        }

        private void produceFluids(List<FluidStack> outputs) {
            for (var stack : outputs) {
                if (stack.isEmpty()) {
                    continue;
                }
                var copied = stack.copy();
                var key = AEFluidKey.of(copied);
                if (key == null) {
                    continue;
                }
                fluidOutputs.merge(key, copied, (oldValue, newValue) -> {
                    oldValue.grow(newValue.getAmount());
                    return oldValue;
                });
            }
        }

        GenericStack[] toPatternInputs() {
            var inputs = new ArrayList<GenericStack>();
            itemInputs.values().stream()
                .map(CompiledRecipe::toGenericStack)
                .filter(Objects::nonNull)
                .forEach(inputs::add);
            fluidInputs.values().stream()
                .map(CompiledRecipe::toGenericStack)
                .filter(Objects::nonNull)
                .forEach(inputs::add);
            return inputs.toArray(GenericStack[]::new);
        }

        GenericStack[] toPatternOutputs() {
            var outputs = new ArrayList<GenericStack>();
            itemOutputs.values().stream()
                .map(GenericStack::fromItemStack)
                .filter(Objects::nonNull)
                .forEach(outputs::add);
            chancedItemOutputs.stream()
                .map(ChancedItemOutput::stack)
                .map(GenericStack::fromItemStack)
                .filter(Objects::nonNull)
                .forEach(outputs::add);
            fluidOutputs.values().stream()
                .map(GenericStack::fromFluidStack)
                .filter(Objects::nonNull)
                .forEach(outputs::add);
            return outputs.toArray(GenericStack[]::new);
        }

        private static @Nullable GenericStack toGenericStack(SizedIngredient ingredient) {
            var stacks = ingredient.getItems();
            if (stacks.length == 0 || stacks[0].isEmpty() || ingredient.getAmount() <= 0) {
                return null;
            }
            var stack = stacks[0].copy();
            stack.setCount(ingredient.getAmount());
            return GenericStack.fromItemStack(stack);
        }

        private static @Nullable GenericStack toGenericStack(
            com.gregtechceu.gtceu.api.recipe.ingredient.FluidIngredient ingredient
        ) {
            var stacks = ingredient.getStacks();
            if (stacks.length == 0 || stacks[0].isEmpty()) {
                return null;
            }
            return GenericStack.fromFluidStack(stacks[0]);
        }
    }

    record ChancedItemOutput(ItemStack stack, int chance) {
    }
}
