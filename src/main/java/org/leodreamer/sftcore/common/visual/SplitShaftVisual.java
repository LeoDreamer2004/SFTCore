package org.leodreamer.sftcore.common.visual;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import org.leodreamer.sftcore.api.blockentity.KineticMachineBlockEntity;
import org.leodreamer.sftcore.api.machine.trait.IKineticMachine;

import java.util.ArrayList;
import java.util.function.Consumer;

public class SplitShaftVisual extends KineticBlockEntityVisual<KineticMachineBlockEntity> {

    protected final ArrayList<RotatingInstance> keys;

    public SplitShaftVisual(VisualizationContext context, KineticMachineBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);

        keys = new ArrayList<>(2);

        float speed = blockEntity.getSpeed();

        for (Direction dir : Iterate.directionsInAxis(rotationAxis())) {

            RotatingInstance half = instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial(AllPartialModels.SHAFT_HALF)).createInstance();

            float splitSpeed = speed * (blockEntity.getMetaMachine() instanceof IKineticMachine kineticMachine ?
                    kineticMachine.getRotationSpeedModifier(dir) : 1);
            half.setup(blockEntity, splitSpeed).setPosition(getVisualPosition()).rotateToFace(Direction.SOUTH, dir).setChanged();
            keys.add(half);
        }
    }

    @Override
    public void update(float pt) {
        Block block = blockState.getBlock();
        final Direction.Axis boxAxis = ((IRotate) block).getRotationAxis(blockState);

        Direction[] directions = Iterate.directionsInAxis(boxAxis);

        for (int i : Iterate.zeroAndOne) {
            keys.get(i).setup(blockEntity, blockEntity.getSpeed() * (blockEntity.getMetaMachine() instanceof IKineticMachine kineticMachine ?
                    kineticMachine.getRotationSpeedModifier(directions[i]) : 1)).setChanged();
        }
    }

    @Override
    protected void _delete() {
        keys.forEach(RotatingInstance::delete);
        keys.clear();
    }


    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        keys.forEach(consumer);
    }

    @Override
    public void updateLight(float v) {
        keys.forEach(rotatingInstance -> relight(pos, rotatingInstance));
    }
}