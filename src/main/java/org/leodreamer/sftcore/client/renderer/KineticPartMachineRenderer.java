package org.leodreamer.sftcore.client.renderer;

import org.leodreamer.sftcore.common.machine.multiblock.part.KineticInputPartMachine;

import com.gregtechceu.gtceu.client.renderer.BlockEntityWithBERModelRenderer;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class KineticPartMachineRenderer implements BlockEntityRenderer<BlockEntity> {

    private static final float SHAFT_LENGTH_SCALE = 15.0F / 16.0F;

    private final BlockEntityWithBERModelRenderer<BlockEntity> machineRenderer;

    public KineticPartMachineRenderer(BlockEntityRendererProvider.Context context) {
        this.machineRenderer = new BlockEntityWithBERModelRenderer<>(context);
    }

    @Override
    public void render(
        BlockEntity be,
        float partialTick,
        PoseStack poseStack,
        MultiBufferSource bufferSource,
        int packedLight,
        int packedOverlay
    ) {
        machineRenderer.render(be, partialTick, poseStack, bufferSource, packedLight, packedOverlay);

        if (!(be instanceof KineticInputPartMachine machine)) {
            return;
        }

        var axis = machine.getRotationAxis();

        var shaftState = AllBlocks.SHAFT.getDefaultState()
            .setValue(BlockStateProperties.AXIS, axis);

        var shaft = CachedBuffers.block(
            KineticBlockEntityRenderer.KINETIC_BLOCK,
            shaftState
        );

        float angle = getShaftAngle(machine, shaftState, axis);

        var buffer = bufferSource.getBuffer(RenderType.cutoutMipped());

        poseStack.pushPose();
        scaleShaftFromInputFace(poseStack, machine.getFrontFacing());
        shaft.light(packedLight)
            .rotateCentered(angle, Direction.get(AxisDirection.POSITIVE, axis))
            .renderInto(poseStack, buffer);
        poseStack.popPose();
    }

    private static float getShaftAngle(KineticInputPartMachine machine, BlockState shaftState, Axis axis) {
        float time = AnimationTickHolder.getRenderTime(machine.getLevel());
        float offset = KineticBlockEntityVisual.rotationOffset(shaftState, axis, machine.getBlockPos());
        float angle = (time * machine.getShaftSpeed() * 3.0F / 10.0F + offset) % 360.0F;

        return angle / 180.0F * (float) Math.PI;
    }

    // Scale the machine to avoid z-fighting
    private static void scaleShaftFromInputFace(PoseStack poseStack, Direction inputFace) {
        switch (inputFace) {
            case EAST -> {
                poseStack.translate(1.0F, 0.0F, 0.0F);
                poseStack.scale(SHAFT_LENGTH_SCALE, 1.0F, 1.0F);
                poseStack.translate(-1.0F, 0.0F, 0.0F);
            }
            case WEST -> poseStack.scale(SHAFT_LENGTH_SCALE, 1.0F, 1.0F);
            case UP -> {
                poseStack.translate(0.0F, 1.0F, 0.0F);
                poseStack.scale(1.0F, SHAFT_LENGTH_SCALE, 1.0F);
                poseStack.translate(0.0F, -1.0F, 0.0F);
            }
            case DOWN -> poseStack.scale(1.0F, SHAFT_LENGTH_SCALE, 1.0F);
            case SOUTH -> {
                poseStack.translate(0.0F, 0.0F, 1.0F);
                poseStack.scale(1.0F, 1.0F, SHAFT_LENGTH_SCALE);
                poseStack.translate(0.0F, 0.0F, -1.0F);
            }
            case NORTH -> poseStack.scale(1.0F, 1.0F, SHAFT_LENGTH_SCALE);
        }
    }
}
