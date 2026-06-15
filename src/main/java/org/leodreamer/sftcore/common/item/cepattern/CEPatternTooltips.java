package org.leodreamer.sftcore.common.item.cepattern;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;

import com.gregtechceu.gtceu.api.recipe.chance.logic.ChanceLogic;
import com.gregtechceu.gtceu.api.recipe.ingredient.SizedIngredient;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@DataGenScanned
public final class CEPatternTooltips {

    @RegisterLanguage("Blank Pattern")
    public static final String TOOLTIP_BLANK = "item.sftcore.create_encapsulation_pattern.tooltip.blank";

    @RegisterLanguage("Contains %s Create recipe steps")
    public static final String TOOLTIP_STEPS = "item.sftcore.create_encapsulation_pattern.tooltip.steps";

    private CEPatternTooltips() {}

    public static List<Component> getTooltip(ItemStack stack, Level level) {
        var data = CEPatternData.read(stack.getOrCreateTag());
        var recipeIds = data.recipeIds();
        if (recipeIds.isEmpty()) {
            return List.of(
                Component.translatable(TOOLTIP_BLANK)
                    .withStyle(ChatFormatting.GRAY)
            );
        }

        var tooltips = new ArrayList<Component>();
        tooltips.add(
            Component.translatable(
                TOOLTIP_STEPS,
                recipeIds.size()
            ).withStyle(ChatFormatting.GRAY)
        );

        if (level != null) {
            var steps = CEPatternLogic.resolveSteps(level, recipeIds);
            if (!steps.isEmpty()) {
                tooltips.add(
                    Component.literal(describeNetRecipe(steps, data.multipliers())).withStyle(ChatFormatting.DARK_GRAY)
                );
            }
        }
        return tooltips;
    }

    /**
     * Generate a description for recipe like A+B->C
     */
    public static String describeStep(CERecipeStep recipe) {
        var inputs = new ArrayList<String>();
        recipe.itemInputs().stream()
            .filter(ingredient -> !ingredient.isEmpty())
            .map(CEPatternTooltips::describeIngredient)
            .forEach(inputs::add);
        recipe.fluidInputs().stream()
            .filter(ingredient -> !ingredient.getMatchingFluidStacks().isEmpty())
            .map(CEPatternTooltips::describeFluidIngredient)
            .forEach(inputs::add);

        var outputs = new ArrayList<String>();
        recipe.itemOutputs().stream()
            .filter(output -> !output.getStack().isEmpty())
            .map(
                output -> output.getChance() >= 1.0F ? describeStack(output.getStack()) :
                    "%s(%s%%)".formatted(describeStack(output.getStack()), trimPercent(output.getChance()))
            )
            .forEach(outputs::add);
        recipe.fluidOutputs().stream()
            .filter(stack -> !stack.isEmpty())
            .map(CEPatternTooltips::describeFluidStack)
            .forEach(outputs::add);
        return String.join("+", inputs) + " -> " + String.join("+", outputs);
    }

    public static String describeNetRecipe(List<CERecipeStep> recipes, List<Integer> multipliers) {
        if (recipes.size() != multipliers.size()) {
            return "";
        }
        var net = CEPatternLogic.CompiledRecipe.create(recipes, multipliers);
        if (net.isEmpty()) {
            return "";
        }
        var in = new ArrayList<String>();
        net.itemInputs().values().stream().map(CEPatternTooltips::describeIngredient).forEach(in::add);
        net.fluidInputs().values().stream().map(CEPatternTooltips::describeGTFluidIngredient).forEach(in::add);

        var out = new ArrayList<String>();
        net.itemOutputs().values().stream().map(CEPatternTooltips::describeStack).forEach(out::add);
        net.chancedItemOutputs().stream().map(
            c -> "%s(%s%%)".formatted(
                describeStack(c.stack()),
                trimGTChance(c.chance())
            )
        ).forEach(out::add);
        net.fluidOutputs().values().stream().map(CEPatternTooltips::describeFluidStack).forEach(out::add);
        return String.join("+", in) + " -> " + String.join("+", out);
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

    private static String describeFluidIngredient(com.simibubi.create.foundation.fluid.FluidIngredient ingredient) {
        var stacks = ingredient.getMatchingFluidStacks();
        if (stacks.isEmpty()) {
            return "?";
        }
        var stack = stacks.get(0).copy();
        stack.setAmount(ingredient.getRequiredAmount());
        return describeFluidStack(stack);
    }

    private static String describeGTFluidIngredient(
        com.gregtechceu.gtceu.api.recipe.ingredient.FluidIngredient ingredient
    ) {
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
}
