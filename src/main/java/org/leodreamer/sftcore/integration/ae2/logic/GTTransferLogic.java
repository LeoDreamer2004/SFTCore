package org.leodreamer.sftcore.integration.ae2.logic;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.integration.ae2.feature.HackyContainerGroupProxy;
import org.leodreamer.sftcore.integration.ae2.feature.ISendToGTMachine;
import org.leodreamer.sftcore.util.GTMachineUtils;

import com.gregtechceu.gtceu.api.block.IMachineBlock;
import com.gregtechceu.gtceu.api.machine.MetaMachine;

import net.minecraft.world.level.block.Block;

import appeng.api.networking.IGridNode;
import appeng.helpers.patternprovider.PatternContainer;

import java.util.Optional;

public class GTTransferLogic {

    public static Optional<AvailableGTRow> tryBuild(
        PatternContainer container, IGridNode containerNode, ISendToGTMachine.RecipeInfo recipeInfo
    ) {
        var group = container.getTerminalGroup();
        var item = group.icon();
        if (item == null) return Optional.empty();

        var block = Block.byItem(item.getItem());
        if (block instanceof IMachineBlock machineBlock) {
            var type = recipeInfo.type();
            if (GTMachineUtils.supportRecipe(machineBlock.getDefinition(), type)) {

                // more precisely...
                var hacky = HackyContainerGroupProxy.of(group);
                var pos = hacky.getBlockBos();
                if (pos != null) {
                    var machine = MetaMachine.getMachine(containerNode.getLevel(), pos);
                    if (
                        machine != null &&
                            GTMachineUtils.guessRecipe(machine, type) == GTMachineUtils.GuessResult.MISMATCH
                    ) {
                        return Optional.empty();
                    }
                }

                // this machine is ok
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
