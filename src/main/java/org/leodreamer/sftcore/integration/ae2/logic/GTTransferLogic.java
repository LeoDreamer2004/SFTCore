package org.leodreamer.sftcore.integration.ae2.logic;

import appeng.api.networking.IGridNode;
import appeng.helpers.patternprovider.PatternContainer;
import com.gregtechceu.gtceu.api.block.MetaMachineBlock;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import net.minecraft.world.level.block.Block;
import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.integration.ae2.feature.HackyContainerGroupProxy;
import org.leodreamer.sftcore.integration.ae2.feature.ISendToGTMachine;

import java.util.Optional;

public class GTTransferLogic {

    public static Optional<AvailableGTRow> tryBuild(
            PatternContainer container, IGridNode containerNode, ISendToGTMachine.RecipeInfo recipeInfo
    ) {
        var group = container.getTerminalGroup();
        var item = group.icon();
        if (item == null) return Optional.empty();

        var block = Block.byItem(item.getItem());
        if (block instanceof MetaMachineBlock machineBlock) {
            for (var type : machineBlock.definition.getRecipeTypes()) {
                if (type != recipeInfo.type()) continue;
                var hacky =HackyContainerGroupProxy.of(group);
                var pos = hacky.getBlockBos();
                if (pos != null) {
                    var machine = MetaMachine.getMachine(containerNode.getLevel(), pos);
                    if (machine instanceof IRecipeLogicMachine logic && logic.getRecipeType() != type) {
                        // the machine contains this type, but not using this!
                        return Optional.empty();
                    }
                }
                var result = AvailableGTRow.of(group);
                int circuit = hacky.getCircuit();
                if (circuit != 0 && circuit == recipeInfo.circuit()) {
                    // the circuit matches! Probably this machine!
                    SFTCore.LOGGER.info("Found a matching machine with the same circuit {}!", circuit);
                    result.withWeight(100);
                }
                return Optional.of(result);
            }
        }
        return Optional.empty();
    }
}
