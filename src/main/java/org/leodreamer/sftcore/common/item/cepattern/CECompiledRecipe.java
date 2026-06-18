package org.leodreamer.sftcore.common.item.cepattern;

import org.leodreamer.sftcore.common.data.recipe.SFTRecipeTypes;
import org.leodreamer.sftcore.common.data.recipe.builder.SFTRecipeBuilder;

import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.chance.logic.ChanceLogic;
import com.gregtechceu.gtceu.api.recipe.ingredient.FluidIngredient;
import com.gregtechceu.gtceu.api.recipe.ingredient.SizedIngredient;

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
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Compiled recipe from {@link CEPatternData} for GT & AE integration.
 * <p>
 * Note: all the chance outputs are using {@link ChanceLogic#OR} here,
 * simulating the {@link ChanceLogic#XOR} for recipes like assembly.
 */
@Accessors(fluent = true)
@Getter
public final class CECompiledRecipe {

    public static final float REQUIRED_RPM = 64.0F;
    public static final float STRESS_PER_STEP = 2048.0F;

    private final Map<AEItemKey, SizedIngredient> itemInputs = new LinkedHashMap<>();
    private final Map<AEItemKey, ItemStack> itemOutputs = new LinkedHashMap<>();
    private final List<ChancedItemOutput> chancedItemOutputs = new ArrayList<>();
    private final Map<AEFluidKey, FluidIngredient> fluidInputs = new LinkedHashMap<>();
    private final Map<AEFluidKey, FluidStack> fluidOutputs = new LinkedHashMap<>();
    private int duration;
    private int weightedSteps;

    public CECompiledRecipe(Level level, CEPatternData data) {
        for (int i = 0; i < data.size(); i++) {
            var recipe = CERecipeStep.fromId(data.recipeIds().get(i), level).orElse(null);
            if (recipe == null) {
                continue;
            }

            int multiplier = data.multipliers().get(i);
            weightedSteps += multiplier;
            duration += Math.max(1, recipe.duration()) * multiplier;
            recipe.itemInputs().forEach(ingredient -> consumeItem(ingredient, multiplier));
            recipe.fluidInputs().forEach(ingredient -> consumeFluid(ingredient, multiplier));
            recipe.itemOutputs().forEach(output -> produceItem(output, multiplier));
            recipe.fluidOutputs().forEach(stack -> produceFluid(stack, multiplier));
        }
    }

    public boolean isEmpty() {
        return itemInputs.isEmpty() && itemOutputs.isEmpty() && chancedItemOutputs.isEmpty() &&
            fluidInputs.isEmpty() && fluidOutputs.isEmpty();
    }

    // --- IO ---

    private void consumeItem(Ingredient ingredient, int multiplier) {
        if (ingredient.isEmpty()) {
            return;
        }
        var stacks = ingredient.getItems();
        if (stacks.length == 0 || stacks[0].isEmpty()) {
            return;
        }
        int amount = ingredient instanceof SizedIngredient sized ? sized.getAmount() :
            Math.max(1, stacks[0].getCount());
        amount *= multiplier;
        var key = AEItemKey.of(stacks[0]);
        if (key == null) {
            return;
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

    private void consumeFluid(com.simibubi.create.foundation.fluid.FluidIngredient ingredient, int multiplier) {
        var stacks = ingredient.getMatchingFluidStacks();
        if (stacks.isEmpty() || stacks.get(0).isEmpty()) {
            return;
        }
        var stack = stacks.get(0).copy();
        stack.setAmount(ingredient.getRequiredAmount() * multiplier);
        var key = AEFluidKey.of(stack);
        if (key == null) {
            return;
        }
        int remaining = consumeFluidOutput(key, stack.getAmount());
        if (remaining > 0) {
            var remainingStack = stack.copy();
            remainingStack.setAmount(remaining);
            var input = FluidIngredient.of(remainingStack);
            fluidInputs.merge(key, input, (oldValue, newValue) -> {
                oldValue.setAmount(oldValue.getAmount() + newValue.getAmount());
                return oldValue;
            });
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

    private void produceItem(ProcessingOutput output, int multiplier) {
        var stack = output.getStack();
        if (stack.isEmpty()) {
            return;
        }
        float chance = output.getChance();
        if (chance >= 0.9999F) {
            var copied = stack.copy();
            copied.setCount(copied.getCount() * multiplier);
            var key = AEItemKey.of(copied);
            if (key == null) {
                return;
            }
            itemOutputs.merge(key, copied, (oldValue, newValue) -> {
                oldValue.grow(newValue.getCount());
                return oldValue;
            });
        } else if (chance > 0.0F) {
            int gtChance = Math.max(1, Math.round(chance * ChanceLogic.getMaxChancedValue()));
            var copied = stack.copy();
            copied.setCount(copied.getCount() * multiplier);
            chancedItemOutputs.add(new ChancedItemOutput(copied, gtChance));
        }
    }

    private void produceFluid(FluidStack stack, int multiplier) {
        if (stack.isEmpty()) {
            return;
        }
        var copied = stack.copy();
        copied.setAmount(copied.getAmount() * multiplier);
        var key = AEFluidKey.of(copied);
        if (key == null) {
            return;
        }
        fluidOutputs.merge(key, copied, (oldValue, newValue) -> {
            oldValue.grow(newValue.getAmount());
            return oldValue;
        });
    }

    // --- AE Integration ---

    public ItemStack makeAEProcessingPattern() {
        if (isEmpty()) {
            return ItemStack.EMPTY;
        }

        var inputs = toPatternInputs();
        var outputs = toPatternOutputs();
        if (
            inputs.length == 0 || outputs.length == 0 || inputs.length > AEProcessingPattern.MAX_INPUT_SLOTS ||
                outputs.length > AEProcessingPattern.MAX_OUTPUT_SLOTS
        ) {
            return ItemStack.EMPTY;
        }

        try {
            return PatternDetailsHelper.encodeProcessingPattern(inputs, outputs);
        } catch (IllegalArgumentException ignored) {
            return ItemStack.EMPTY;
        }
    }

    private GenericStack[] toPatternInputs() {
        var inputs = new ArrayList<GenericStack>();
        itemInputs.values().stream()
            .map(CECompiledRecipe::toGenericStack)
            .filter(Objects::nonNull)
            .forEach(inputs::add);
        fluidInputs.values().stream()
            .map(CECompiledRecipe::toGenericStack)
            .filter(Objects::nonNull)
            .forEach(inputs::add);
        return inputs.toArray(GenericStack[]::new);
    }

    private GenericStack[] toPatternOutputs() {
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

    // --- GT Integration ---

    public @Nullable GTRecipe toGTRecipe(ResourceLocation id) {
        if (isEmpty()) {
            return null;
        }

        var builder = SFTRecipeBuilder.of(id, SFTRecipeTypes.MECHANICAL_BOX_RECIPES);

        for (var input : itemInputs.values()) {
            builder.input(ItemRecipeCapability.CAP, SizedIngredient.copy(input));
        }
        for (var input : fluidInputs.values()) {
            builder.input(FluidRecipeCapability.CAP, input.copy());
        }
        for (var output : itemOutputs.values()) {
            builder.outputItems(output.copy());
        }
        for (var output : chancedItemOutputs) {
            builder.chancedOutput(output.stack().copy(), output.chance(), 0);
        }
        for (var stack : fluidOutputs.values()) {
            builder.outputFluids(stack.copy());
        }

        builder.duration(duration)
            .inputRPM(REQUIRED_RPM)
            .inputStress(STRESS_PER_STEP * weightedSteps);

        return builder.buildRawRecipe();
    }

    record ChancedItemOutput(ItemStack stack, int chance) {}
}
