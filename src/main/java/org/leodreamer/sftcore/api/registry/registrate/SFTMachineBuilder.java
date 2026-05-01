package org.leodreamer.sftcore.api.registry.registrate;

import com.gregtechceu.gtceu.api.block.IMachineBlock;
import com.gregtechceu.gtceu.api.item.MetaMachineItem;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.gregtechceu.gtceu.api.registry.registrate.MachineBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.function.TriFunction;
import org.leodreamer.sftcore.common.data.lang.SFTTooltipsBuilder;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.BiFunction;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SFTMachineBuilder<DEFINITION extends MachineDefinition>
    extends MachineBuilder<DEFINITION, SFTMachineBuilder<DEFINITION>> {

    public SFTMachineBuilder(
        GTRegistrate registrate,
        String name,
        Function<ResourceLocation, DEFINITION> definition,
        Function<IMachineBlockEntity, MetaMachine> machine,
        BiFunction<BlockBehaviour.Properties, DEFINITION, IMachineBlock> blockFactory,
        BiFunction<IMachineBlock, Item.Properties, MetaMachineItem> itemFactory,
        TriFunction<BlockEntityType<?>, BlockPos, BlockState, IMachineBlockEntity> blockEntityFactory
    ) {
        super(registrate, name, definition, machine, blockFactory, itemFactory, blockEntityFactory);
    }

    public SFTMachineBuilder<DEFINITION> tooltips(
        Function<SFTMachineBuilder<DEFINITION>, SFTTooltipsBuilder> tooltipsBuilder
    ) {
        return this.tooltips(tooltipsBuilder.apply(this).list());
    }
}
