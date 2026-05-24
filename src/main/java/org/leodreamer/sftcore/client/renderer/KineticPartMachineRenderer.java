package org.leodreamer.sftcore.client.renderer;

import org.leodreamer.sftcore.common.machine.multiblock.part.KineticPartMachine;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class KineticPartMachineRenderer implements BlockEntityRenderer<BlockEntity> {

    public KineticPartMachineRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(
        BlockEntity be,
        float partialTick,
        PoseStack poseStack,
        MultiBufferSource bufferSource,
        int packedLight,
        int packedOverlay
    ) {
        if (!(be instanceof KineticPartMachine machine)) {
            return;
        }

        float speed = machine.getShaftSpeed();
        if (speed == 0) {
            return;
        }

        var axis = machine.getFrontFacing().getAxis();

        var shaftState = AllBlocks.SHAFT.getDefaultState()
            .setValue(BlockStateProperties.AXIS, axis);

        var shaft = CachedBuffers.block(
            KineticBlockEntityRenderer.KINETIC_BLOCK,
            shaftState
        );

        float angle = getShaftAngle(machine, axis, speed);

        var buffer = bufferSource.getBuffer(RenderType.cutoutMipped());

        shaft.light(packedLight)
            .rotateCentered(angle, Direction.get(AxisDirection.POSITIVE, axis))
            .renderInto(poseStack, buffer);
    }

    private static float getShaftAngle(KineticPartMachine machine, Axis axis, float speed) {
        float time = AnimationTickHolder.getRenderTime(machine.getLevel());

        float angle = (time * speed * 3.0F / 10.0F) % 360.0F;

        int offset = switch (axis) {
            case X -> machine.getBlockPos().getX();
            case Y -> machine.getBlockPos().getY();
            case Z -> machine.getBlockPos().getZ();
        };

        angle += offset * 11.25F;

        return angle / 180.0F * (float) Math.PI;
    }
}
