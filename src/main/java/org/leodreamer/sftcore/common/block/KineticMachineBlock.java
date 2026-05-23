package org.leodreamer.sftcore.common.block;

import org.leodreamer.sftcore.api.kinetics.IKineticStressConsumer;

import com.gregtechceu.gtceu.api.block.MetaMachineBlock;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

import com.simibubi.create.content.kinetics.base.IRotate;

public class KineticMachineBlock extends MetaMachineBlock implements IRotate {

    public KineticMachineBlock(Properties properties, MachineDefinition definition) {
        super(properties, definition);
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        var machine = MetaMachine.getMachine(world, pos);

        if (machine instanceof IKineticStressConsumer consumer) {
            return consumer.sftcore$hasShaftTowards(face);
        }

        return face.getAxis() == getRotationAxis(state);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return getFrontFacing(state).getAxis();
    }

    @Override
    public boolean showCapacityWithAnnotation() {
        return true;
    }
}
