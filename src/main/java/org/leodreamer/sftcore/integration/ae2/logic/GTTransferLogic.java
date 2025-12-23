package org.leodreamer.sftcore.integration.ae2.logic;

import com.gregtechceu.gtceu.api.block.MetaMachineBlock;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;

import net.minecraft.world.level.block.Block;

import appeng.helpers.patternprovider.PatternContainer;

import java.util.Optional;

public class GTTransferLogic {

    public static Optional<AvailableGTRow> tryBuild(PatternContainer container, GTRecipeType recipeType) {
        var group = container.getTerminalGroup();
        var item = group.icon();
        if (item == null) return Optional.empty();

        var block = Block.byItem(item.getItem());
        if (block instanceof MetaMachineBlock machineBlock) {
            for (var type : machineBlock.definition.getRecipeTypes()) {
                if (type == recipeType) {
                    return Optional.of(AvailableGTRow.of(item, group.name(), group.tooltip()));
                }
            }
        }
        return Optional.empty();
    }
}
