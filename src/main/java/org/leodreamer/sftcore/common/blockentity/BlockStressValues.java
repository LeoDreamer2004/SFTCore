package org.leodreamer.sftcore.common.blockentity;

import com.gregtechceu.gtceu.api.block.IMachineBlock;
import net.createmod.catnip.data.Couple;
import net.minecraft.world.level.block.Block;
import org.leodreamer.sftcore.api.machine.KineticMachineDefinition;

import javax.annotation.Nullable;

public class BlockStressValues {
    public BlockStressValues() {
        super();
    }

    public static double getImpact(Block block) {
        if (block instanceof IMachineBlock machineBlock &&
                machineBlock.getDefinition() instanceof KineticMachineDefinition definition) {
            if (!definition.isSource()) {
                return definition.getTorque();
            }
        }
        return 0;
    }

    public static double getCapacity(Block block) {
        if (block instanceof IMachineBlock machineBlock &&
                machineBlock.getDefinition() instanceof KineticMachineDefinition definition) {
            if (definition.isSource()) {
                return definition.getTorque();
            }
        }
        return 0;
    }

    public boolean hasImpact(Block block) {
        if (block instanceof IMachineBlock machineBlock &&
                machineBlock.getDefinition() instanceof KineticMachineDefinition definition) {
            return !definition.isSource();
        }
        return false;
    }

    public boolean hasCapacity(Block block) {
        if (block instanceof IMachineBlock machineBlock &&
                machineBlock.getDefinition() instanceof KineticMachineDefinition definition) {
            return definition.isSource();
        }
        return false;
    }

    @Nullable
    public Couple<Integer> getGeneratedRPM(Block block) {
        return null;
    }
}