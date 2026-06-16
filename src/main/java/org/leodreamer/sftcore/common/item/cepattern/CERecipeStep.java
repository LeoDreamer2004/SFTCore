package org.leodreamer.sftcore.common.item.cepattern;

import org.leodreamer.sftcore.integration.IntegrateMods;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.crafter.MechanicalCraftingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.foundation.fluid.FluidIngredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A general adapter step for create recipes
 */
public record CERecipeStep(
    ResourceLocation id,
    ResourceLocation typeId,
    String titleKey,
    ItemStack machineIcon,
    List<Ingredient> itemInputs,
    List<FluidIngredient> fluidInputs,
    List<ProcessingOutput> itemOutputs,
    List<FluidStack> fluidOutputs,
    int duration
) {

    public static Optional<CERecipeStep> fromRecipe(Recipe<?> recipe) {
        if (recipe instanceof ProcessingRecipe<?> processingRecipe) {
            return Optional.of(from(processingRecipe));
        }
        if (recipe instanceof MechanicalCraftingRecipe mechanicalCraftingRecipe) {
            return Optional.of(from(mechanicalCraftingRecipe));
        }
        if (recipe instanceof SequencedAssemblyRecipe sequencedAssemblyRecipe) {
            return Optional.of(from(sequencedAssemblyRecipe));
        }
        return Optional.empty();
    }

    public static Optional<CERecipeStep> fromId(ResourceLocation id, Level level) {
        var recipe = level.getRecipeManager().byKey(id);
        return recipe.flatMap(CERecipeStep::fromRecipe);
    }

    public Component typeName() {
        return Component.translatable(titleKey);
    }

    private static CERecipeStep from(ProcessingRecipe<?> recipe) {
        var typeId = recipe.getTypeInfo().getId();
        return new CERecipeStep(
            recipe.getId(),
            typeId,
            titleKey(typeId),
            machineIcon(typeId),
            recipe.getIngredients(),
            recipe.getFluidIngredients(),
            recipe.getRollableResults(),
            recipe.getFluidResults(),
            Math.max(1, recipe.getProcessingDuration())
        );
    }

    private static CERecipeStep from(MechanicalCraftingRecipe recipe) {
        var output = recipe.getResultItem(RegistryAccess.EMPTY);
        var outputs = output.isEmpty() ? List.<ProcessingOutput>of() :
            List.of(new ProcessingOutput(output.copy(), 1.0F));
        var typeId = AllRecipeTypes.MECHANICAL_CRAFTING.getId();
        return new CERecipeStep(
            recipe.getId(),
            typeId,
            titleKey(typeId),
            AllBlocks.MECHANICAL_CRAFTER.asStack(),
            recipe.getIngredients(),
            List.of(),
            outputs,
            List.of(),
            100
        );
    }

    private static CERecipeStep from(SequencedAssemblyRecipe recipe) {
        var itemInputs = new ArrayList<Ingredient>();
        itemInputs.add(recipe.getIngredient());
        var fluidInputs = new ArrayList<FluidIngredient>();
        int loops = Math.max(1, recipe.getLoops());
        int duration = 0;

        for (int i = 0; i < loops; i++) {
            for (var sequencedRecipe : recipe.getSequence()) {
                var processingRecipe = sequencedRecipe.getRecipe();
                duration += Math.max(1, processingRecipe.getProcessingDuration());
                sequencedRecipe.getAsAssemblyRecipe().addAssemblyIngredients(itemInputs);
                sequencedRecipe.getAsAssemblyRecipe().addAssemblyFluidIngredients(fluidInputs);
            }
        }

        var itemOutputs = normalizeSequencedOutputs(recipe.resultPool);
        var typeId = AllRecipeTypes.SEQUENCED_ASSEMBLY.getId();
        return new CERecipeStep(
            recipe.getId(),
            typeId,
            titleKey(typeId),
            AllBlocks.MECHANICAL_ARM.asStack(),
            itemInputs,
            fluidInputs,
            itemOutputs,
            List.of(),
            Math.max(1, duration)
        );
    }

    private static List<ProcessingOutput> normalizeSequencedOutputs(List<ProcessingOutput> resultPool) {
        float totalWeight = 0.0F;
        for (var output : resultPool) {
            if (!output.getStack().isEmpty() && output.getChance() > 0.0F) {
                totalWeight += output.getChance();
            }
        }
        if (totalWeight <= 0.0F) {
            return List.of();
        }

        var outputs = new ArrayList<ProcessingOutput>();
        for (var output : resultPool) {
            if (output.getStack().isEmpty() || output.getChance() <= 0.0F) {
                continue;
            }
            outputs.add(new ProcessingOutput(output.getStack().copy(), output.getChance() / totalWeight));
        }
        return outputs;
    }

    private static String titleKey(ResourceLocation typeId) {
        if (!typeId.getNamespace().equals(IntegrateMods.CREATE)) {
            return "emi.category.%s.%s".formatted(typeId.getNamespace(), typeId.getPath().replace('/', '.'));
        }
        return "create.recipe." + switch (typeId.getPath()) {
            case "compacting" -> "automatic_packing";
            case "cutting" -> "sawing";
            case "emptying" -> "draining";
            case "filling" -> "spout_filling";
            case "splashing" -> "fan_washing";
            case "haunting" -> "fan_haunting";
            default -> typeId.getPath();
        };
    }

    private static ItemStack machineIcon(ResourceLocation typeId) {
        return switch (typeId.getPath()) {
            case "pressing" -> AllBlocks.MECHANICAL_PRESS.asStack();
            case "mixing", "compacting", "basin" -> AllBlocks.MECHANICAL_MIXER.asStack();
            case "deploying", "item_application" -> AllBlocks.DEPLOYER.asStack();
            case "filling", "emptying" -> AllBlocks.SPOUT.asStack();
            case "crushing" -> AllBlocks.CRUSHING_WHEEL.asStack();
            case "milling" -> AllBlocks.MILLSTONE.asStack();
            case "cutting" -> AllBlocks.MECHANICAL_SAW.asStack();
            case "splashing", "haunting" -> AllBlocks.ENCASED_FAN.asStack();
            default -> AllBlocks.COGWHEEL.asStack();
        };
    }
}
