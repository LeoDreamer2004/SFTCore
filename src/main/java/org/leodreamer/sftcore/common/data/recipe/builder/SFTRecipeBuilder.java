package org.leodreamer.sftcore.common.data.recipe.builder;

import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.leodreamer.sftcore.api.recipe.capability.StressRecipeCapability;
import org.leodreamer.sftcore.common.recipe.condition.RPMCondition;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SFTRecipeBuilder extends GTRecipeBuilder {
    public static final String INPUT_STRESS = "input_stress";

    public SFTRecipeBuilder(ResourceLocation id, GTRecipeType recipeType) {
        super(id, recipeType);
    }

    public SFTRecipeBuilder(GTRecipe toCopy, GTRecipeType recipeType) {
        super(toCopy, recipeType);
    }

    public static SFTRecipeBuilder of(ResourceLocation id, GTRecipeType recipeType) {
        return new SFTRecipeBuilder(id, recipeType);
    }

    @SafeVarargs
    @Override
    public final <T> SFTRecipeBuilder input(RecipeCapability<T> capability, T... obj) {
        super.input(capability, obj);
        return this;
    }

    @SafeVarargs
    @Override
    public final <T> SFTRecipeBuilder output(RecipeCapability<T> capability, T... obj) {
        super.output(capability, obj);
        return this;
    }

    @Override
    public SFTRecipeBuilder inputItems(Item input) {
        super.inputItems(input);
        return this;
    }

    @Override
    public SFTRecipeBuilder inputItems(Item input, int amount) {
        super.inputItems(input, amount);
        return this;
    }


    @Override
    public SFTRecipeBuilder inputItems(Supplier<? extends Item> input) {
        super.inputItems(input);
        return this;
    }

    @Override
    public SFTRecipeBuilder inputItems(Supplier<? extends Item> input, int amount) {
        super.inputItems(input, amount);
        return this;
    }

    @Override
    public SFTRecipeBuilder inputItems(ItemStack input) {
        super.inputItems(input);
        return this;
    }

    @Override
    public SFTRecipeBuilder notConsumable(Item item) {
        super.notConsumable(item);
        return this;
    }

    @Override
    public SFTRecipeBuilder outputItems(Item output) {
        super.outputItems(output);
        return this;
    }


    @Override
    public SFTRecipeBuilder outputItems(ItemStack output) {
        super.outputItems(output);
        return this;
    }

    @Override
    public SFTRecipeBuilder inputFluids(FluidStack input) {
        super.inputFluids(input);
        return this;
    }

    @Override
    public SFTRecipeBuilder outputFluids(FluidStack output) {
        super.outputFluids(output);
        return this;
    }

    @Override
    public SFTRecipeBuilder duration(int duration) {
        super.duration(duration);
        return this;
    }

    @Override
    public SFTRecipeBuilder notConsumableFluid(FluidStack fluid) {
        super.notConsumableFluid(fluid);
        return this;
    }

    @Override
    public SFTRecipeBuilder notConsumable(ItemStack input) {
        super.notConsumable(input);
        return this;
    }

    @Override
    public SFTRecipeBuilder circuitMeta(int configuration) {
        super.circuitMeta(configuration);
        return this;
    }

    public SFTRecipeBuilder inputStress(float stress) {
        input(StressRecipeCapability.CAP, stress);
        this.data.putFloat(INPUT_STRESS, stress);
        return this;
    }

    public SFTRecipeBuilder outputStress(float stress) {
        output(StressRecipeCapability.CAP, stress);
        this.data.putFloat("output_stress", stress);
        return this;
    }

    public SFTRecipeBuilder rpm(float rpm, boolean reverse) {
        addCondition(new RPMCondition(rpm).setReverse(reverse));
        return this;
    }

    public SFTRecipeBuilder rpm(float rpm) {
        return rpm(rpm, false);
    }
}
