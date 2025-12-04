package org.leodreamer.sftcore.api.registry.registrate;

import com.gregtechceu.gtceu.api.block.IMachineBlock;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.item.MetaMachineItem;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.gregtechceu.gtceu.api.registry.registrate.MachineBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.Nullable;
import org.leodreamer.sftcore.api.registry.SFTTooltipsBuilder;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SFTMachineBuilder<DEFINITION extends MachineDefinition> extends MachineBuilder<DEFINITION> {
    public SFTMachineBuilder(GTRegistrate registrate, String name, Function<ResourceLocation, DEFINITION> definition, Function<IMachineBlockEntity, MetaMachine> machine, BiFunction<BlockBehaviour.Properties, DEFINITION, IMachineBlock> blockFactory, BiFunction<IMachineBlock, Item.Properties, MetaMachineItem> itemFactory, TriFunction<BlockEntityType<?>, BlockPos, BlockState, IMachineBlockEntity> blockEntityFactory) {
        super(registrate, name, definition, machine, blockFactory, itemFactory, blockEntityFactory);
    }

    public SFTMachineBuilder<DEFINITION> tooltips(Function<SFTMachineBuilder<DEFINITION>, SFTTooltipsBuilder> tooltipsBuilder) {
        return this.tooltips(tooltipsBuilder.apply(this).all());
    }

    /// COPIED METHODS

    @Override
    public SFTMachineBuilder<DEFINITION> rotationState(RotationState rotationState) {
        return (SFTMachineBuilder<DEFINITION>) super.rotationState(rotationState);
    }

    @Override
    public SFTMachineBuilder<DEFINITION> recipeTypes(GTRecipeType... types) {
        return (SFTMachineBuilder<DEFINITION>) super.recipeTypes(types);
    }

    @Override
    public SFTMachineBuilder<DEFINITION> abilities(PartAbility... abilities) {
        return (SFTMachineBuilder<DEFINITION>) super.abilities(abilities);
    }

    @Override
    public SFTMachineBuilder<DEFINITION> modelProperty(Property<?> property) {
        return (SFTMachineBuilder<DEFINITION>) super.modelProperty(property);
    }

    @Override
    public <T extends Comparable<T>> SFTMachineBuilder<DEFINITION> modelProperty(Property<T> property, @Nullable T defaultValue) {
        return (SFTMachineBuilder<DEFINITION>) super.modelProperty(property, defaultValue);
    }

    @Override
    public SFTMachineBuilder<DEFINITION> modelProperties(Property<?>... properties) {
        return (SFTMachineBuilder<DEFINITION>) super.modelProperties(properties);
    }

    @Override
    public SFTMachineBuilder<DEFINITION> tooltips(@Nullable Component... components) {
        return (SFTMachineBuilder<DEFINITION>) super.tooltips(components);
    }

    @Override
    public SFTMachineBuilder<DEFINITION> tooltips(List<? extends @Nullable Component> components) {
        return (SFTMachineBuilder<DEFINITION>) super.tooltips(components);
    }
}
