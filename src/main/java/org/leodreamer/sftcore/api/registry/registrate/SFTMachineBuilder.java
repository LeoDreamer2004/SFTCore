package org.leodreamer.sftcore.api.registry.registrate;

import com.gregtechceu.gtceu.api.block.MetaMachineBlock;
import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.item.MetaMachineItem;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.gregtechceu.gtceu.api.registry.registrate.MachineBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.leodreamer.sftcore.common.data.lang.SFTTooltipsBuilder;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.BiFunction;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SFTMachineBuilder<DEFINITION extends MachineDefinition>
    extends MachineBuilder<DEFINITION, SFTMachineBuilder<DEFINITION>> {


    public SFTMachineBuilder(
        GTRegistrate registrate, String name,
        Function<ResourceLocation, DEFINITION> definition,
        BiFunction<BlockBehaviour.Properties, DEFINITION, MetaMachineBlock> blockFactory,
        BiFunction<MetaMachineBlock, Item.Properties, MetaMachineItem> itemFactory,
        Function<BlockEntityCreationInfo, MetaMachine> blockEntityFactory) {
        super(registrate, name, definition, blockFactory, itemFactory, blockEntityFactory);
    }

    public SFTMachineBuilder<DEFINITION> tooltips(
        Function<SFTMachineBuilder<DEFINITION>, SFTTooltipsBuilder> tooltipsBuilder
    ) {
        return this.tooltips(tooltipsBuilder.apply(this).list());
    }
}
