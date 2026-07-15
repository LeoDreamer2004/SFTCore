package org.leodreamer.sftcore.api.registry.registrate;

import org.leodreamer.sftcore.common.data.lang.SFTTooltipsBuilder;

import com.gregtechceu.gtceu.api.block.MetaMachineBlock;
import com.gregtechceu.gtceu.api.item.MetaMachineItem;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MachineInstanceFactory;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.gregtechceu.gtceu.api.registry.registrate.MachineBuilder;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.BiFunction;
import java.util.function.Function;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SFTMachineBuilder<DEFINITION extends MachineDefinition, MACHINE extends MetaMachine>
    extends MachineBuilder<DEFINITION, MACHINE, SFTMachineBuilder<DEFINITION, MACHINE>> {

    public SFTMachineBuilder(
        GTRegistrate registrate,
        String name,
        Function<ResourceLocation, DEFINITION> definition,
        BiFunction<BlockBehaviour.Properties, DEFINITION, MetaMachineBlock> blockFactory,
        BiFunction<MetaMachineBlock, Item.Properties, MetaMachineItem> itemFactory,
        MachineInstanceFactory<MACHINE> blockEntityFactory
    ) {
        super(registrate, name, definition, blockFactory, itemFactory, blockEntityFactory);
    }

    public SFTMachineBuilder<DEFINITION, MACHINE> tooltips(
        Function<SFTMachineBuilder<DEFINITION, MACHINE>, SFTTooltipsBuilder> tooltipsBuilder
    ) {
        return this.tooltips(tooltipsBuilder.apply(this).list());
    }
}
