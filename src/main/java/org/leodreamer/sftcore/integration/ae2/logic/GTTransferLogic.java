package org.leodreamer.sftcore.integration.ae2.logic;

import com.gregtechceu.gtceu.api.block.MetaMachineBlock;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;

import appeng.api.networking.IGridNode;
import appeng.helpers.patternprovider.PatternContainer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class GTTransferLogic {

    public static Optional<AvailableGTRow> tryBuild(
        PatternContainer container, IGridNode containerNode, GTRecipeType recipeType
    ) {
        var group = container.getTerminalGroup();
        var item = group.icon();
        if (item == null) return Optional.empty();

        var block = Block.byItem(item.getItem());
        if (block instanceof MetaMachineBlock machineBlock) {
            for (var type : machineBlock.definition.getRecipeTypes()) {
                if (type != recipeType) continue;

                var tooltips = group.tooltip();
                if (!tooltips.isEmpty()) {
                    try {
                        var pos = getBlockPos(tooltips);
                        var machine = MetaMachine.getMachine(containerNode.getLevel(), pos);
                        if (
                            machine instanceof IRecipeLogicMachine logicMachine && logicMachine.getRecipeType() != type
                        ) {
                            // the machine contains this type, but not using this!
                            return Optional.empty();
                        }
                    } catch (RuntimeException ignored) {
                        // the tooltip for block pos may not exist
                    }
                }
                return Optional.of(AvailableGTRow.of(item, group.name(), tooltips));
            }
        }
        return Optional.empty();
    }

    /**
     * hacky way to get x, y, z from the tooltip
     */
    private static @NotNull BlockPos getBlockPos(List<Component> tooltips) {
        var tooltip = tooltips.getLast().getString();
        // tooltip: "block position: x y z"
        var parts = tooltip.split(" ");
        var x = Integer.parseInt(parts[parts.length - 3]);
        var y = Integer.parseInt(parts[parts.length - 2]);
        var z = Integer.parseInt(parts[parts.length - 1]);
        return new BlockPos(x, y, z);
    }
}
