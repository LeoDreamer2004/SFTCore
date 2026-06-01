package org.leodreamer.sftcore.integration.jade.provider;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.api.recipe.capability.GasRecipeCapability;
import org.leodreamer.sftcore.integration.jade.element.JadeGasStackElement;

import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.integration.jade.provider.MachineTraitProvider;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

import mekanism.api.MekanismAPI;
import mekanism.api.chemical.gas.GasStack;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.util.FluidTextHelper;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Gas fix for Jade in {@link com.gregtechceu.gtceu.integration.jade.provider.RecipeOutputProvider}
 */
@DataGenScanned
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GasRecipeOutputProvider extends MachineTraitProvider<RecipeLogic, CompoundTag> {

    public static final GasRecipeOutputProvider INSTANCE = new GasRecipeOutputProvider();

    private static final String WORKING = "Working";
    private static final String OUTPUT_GASES = "OutputGases";
    private static final String GAS_ID = "Gas";
    private static final String AMOUNT = "Amount";

    @RegisterLanguage("Recipe Gas Output")
    static final String JADE_CONFIG = "config.jade.plugin_sftcore.recipe_gas_output_info";

    private GasRecipeOutputProvider() {
        super(SFTCore.id("recipe_gas_output_info"), RecipeLogic.TYPE);
    }

    @Override
    protected CompoundTag write(RecipeLogic recipeLogic) {
        var data = new CompoundTag();

        if (!recipeLogic.isWorking()) {
            return data;
        }

        data.putBoolean(WORKING, true);

        var recipe = recipeLogic.getLastRecipe();
        if (recipe == null) {
            return data;
        }

        int recipeTier = RecipeHelper.getPreOCRecipeEuTier(recipe);
        int chanceTier = recipeTier + recipe.ocLevel;
        int runs = recipe.getTotalRuns();
        var chanceFunction = recipe.getType().getChanceFunction();

        var gasTags = new ListTag();

        for (var gasContent : recipe.getOutputContents(GasRecipeCapability.CAP)) {
            GasStack stack;

            try {
                stack = GasRecipeCapability.CAP.of(gasContent.content);
            } catch (Exception ignored) {
                continue;
            }

            if (stack == null || stack.isEmpty()) {
                continue;
            }

            long amount = stack.getAmount();

            // Same with the item/fluid in GTM
            if (gasContent.chance < gasContent.maxChance) {
                double amountD = (double) amount * runs *
                    chanceFunction.getBoostedChance(gasContent, recipeTier, chanceTier) / gasContent.maxChance;
                amount = Math.max(1L, Math.round(amountD));
            }

            var gasTag = new CompoundTag();
            gasTag.putString(GAS_ID, stack.getTypeRegistryName().toString());
            gasTag.putLong(AMOUNT, amount);
            gasTags.add(gasTag);
        }

        if (!gasTags.isEmpty()) {
            data.put(OUTPUT_GASES, gasTags);
        }

        return data;
    }

    @Override
    protected void addTooltip(
        CompoundTag capData,
        ITooltip tooltip,
        Player player,
        BlockAccessor block,
        BlockEntity blockEntity,
        IPluginConfig config
    ) {
        if (!capData.getBoolean(WORKING)) {
            return;
        }

        if (!capData.contains(OUTPUT_GASES, Tag.TAG_LIST)) {
            return;
        }

        var gasTags = capData.getList(OUTPUT_GASES, Tag.TAG_COMPOUND);
        if (gasTags.isEmpty()) {
            return;
        }

        for (var tag : gasTags) {
            if (!(tag instanceof CompoundTag gasTag)) {
                continue;
            }

            var stack = readGasStack(gasTag);
            if (stack.isEmpty()) {
                continue;
            }

            addGasTooltip(tooltip, stack);
        }
    }

    private static void addGasTooltip(ITooltip tooltip, GasStack stack) {
        var text = CommonComponents.space()
            .append(FluidTextHelper.getUnicodeMillibuckets(stack.getAmount(), true))
            .append(CommonComponents.space())
            .append(getGasName(stack))
            .withStyle(ChatFormatting.WHITE);

        tooltip.add(JadeGasStackElement.smallGas(stack));
        tooltip.append(text);
    }

    private static Component getGasName(GasStack stack) {
        return ComponentUtils.wrapInSquareBrackets(
            stack.getType().getTextComponent().copy()
        ).withStyle(ChatFormatting.WHITE);
    }

    private static GasStack readGasStack(CompoundTag tag) {
        var id = ResourceLocation.tryParse(tag.getString(GAS_ID));
        if (id == null) {
            return GasStack.EMPTY;
        }

        var gas = MekanismAPI.gasRegistry().getValue(id);
        if (gas == null || gas.isEmptyType()) {
            return GasStack.EMPTY;
        }

        long amount = tag.getLong(AMOUNT);
        if (amount <= 0) {
            return GasStack.EMPTY;
        }

        return new GasStack(gas, amount);
    }
}
