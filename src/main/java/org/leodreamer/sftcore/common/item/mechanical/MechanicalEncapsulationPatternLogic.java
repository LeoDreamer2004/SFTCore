package org.leodreamer.sftcore.common.item.mechanical;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.api.recipe.capability.StressRecipeCapability;
import org.leodreamer.sftcore.common.data.SFTItems;
import org.leodreamer.sftcore.common.data.recipe.builder.SFTRecipeBuilder;
import org.leodreamer.sftcore.common.recipe.condition.RPMCondition;

import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.chance.logic.ChanceLogic;
import com.gregtechceu.gtceu.api.recipe.ingredient.SizedIngredient;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import appeng.core.definitions.AEItems;
import appeng.crafting.pattern.AEProcessingPattern;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@DataGenScanned
public final class MechanicalEncapsulationPatternLogic {

    public static final int MAX_STEPS = 9;
    public static final int MIN_MULTIPLIER = 1;
    public static final int MAX_MULTIPLIER = 999;
    public static final float REQUIRED_RPM = 64.0F;
    public static final float STRESS_PER_STEP = 8192.0F;

    @RegisterLanguage("Blank Pattern")
    public static final String TOOLTIP_BLANK = "item.sftcore.mechanical_encapsulation_pattern.tooltip.blank";

    @RegisterLanguage("Contains %s Create recipe steps")
    public static final String TOOLTIP_STEPS = "item.sftcore.mechanical_encapsulation_pattern.tooltip.steps";

    private static final String RECIPES = "mechanical_recipes";
    private static final String MULTIPLIERS = "mechanical_multipliers";
    private static final String RECIPE_ID_PREFIX = "mechanical_recipe_";
    private static final String RECIPE_MULTIPLIER_PREFIX = "mechanical_multiplier_";

    private MechanicalEncapsulationPatternLogic() {
    }

    public static List<ResourceLocation> readRecipeIds(ItemStack stack) {
        if (!stack.hasTag()) {
            return List.of();
        }
        var list = stack.getOrCreateTag().getList(RECIPES, Tag.TAG_STRING);
        if (list.isEmpty()) {
            return List.of();
        }

        var ids = new ArrayList<ResourceLocation>(Math.min(list.size(), MAX_STEPS));
        for (int i = 0; i < list.size() && ids.size() < MAX_STEPS; i++) {
            var id = ResourceLocation.tryParse(list.getString(i));
            if (id != null) {
                ids.add(id);
            }
        }
        return ids;
    }

    public static ItemStack writeRecipeIds(
        ItemStack stack,
        List<ResourceLocation> recipeIds,
        List<Integer> multipliers
    ) {
        var result = stack.copy();
        result.setCount(1);
        var tag = result.getOrCreateTag();
        var list = new ListTag();
        recipeIds.stream()
            .limit(MAX_STEPS)
            .map(ResourceLocation::toString)
            .map(StringTag::valueOf)
            .forEach(list::add);

        if (list.isEmpty()) {
            tag.remove(RECIPES);
            tag.remove(MULTIPLIERS);
        } else {
            tag.put(RECIPES, list);
            tag.putIntArray(MULTIPLIERS, normalizeMultipliers(multipliers, recipeIds.size()));
        }
        if (tag.isEmpty()) {
            result.setTag(null);
        }
        return result;
    }

    public static List<Integer> readRecipeMultipliers(ItemStack stack) {
        return readMultipliers(stack.hasTag() ? stack.getOrCreateTag() : null, readRecipeIds(stack).size());
    }

    public static int sanitizeMultiplier(int multiplier) {
        return Math.max(MIN_MULTIPLIER, Math.min(MAX_MULTIPLIER, multiplier));
    }

    public static boolean isBlankAePattern(ItemStack stack) {
        return AEItems.BLANK_PATTERN.isSameAs(stack);
    }

    public static boolean isEncoded(ItemStack stack) {
        if (readRecipeIds(stack).isEmpty()) {
            return false;
        }
        return stack.is(SFTItems.MECHANICAL_ENCAPSULATION_PATTERN.asItem()) ||
            AEItems.PROCESSING_PATTERN.isSameAs(stack);
    }

    public static List<MechanicalRecipeStep> resolveSteps(Level level, List<ResourceLocation> recipeIds) {
        if (recipeIds.isEmpty()) {
            return List.of();
        }
        var recipes = new ArrayList<MechanicalRecipeStep>(recipeIds.size());
        for (var id : recipeIds) {
            var recipe = getMechanicalRecipe(level, id);
            if (recipe.isEmpty()) {
                return List.of();
            }
            recipes.add(recipe.get());
        }
        return recipes;
    }

    public static boolean canEncode(Level level, ResourceLocation id) {
        return getMechanicalRecipe(level, id).isPresent();
    }

    public static boolean canEncode(Recipe<?> recipe) {
        return MechanicalRecipeStep.fromRecipe(recipe).isPresent();
    }

    public static Optional<MechanicalRecipeStep> getMechanicalRecipe(Level level, ResourceLocation id) {
        var recipe = level.getRecipeManager().byKey(id);
        return recipe.flatMap(MechanicalRecipeStep::fromRecipe);
    }

    public static @Nullable GTRecipe buildRecipe(Level level, ItemStack stack, GTRecipeType recipeType, int slot) {
        var recipeIds = readRecipeIds(stack);
        var steps = resolveSteps(level, recipeIds);
        if (steps.isEmpty()) {
            return null;
        }
        return buildRecipe(recipeIds, steps, readRecipeMultipliers(stack), recipeType, slot);
    }

    public static @Nullable GTRecipe buildRecipe(
        List<ResourceLocation> recipeIds,
        List<MechanicalRecipeStep> steps,
        List<Integer> multipliers,
        GTRecipeType recipeType,
        int slot
    ) {
        if (recipeIds.isEmpty() || steps.isEmpty() || recipeIds.size() != steps.size()) {
            return null;
        }

        var normalizedMultipliers = normalizeMultipliers(multipliers, recipeIds.size());
        var net = NetRecipe.create(steps, normalizedMultipliers);
        if (net.isEmpty()) {
            return null;
        }
        int weightedSteps = weightedSteps(normalizedMultipliers);

        var builder = GTRecipeBuilder.of(
            SFTCore.id("%s/%s".formatted(slot, Integer.toUnsignedString(recipeHash(recipeIds, normalizedMultipliers), 16))),
            recipeType
        );

        net.itemInputs.values().forEach(ingredient -> builder.input(ItemRecipeCapability.CAP, SizedIngredient.copy(ingredient)));
        net.fluidInputs.values().forEach(ingredient -> builder.input(FluidRecipeCapability.CAP, ingredient.copy()));

        for (var stack : net.itemOutputs.values()) {
            builder.outputItems(stack.copy());
        }
        for (var output : net.chancedItemOutputs) {
            builder.chancedOutput(output.stack().copy(), output.chance(), 0);
        }
        if (net.itemOutputChanceLogic != ChanceLogic.OR) {
            builder.chancedItemOutputLogic(net.itemOutputChanceLogic);
        }
        for (var stack : net.fluidOutputs.values()) {
            builder.outputFluids(stack.copy());
        }

        builder.duration(net.duration);
        builder.addCondition(new RPMCondition(REQUIRED_RPM));
        boolean previousPerTick = builder.perTick;
        builder.perTick(true);
        builder.input(
            StressRecipeCapability.CAP,
            STRESS_PER_STEP * weightedSteps
        );
        builder.perTick(previousPerTick);
        builder.data.putFloat(SFTRecipeBuilder.INPUT_RPM, REQUIRED_RPM);
        builder.data.putFloat(SFTRecipeBuilder.INPUT_STRESS, STRESS_PER_STEP * weightedSteps);
        builder.data.putInt("mechanical_steps", steps.size());
        builder.data.putInt("mechanical_weighted_steps", weightedSteps);
        builder.data.putInt("mechanical_slot", slot);
        for (int i = 0; i < recipeIds.size(); i++) {
            builder.data.putString(RECIPE_ID_PREFIX + i, recipeIds.get(i).toString());
            builder.data.putInt(RECIPE_MULTIPLIER_PREFIX + i, normalizedMultipliers[i]);
        }

        return builder.buildRawRecipe();
    }

    public static GTRecipe buildRecipeStub(
        List<ResourceLocation> recipeIds,
        List<Integer> multipliers,
        GTRecipeType recipeType,
        int slot
    ) {
        var normalizedMultipliers = normalizeMultipliers(multipliers, recipeIds.size());
        int weightedSteps = weightedSteps(normalizedMultipliers);
        var builder = GTRecipeBuilder.of(
            SFTCore.id("%s/%s".formatted(slot, Integer.toUnsignedString(recipeHash(recipeIds, normalizedMultipliers), 16))),
            recipeType
        );
        builder.duration(1);
        builder.data.putFloat(SFTRecipeBuilder.INPUT_RPM, REQUIRED_RPM);
        builder.data.putFloat(SFTRecipeBuilder.INPUT_STRESS, STRESS_PER_STEP * weightedSteps);
        builder.data.putInt("mechanical_steps", recipeIds.size());
        builder.data.putInt("mechanical_weighted_steps", weightedSteps);
        builder.data.putInt("mechanical_slot", slot);
        for (int i = 0; i < recipeIds.size(); i++) {
            builder.data.putString(RECIPE_ID_PREFIX + i, recipeIds.get(i).toString());
            builder.data.putInt(RECIPE_MULTIPLIER_PREFIX + i, normalizedMultipliers[i]);
        }
        return builder.buildRawRecipe();
    }

    public static List<ResourceLocation> readRecipeIds(GTRecipe recipe) {
        int steps = recipe.data.getInt("mechanical_steps");
        if (steps <= 0) {
            return List.of();
        }

        var ids = new ArrayList<ResourceLocation>(Math.min(steps, MAX_STEPS));
        for (int i = 0; i < steps && ids.size() < MAX_STEPS; i++) {
            var id = ResourceLocation.tryParse(recipe.data.getString(RECIPE_ID_PREFIX + i));
            if (id == null) {
                return List.of();
            }
            ids.add(id);
        }
        return ids;
    }

    public static List<Integer> readRecipeMultipliers(GTRecipe recipe) {
        int steps = recipe.data.getInt("mechanical_steps");
        if (steps <= 0) {
            return List.of();
        }
        var multipliers = new ArrayList<Integer>(Math.min(steps, MAX_STEPS));
        for (int i = 0; i < steps && multipliers.size() < MAX_STEPS; i++) {
            multipliers.add(sanitizeMultiplier(
                recipe.data.contains(RECIPE_MULTIPLIER_PREFIX + i) ?
                    recipe.data.getInt(RECIPE_MULTIPLIER_PREFIX + i) : 1
            ));
        }
        return multipliers;
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

        var normalizedMultipliers = normalizeMultipliers(multipliers, recipeIds.size());
        var net = NetRecipe.create(steps, normalizedMultipliers);
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
            return writeRecipeIds(PatternDetailsHelper.encodeProcessingPattern(inputs, outputs), recipeIds, multipliers);
        } catch (IllegalArgumentException ignored) {
            return ItemStack.EMPTY;
        }
    }

    public static List<Component> getTooltip(ItemStack stack, @Nullable Level level) {
        var recipeIds = readRecipeIds(stack);
        if (recipeIds.isEmpty()) {
            return List.of(Component.translatable(TOOLTIP_BLANK)
                .withStyle(ChatFormatting.GRAY));
        }

        var tooltips = new ArrayList<Component>();
        tooltips.add(Component.translatable(
            TOOLTIP_STEPS,
            recipeIds.size()
        ).withStyle(ChatFormatting.GRAY));

        if (level != null) {
            var steps = resolveSteps(level, recipeIds);
            if (!steps.isEmpty()) {
                tooltips.add(Component.literal(describeNetRecipe(steps, readRecipeMultipliers(stack))).withStyle(ChatFormatting.DARK_GRAY));
            }
        }
        return tooltips;
    }

    public static String describeStep(MechanicalRecipeStep recipe) {
        var inputs = new ArrayList<String>();
        recipe.itemInputs().stream()
            .filter(ingredient -> !ingredient.isEmpty())
            .map(MechanicalEncapsulationPatternLogic::describeIngredient)
            .forEach(inputs::add);
        recipe.fluidInputs().stream()
            .filter(ingredient -> !ingredient.getMatchingFluidStacks().isEmpty())
            .map(MechanicalEncapsulationPatternLogic::describeFluidIngredient)
            .forEach(inputs::add);

        var outputs = new ArrayList<String>();
        recipe.itemOutputs().stream()
            .filter(output -> !output.getStack().isEmpty())
            .map(output -> output.getChance() >= 1.0F ? describeStack(output.getStack()) :
                "%s(%s%%)".formatted(describeStack(output.getStack()), trimPercent(output.getChance())))
            .forEach(outputs::add);
        recipe.fluidOutputs().stream()
            .filter(stack -> !stack.isEmpty())
            .map(MechanicalEncapsulationPatternLogic::describeFluidStack)
            .forEach(outputs::add);
        return String.join("+", inputs) + " -> " + String.join("+", outputs);
    }

    public static String describeNetRecipe(List<MechanicalRecipeStep> recipes, List<Integer> multipliers) {
        var net = NetRecipe.create(recipes, normalizeMultipliers(multipliers, recipes.size()));
        if (net.isEmpty()) {
            return "";
        }
        var in = new ArrayList<String>();
        net.itemInputs.values().stream().map(MechanicalEncapsulationPatternLogic::describeIngredient).forEach(in::add);
        net.fluidInputs.values().stream().map(MechanicalEncapsulationPatternLogic::describeGTFluidIngredient).forEach(in::add);

        var out = new ArrayList<String>();
        net.itemOutputs.values().stream().map(MechanicalEncapsulationPatternLogic::describeStack).forEach(out::add);
        net.chancedItemOutputs.stream().map(c -> "%s(%s%%)".formatted(
            describeStack(c.stack()),
            trimGTChance(c.chance())
        )).forEach(out::add);
        net.fluidOutputs.values().stream().map(MechanicalEncapsulationPatternLogic::describeFluidStack).forEach(out::add);
        return String.join("+", in) + " -> " + String.join("+", out);
    }

    public static ItemStack getMachineIcon(MechanicalRecipeStep recipe) {
        return recipe.machineIcon().copy();
    }

    public static Component getRecipeTypeName(MechanicalRecipeStep recipe) {
        return recipe.typeName();
    }

    private static String describeIngredient(Ingredient ingredient) {
        var items = ingredient.getItems();
        if (items.length == 0) {
            return "?";
        }
        var stack = items[0];
        int amount = ingredient instanceof SizedIngredient sized ? sized.getAmount() : Math.max(1, stack.getCount());
        return "%sx%s".formatted(amount, stack.getHoverName().getString());
    }

    private static String describeFluidIngredient(FluidIngredient ingredient) {
        var stacks = ingredient.getMatchingFluidStacks();
        if (stacks.isEmpty()) {
            return "?";
        }
        var stack = stacks.get(0).copy();
        stack.setAmount(ingredient.getRequiredAmount());
        return describeFluidStack(stack);
    }

    private static String describeGTFluidIngredient(com.gregtechceu.gtceu.api.recipe.ingredient.FluidIngredient ingredient) {
        var stacks = ingredient.getStacks();
        if (stacks.length == 0) {
            return "?";
        }
        return describeFluidStack(stacks[0]);
    }

    private static String describeStack(ItemStack stack) {
        return "%sx%s".formatted(stack.getCount(), stack.getHoverName().getString());
    }

    private static String describeFluidStack(FluidStack stack) {
        return "%sB%s".formatted(formatBuckets(stack.getAmount()), stack.getDisplayName().getString());
    }

    private static String formatBuckets(int amount) {
        if (amount % 1000 == 0) {
            return Integer.toString(amount / 1000);
        }
        return String.format(Locale.ROOT, "%.3f", amount / 1000.0F);
    }

    private static String trimPercent(float chance) {
        float percent = chance * 100.0F;
        if (Math.abs(percent - Math.round(percent)) < 0.001F) {
            return Integer.toString(Math.round(percent));
        }
        return String.format(Locale.ROOT, "%.2f", percent);
    }

    private static String trimGTChance(int chance) {
        return trimPercent(chance / (float) ChanceLogic.getMaxChancedValue());
    }

    private static List<Integer> readMultipliers(@Nullable CompoundTag tag, int size) {
        if (size <= 0) {
            return List.of();
        }
        var result = new ArrayList<Integer>(Math.min(size, MAX_STEPS));
        int[] stored = tag == null ? new int[0] : tag.getIntArray(MULTIPLIERS);
        for (int i = 0; i < size && result.size() < MAX_STEPS; i++) {
            result.add(sanitizeMultiplier(i < stored.length ? stored[i] : 1));
        }
        return result;
    }

    private static int[] normalizeMultipliers(List<Integer> multipliers, int size) {
        var normalized = new int[Math.max(0, Math.min(size, MAX_STEPS))];
        for (int i = 0; i < normalized.length; i++) {
            int multiplier = i < multipliers.size() && multipliers.get(i) != null ? multipliers.get(i) : 1;
            normalized[i] = sanitizeMultiplier(multiplier);
        }
        return normalized;
    }

    private static int weightedSteps(int[] multipliers) {
        int total = 0;
        for (int multiplier : multipliers) {
            total += sanitizeMultiplier(multiplier);
        }
        return Math.max(1, total);
    }

    private static int recipeHash(List<ResourceLocation> recipeIds, int[] multipliers) {
        return 31 * recipeIds.hashCode() + Arrays.hashCode(multipliers);
    }

    private static final class NetRecipe {

        private final Map<AEItemKey, SizedIngredient> itemInputs = new LinkedHashMap<>();
        private final Map<AEItemKey, ItemStack> itemOutputs = new LinkedHashMap<>();
        private final List<ChancedItemOutput> chancedItemOutputs = new ArrayList<>();
        private final Map<AEFluidKey, com.gregtechceu.gtceu.api.recipe.ingredient.FluidIngredient> fluidInputs =
            new LinkedHashMap<>();
        private final Map<AEFluidKey, FluidStack> fluidOutputs = new LinkedHashMap<>();
        private ChanceLogic itemOutputChanceLogic = ChanceLogic.OR;
        private int duration;

        private static NetRecipe create(List<MechanicalRecipeStep> recipes, int[] multipliers) {
            var net = new NetRecipe();
            for (int i = 0; i < recipes.size(); i++) {
                var recipe = recipes.get(i);
                int multiplier = i < multipliers.length ? sanitizeMultiplier(multipliers[i]) : 1;
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

        private boolean isEmpty() {
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

        private GenericStack[] toPatternInputs() {
            var inputs = new ArrayList<GenericStack>();
            itemInputs.values().stream()
                .map(MechanicalEncapsulationPatternLogic::toGenericStack)
                .filter(Objects::nonNull)
                .forEach(inputs::add);
            fluidInputs.values().stream()
                .map(MechanicalEncapsulationPatternLogic::toGenericStack)
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

    private record ChancedItemOutput(ItemStack stack, int chance) {
    }
}
