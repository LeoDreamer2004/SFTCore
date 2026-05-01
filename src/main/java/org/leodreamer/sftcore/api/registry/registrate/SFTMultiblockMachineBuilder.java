package org.leodreamer.sftcore.api.registry.registrate;

import com.gregtechceu.gtceu.api.block.IMachineBlock;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.item.MetaMachineItem;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.api.registry.registrate.MultiblockMachineBuilder;
import com.gregtechceu.gtceu.common.data.GTMaterialItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.function.TriFunction;
import org.leodreamer.sftcore.api.registry.SFTRegistrate;
import org.leodreamer.sftcore.common.data.lang.SFTTooltipsBuilder;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SFTMultiblockMachineBuilder extends MultiblockMachineBuilder<MultiblockMachineDefinition, SFTMultiblockMachineBuilder> {

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
            () -> new ItemLike[]{
                GTMaterialItems.MATERIAL_ITEMS.get(TagPrefix.dustTiny, GTMaterials.Ash)
            }
        );
        return this;
    }
}
