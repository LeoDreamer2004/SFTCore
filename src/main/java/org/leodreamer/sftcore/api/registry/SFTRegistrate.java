package org.leodreamer.sftcore.api.registry;

import com.gregtechceu.gtceu.api.block.IMachineBlock;
import com.gregtechceu.gtceu.api.registry.registrate.MachineBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.function.TriFunction;
import org.leodreamer.sftcore.api.registry.registrate.SFTMachineBuilder;
import org.leodreamer.sftcore.api.registry.registrate.SFTMultiblockMachineBuilder;

import com.gregtechceu.gtceu.api.block.MetaMachineBlock;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.item.MetaMachineItem;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;

import net.minecraft.MethodsReturnNonnullByDefault;

import java.util.function.BiFunction;
import java.util.function.Function;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SFTRegistrate extends GTRegistrate {

    protected SFTRegistrate(String modId) {
        super(modId);
    }

    public static SFTRegistrate create(String modId) {
        return new SFTRegistrate(modId);
    }

    @Override
    public SFTMultiblockMachineBuilder multiblock(
        String name,
        Function<IMachineBlockEntity, ? extends MultiblockControllerMachine> metaMachine
    ) {
        return new SFTMultiblockMachineBuilder(
            this,
            name,
            metaMachine,
            MetaMachineBlock::new,
            MetaMachineItem::new,
            MetaMachineBlockEntity::new
        );
    }

    @Override
    public SFTMachineBuilder<MachineDefinition> machine(
        String name,
        Function<IMachineBlockEntity, MetaMachine> metaMachine
    ) {
        return new SFTMachineBuilder<>(
            this,
            name,
            MachineDefinition::new,
            metaMachine,
            MetaMachineBlock::new,
            MetaMachineItem::new,
            MetaMachineBlockEntity::new
        );
    }

    @Override
    public <DEFINITION extends MachineDefinition> SFTMachineBuilder<DEFINITION> machine(String name, Function<ResourceLocation, DEFINITION> definitionFactory, Function<IMachineBlockEntity, MetaMachine> metaMachine, BiFunction<BlockBehaviour.Properties, DEFINITION, IMachineBlock> blockFactory, BiFunction<IMachineBlock, Item.Properties, MetaMachineItem> itemFactory, TriFunction<BlockEntityType<?>, BlockPos, BlockState, IMachineBlockEntity> blockEntityFactory) {
        return new SFTMachineBuilder<>(this, name, definitionFactory, metaMachine, blockFactory, itemFactory, blockEntityFactory);
    }
}
