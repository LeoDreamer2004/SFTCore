package org.leodreamer.sftcore.api.registry;

import org.leodreamer.sftcore.api.registry.registrate.SFTMachineBuilder;
import org.leodreamer.sftcore.api.registry.registrate.SFTMultiblockMachineBuilder;

import com.gregtechceu.gtceu.api.block.MetaMachineBlock;
import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.item.MetaMachineItem;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockBehaviour;

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
        String name, Function<BlockEntityCreationInfo, MetaMachine> blockEntityFactory
    ) {
        return new SFTMultiblockMachineBuilder(
            this,
            name,
            MetaMachineBlock::new,
            MetaMachineItem::new,
            blockEntityFactory
        );
    }

    @Override
    public SFTMachineBuilder<MachineDefinition> machine(
        String name,
        Function<BlockEntityCreationInfo, MetaMachine> blockEntityFactory
    ) {
        return new SFTMachineBuilder<>(
            this,
            name,
            MachineDefinition::new,
            MetaMachineBlock::new,
            MetaMachineItem::new,
            blockEntityFactory
        );
    }

    public <DEFINITION extends MachineDefinition> SFTMachineBuilder<DEFINITION> machine(
        String name,
        Function<ResourceLocation, DEFINITION> definitionFactory,
        BiFunction<BlockBehaviour.Properties, DEFINITION, MetaMachineBlock> blockFactory,
        BiFunction<MetaMachineBlock, Item.Properties, MetaMachineItem> itemFactory,
        Function<BlockEntityCreationInfo, MetaMachine> blockEntityFactory
    ) {
        return new SFTMachineBuilder<>(
            this, name, definitionFactory, blockFactory, itemFactory, blockEntityFactory
        );
    }
}
