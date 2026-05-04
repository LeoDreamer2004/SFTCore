package org.leodreamer.sftcore.api.registry.registrate;

import org.leodreamer.sftcore.common.data.lang.SFTTooltipsBuilder;

import com.gregtechceu.gtceu.api.block.MetaMachineBlock;
import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.item.MetaMachineItem;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.gregtechceu.gtceu.api.registry.registrate.MultiblockMachineBuilder;
import com.gregtechceu.gtceu.common.data.GTMaterialItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SFTMultiblockMachineBuilder
    extends MultiblockMachineBuilder<MultiblockMachineDefinition, SFTMultiblockMachineBuilder> {

    public SFTMultiblockMachineBuilder(
        GTRegistrate registrate,
        String name,
        BiFunction<BlockBehaviour.Properties, MultiblockMachineDefinition, MetaMachineBlock> blockFactory,
        BiFunction<MetaMachineBlock, Item.Properties, MetaMachineItem> itemFactory,
        Function<BlockEntityCreationInfo, MetaMachine> blockEntityFactory
    ) {
        super(registrate, name, blockFactory, itemFactory, blockEntityFactory);
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
}
