package org.leodreamer.sftcore.api.registry.registrate;

import org.leodreamer.sftcore.api.registry.SFTRegistrate;
import org.leodreamer.sftcore.api.registry.SFTTooltipsBuilder;

import com.gregtechceu.gtceu.api.block.IMachineBlock;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.gui.editor.EditableMachineUI;
import com.gregtechceu.gtceu.api.item.MetaMachineItem;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.gregtechceu.gtceu.api.registry.registrate.MachineBuilder;
import com.gregtechceu.gtceu.api.registry.registrate.MultiblockMachineBuilder;
import com.gregtechceu.gtceu.api.registry.registrate.provider.GTBlockstateProvider;
import com.gregtechceu.gtceu.common.data.GTMaterialItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.*;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SFTMultiblockMachineBuilder extends MultiblockMachineBuilder {

    public SFTMultiblockMachineBuilder(
        SFTRegistrate registrate,
        String name,
        Function<IMachineBlockEntity, ? extends MultiblockControllerMachine> metaMachine,
        BiFunction<BlockBehaviour.Properties, MultiblockMachineDefinition, IMachineBlock> blockFactory,
        BiFunction<IMachineBlock, Item.Properties, MetaMachineItem> itemFactory,
        TriFunction<BlockEntityType<?>, BlockPos, BlockState, IMachineBlockEntity> blockEntityFactory
    ) {
        super(registrate, name, metaMachine, blockFactory, itemFactory, blockEntityFactory);
    }

    public SFTMultiblockMachineBuilder tooltips(
        UnaryOperator<SFTTooltipsBuilder> tooltipsBuilder
    ) {
        return this.tooltips(tooltipsBuilder.apply(SFTTooltipsBuilder.machine(this.id)).list());
    }

    public SFTMultiblockMachineBuilder recoverAsh() {
        recoveryItems(
            () -> new ItemLike[] {
                GTMaterialItems.MATERIAL_ITEMS.get(TagPrefix.dustTiny, GTMaterials.Ash)
            }
        );
        return this;
    }

    /// COPIED METHODS

    @Override
    public SFTMultiblockMachineBuilder machine(Function<IMachineBlockEntity, MetaMachine> metaMachine) {
        return (SFTMultiblockMachineBuilder) super.machine(metaMachine);
    }

    @Override
    public SFTMultiblockMachineBuilder generator(boolean generator) {
        return (SFTMultiblockMachineBuilder) super.generator(generator);
    }

    @Override
    public SFTMultiblockMachineBuilder model(@Nullable MachineBuilder.ModelInitializer model) {
        return (SFTMultiblockMachineBuilder) super.model(model);
    }

    @Override
    public SFTMultiblockMachineBuilder blockModel(
        @Nullable NonNullBiConsumer<DataGenContext<Block, ? extends Block>, GTBlockstateProvider> blockModel
    ) {
        return (SFTMultiblockMachineBuilder) super.blockModel(blockModel);
    }

    @Override
    public SFTMultiblockMachineBuilder shape(VoxelShape shape) {
        return (SFTMultiblockMachineBuilder) super.shape(shape);
    }

    @Override
    public SFTMultiblockMachineBuilder multiblockPreviewRenderer(
        boolean multiBlockWorldPreview,
        boolean multiBlockXEIPreview
    ) {
        return (SFTMultiblockMachineBuilder) super.multiblockPreviewRenderer(
            multiBlockWorldPreview,
            multiBlockXEIPreview
        );
    }

    @Override
    public SFTMultiblockMachineBuilder rotationState(RotationState rotationState) {
        return (SFTMultiblockMachineBuilder) super.rotationState(rotationState);
    }

    @Override
    public SFTMultiblockMachineBuilder hasBER(boolean hasBER) {
        return (SFTMultiblockMachineBuilder) super.hasBER(hasBER);
    }

    @Override
    public SFTMultiblockMachineBuilder blockProp(NonNullUnaryOperator<BlockBehaviour.Properties> blockProp) {
        return (SFTMultiblockMachineBuilder) super.blockProp(blockProp);
    }

    @Override
    public SFTMultiblockMachineBuilder itemProp(NonNullUnaryOperator<Item.Properties> itemProp) {
        return (SFTMultiblockMachineBuilder) super.itemProp(itemProp);
    }

    @Override
    public SFTMultiblockMachineBuilder blockBuilder(Consumer<BlockBuilder<? extends Block, ?>> blockBuilder) {
        return (SFTMultiblockMachineBuilder) super.blockBuilder(blockBuilder);
    }

    @Override
    public SFTMultiblockMachineBuilder itemBuilder(Consumer<ItemBuilder<? extends MetaMachineItem, ?>> itemBuilder) {
        return (SFTMultiblockMachineBuilder) super.itemBuilder(itemBuilder);
    }

    @Override
    public SFTMultiblockMachineBuilder recipeTypes(GTRecipeType... recipeTypes) {
        return (SFTMultiblockMachineBuilder) super.recipeTypes(recipeTypes);
    }

    @Override
    public SFTMultiblockMachineBuilder recipeType(GTRecipeType recipeTypes) {
        return (SFTMultiblockMachineBuilder) super.recipeType(recipeTypes);
    }

    @Override
    public SFTMultiblockMachineBuilder tier(int tier) {
        return (SFTMultiblockMachineBuilder) super.tier(tier);
    }

    public SFTMultiblockMachineBuilder recipeOutputLimits(Reference2IntMap<RecipeCapability<?>> map) {
        return (SFTMultiblockMachineBuilder) super.recipeOutputLimits(map);
    }

    @Override
    public SFTMultiblockMachineBuilder addOutputLimit(RecipeCapability<?> capability, int limit) {
        return (SFTMultiblockMachineBuilder) super.addOutputLimit(capability, limit);
    }

    @Override
    public SFTMultiblockMachineBuilder itemColor(BiFunction<ItemStack, Integer, Integer> itemColor) {
        return (SFTMultiblockMachineBuilder) super.itemColor(itemColor);
    }

    @Override
    public SFTMultiblockMachineBuilder simpleModel(ResourceLocation model) {
        return (SFTMultiblockMachineBuilder) super.simpleModel(model);
    }

    @Override
    public SFTMultiblockMachineBuilder defaultModel() {
        return (SFTMultiblockMachineBuilder) super.defaultModel();
    }

    @Override
    public SFTMultiblockMachineBuilder tieredHullModel(ResourceLocation model) {
        return (SFTMultiblockMachineBuilder) super.tieredHullModel(model);
    }

    @Override
    public SFTMultiblockMachineBuilder overlayTieredHullModel(String name) {
        return (SFTMultiblockMachineBuilder) super.overlayTieredHullModel(name);
    }

    @Override
    public SFTMultiblockMachineBuilder overlayTieredHullModel(ResourceLocation overlayModel) {
        return (SFTMultiblockMachineBuilder) super.overlayTieredHullModel(overlayModel);
    }

    @Override
    public SFTMultiblockMachineBuilder colorOverlayTieredHullModel(String overlay) {
        return (SFTMultiblockMachineBuilder) super.colorOverlayTieredHullModel(overlay);
    }

    @Override
    public SFTMultiblockMachineBuilder colorOverlayTieredHullModel(
        String overlay,
        @Nullable String pipeOverlay,
        @Nullable String emissiveOverlay
    ) {
        return (SFTMultiblockMachineBuilder) super.colorOverlayTieredHullModel(overlay, pipeOverlay, emissiveOverlay);
    }

    @Override
    public SFTMultiblockMachineBuilder colorOverlayTieredHullModel(ResourceLocation overlay) {
        return (SFTMultiblockMachineBuilder) super.colorOverlayTieredHullModel(overlay);
    }

    @Override
    public SFTMultiblockMachineBuilder colorOverlayTieredHullModel(
        ResourceLocation overlay,
        @Nullable ResourceLocation pipeOverlay,
        @Nullable ResourceLocation emissiveOverlay
    ) {
        return (SFTMultiblockMachineBuilder) super.colorOverlayTieredHullModel(overlay, pipeOverlay, emissiveOverlay);
    }

    @Override
    public SFTMultiblockMachineBuilder workableTieredHullModel(ResourceLocation workableModel) {
        return (SFTMultiblockMachineBuilder) super.workableTieredHullModel(workableModel);
    }

    @Override
    public SFTMultiblockMachineBuilder simpleGeneratorModel(ResourceLocation workableModel) {
        return (SFTMultiblockMachineBuilder) super.simpleGeneratorModel(workableModel);
    }

    @Override
    public SFTMultiblockMachineBuilder workableCasingModel(ResourceLocation baseCasing, ResourceLocation overlayModel) {
        return (SFTMultiblockMachineBuilder) super.workableCasingModel(baseCasing, overlayModel);
    }

    @Override
    public SFTMultiblockMachineBuilder sidedOverlayCasingModel(
        ResourceLocation baseCasing,
        ResourceLocation workableModel
    ) {
        return (SFTMultiblockMachineBuilder) super.sidedOverlayCasingModel(baseCasing, workableModel);
    }

    @Override
    public SFTMultiblockMachineBuilder sidedWorkableCasingModel(
        ResourceLocation baseCasing,
        ResourceLocation workableModel
    ) {
        return (SFTMultiblockMachineBuilder) super.sidedWorkableCasingModel(baseCasing, workableModel);
    }

    @Override
    public SFTMultiblockMachineBuilder tooltipBuilder(@Nullable BiConsumer<ItemStack, List<Component>> tooltipBuilder) {
        return (SFTMultiblockMachineBuilder) super.tooltipBuilder(tooltipBuilder);
    }

    @Override
    public SFTMultiblockMachineBuilder appearance(Supplier<BlockState> state) {
        return (SFTMultiblockMachineBuilder) super.appearance(state);
    }

    @Override
    public SFTMultiblockMachineBuilder appearanceBlock(Supplier<? extends Block> block) {
        return (SFTMultiblockMachineBuilder) super.appearanceBlock(block);
    }

    @Override
    public SFTMultiblockMachineBuilder langValue(@Nullable String langValue) {
        return (SFTMultiblockMachineBuilder) super.langValue(langValue);
    }

    @Override
    public SFTMultiblockMachineBuilder overlaySteamHullModel(String name) {
        return (SFTMultiblockMachineBuilder) super.overlaySteamHullModel(name);
    }

    @Override
    public SFTMultiblockMachineBuilder overlaySteamHullModel(ResourceLocation overlayModel) {
        return (SFTMultiblockMachineBuilder) super.overlaySteamHullModel(overlayModel);
    }

    @Override
    public SFTMultiblockMachineBuilder colorOverlaySteamHullModel(String overlay) {
        return (SFTMultiblockMachineBuilder) super.colorOverlaySteamHullModel(overlay);
    }

    @Override
    public SFTMultiblockMachineBuilder colorOverlaySteamHullModel(
        String overlay,
        @Nullable ResourceLocation pipeOverlay,
        @Nullable String emissiveOverlay
    ) {
        return (SFTMultiblockMachineBuilder) super.colorOverlaySteamHullModel(overlay, pipeOverlay, emissiveOverlay);
    }

    @Override
    public SFTMultiblockMachineBuilder colorOverlaySteamHullModel(
        ResourceLocation overlay,
        @Nullable ResourceLocation pipeOverlay,
        @Nullable ResourceLocation emissiveOverlay
    ) {
        return (SFTMultiblockMachineBuilder) super.colorOverlaySteamHullModel(overlay, pipeOverlay, emissiveOverlay);
    }

    @Override
    public SFTMultiblockMachineBuilder colorOverlaySteamHullModel(ResourceLocation overlay) {
        return (SFTMultiblockMachineBuilder) super.colorOverlaySteamHullModel(overlay);
    }

    @Override
    public SFTMultiblockMachineBuilder workableSteamHullModel(boolean isHighPressure, ResourceLocation workableModel) {
        return (SFTMultiblockMachineBuilder) super.workableSteamHullModel(isHighPressure, workableModel);
    }

    @Override
    public SFTMultiblockMachineBuilder tooltips(@Nullable Component... components) {
        return (SFTMultiblockMachineBuilder) super.tooltips(components);
    }

    @Override
    public SFTMultiblockMachineBuilder tooltips(List<? extends @Nullable Component> components) {
        return (SFTMultiblockMachineBuilder) super.tooltips(components);
    }

    @Override
    public SFTMultiblockMachineBuilder conditionalTooltip(Component component, BooleanSupplier condition) {
        return (SFTMultiblockMachineBuilder) super.conditionalTooltip(component, condition);
    }

    @Override
    public SFTMultiblockMachineBuilder conditionalTooltip(Component component, boolean condition) {
        return (SFTMultiblockMachineBuilder) super.conditionalTooltip(component, condition);
    }

    @Override
    public SFTMultiblockMachineBuilder abilities(PartAbility... abilities) {
        return (SFTMultiblockMachineBuilder) super.abilities(abilities);
    }

    @Override
    public SFTMultiblockMachineBuilder modelProperty(Property<?> property) {
        return (SFTMultiblockMachineBuilder) super.modelProperty(property);
    }

    @Override
    public <T extends Comparable<T>> SFTMultiblockMachineBuilder modelProperty(
        Property<T> property,
        @Nullable T defaultValue
    ) {
        return (SFTMultiblockMachineBuilder) super.modelProperty(property, defaultValue);
    }

    @Override
    public SFTMultiblockMachineBuilder modelProperties(Property<?>... properties) {
        return (SFTMultiblockMachineBuilder) super.modelProperties(properties);
    }

    @Override
    public SFTMultiblockMachineBuilder modelProperties(Collection<Property<?>> properties) {
        return (SFTMultiblockMachineBuilder) super.modelProperties(properties);
    }

    @Override
    public SFTMultiblockMachineBuilder modelProperties(Map<Property<?>, ? extends Comparable<?>> properties) {
        return (SFTMultiblockMachineBuilder) super.modelProperties(properties);
    }

    @Override
    public SFTMultiblockMachineBuilder removeModelProperty(Property<?> property) {
        return (SFTMultiblockMachineBuilder) super.removeModelProperty(property);
    }

    @Override
    public SFTMultiblockMachineBuilder clearModelProperties() {
        return (SFTMultiblockMachineBuilder) super.clearModelProperties();
    }

    @Override
    public SFTMultiblockMachineBuilder paintingColor(int paintingColor) {
        return (SFTMultiblockMachineBuilder) super.paintingColor(paintingColor);
    }

    @Override
    public SFTMultiblockMachineBuilder recipeModifier(RecipeModifier recipeModifier) {
        return (SFTMultiblockMachineBuilder) super.recipeModifier(recipeModifier);
    }

    @Override
    public SFTMultiblockMachineBuilder recipeModifier(RecipeModifier recipeModifier, boolean alwaysTryModifyRecipe) {
        return (SFTMultiblockMachineBuilder) super.recipeModifier(recipeModifier, alwaysTryModifyRecipe);
    }

    @Override
    public SFTMultiblockMachineBuilder recipeModifiers(RecipeModifier... recipeModifiers) {
        return (SFTMultiblockMachineBuilder) super.recipeModifiers(recipeModifiers);
    }

    @Override
    public SFTMultiblockMachineBuilder recipeModifiers(
        boolean alwaysTryModifyRecipe,
        RecipeModifier... recipeModifiers
    ) {
        return (SFTMultiblockMachineBuilder) super.recipeModifiers(alwaysTryModifyRecipe, recipeModifiers);
    }

    public SFTMultiblockMachineBuilder noRecipeModifier() {
        return (SFTMultiblockMachineBuilder) super.noRecipeModifier();
    }

    @Override
    public SFTMultiblockMachineBuilder alwaysTryModifyRecipe(boolean alwaysTryModifyRecipe) {
        return (SFTMultiblockMachineBuilder) super.alwaysTryModifyRecipe(alwaysTryModifyRecipe);
    }

    @Override
    public SFTMultiblockMachineBuilder beforeWorking(BiPredicate<IRecipeLogicMachine, GTRecipe> beforeWorking) {
        return (SFTMultiblockMachineBuilder) super.beforeWorking(beforeWorking);
    }

    @Override
    public SFTMultiblockMachineBuilder onWorking(Predicate<IRecipeLogicMachine> onWorking) {
        return (SFTMultiblockMachineBuilder) super.onWorking(onWorking);
    }

    @Override
    public SFTMultiblockMachineBuilder onWaiting(Consumer<IRecipeLogicMachine> onWaiting) {
        return (SFTMultiblockMachineBuilder) super.onWaiting(onWaiting);
    }

    @Override
    public SFTMultiblockMachineBuilder afterWorking(Consumer<IRecipeLogicMachine> afterWorking) {
        return (SFTMultiblockMachineBuilder) super.afterWorking(afterWorking);
    }

    @Override
    public SFTMultiblockMachineBuilder regressWhenWaiting(boolean regressWhenWaiting) {
        return (SFTMultiblockMachineBuilder) super.regressWhenWaiting(regressWhenWaiting);
    }

    @Override
    public SFTMultiblockMachineBuilder editableUI(@Nullable EditableMachineUI editableUI) {
        return (SFTMultiblockMachineBuilder) super.editableUI(editableUI);
    }

    @Override
    public SFTMultiblockMachineBuilder onBlockEntityRegister(
        NonNullConsumer<BlockEntityType<BlockEntity>> onBlockEntityRegister
    ) {
        return (SFTMultiblockMachineBuilder) super.onBlockEntityRegister(onBlockEntityRegister);
    }

    @Override
    public SFTMultiblockMachineBuilder allowExtendedFacing(boolean allowExtendedFacing) {
        return (SFTMultiblockMachineBuilder) super.allowExtendedFacing(allowExtendedFacing);
    }

    @Override
    public SFTMultiblockMachineBuilder allowCoverOnFront(boolean allowCoverOnFront) {
        return (SFTMultiblockMachineBuilder) super.allowCoverOnFront(allowCoverOnFront);
    }

    @Override
    public SFTMultiblockMachineBuilder pattern(Function<MultiblockMachineDefinition, BlockPattern> pattern) {
        return (SFTMultiblockMachineBuilder) super.pattern(pattern);
    }
}
