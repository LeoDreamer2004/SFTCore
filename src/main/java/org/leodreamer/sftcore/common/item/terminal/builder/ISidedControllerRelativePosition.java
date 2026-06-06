package org.leodreamer.sftcore.common.item.terminal.builder;

import org.leodreamer.sftcore.common.item.terminal.api.*;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * For controller-based multiblocks whose controller side can be inferred
 * from the horizontal facing of the clicked controller.
 */
public interface ISidedControllerRelativePosition extends IControllerRelativePosition {

    /**
     * Facing of the controller already placed in the real world.
     */
    default Direction controllerFacing(BuildContext ctx) {
        var state = ctx.level().getBlockState(ctx.clicked());

        if (!state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            throw new IllegalStateException(
                "Clicked controller has no horizontal facing property: " + state
            );
        }

        return state.getValue(BlockStateProperties.HORIZONTAL_FACING);
    }

    /**
     * Canonical facing used only by the GUI preview.
     */
    default Direction previewControllerFacing() {
        return Direction.NORTH;
    }

    /**
     * Convert block facing to the structure side where the controller is
     * located.
     * Default assumes the controller front faces outward.
     */
    default Direction controllerSideFromFacing(Direction facing) {
        return facing;
    }

    /**
     * How much the controller is placed above the bottom.
     */
    int controllerY(
        CompoundTag terminalTag,
        BuildDimensions dimensions
    );

    /**
     * How much the controller is placed away from the horizontal edge.
     */
    int controllerSideOffset(
        CompoundTag terminalTag,
        BuildDimensions dimensions
    );

    /**
     * Shared coordinate calculation for both:
     * - actual construction, using the clicked controller facing;
     * - preview rendering, using a canonical facing.
     */
    default RelativeBuildPos controllerInDimension(
        Direction facing,
        CompoundTag terminalTag,
        BuildDimensions dimensions
    ) {
        var side = controllerSideFromFacing(facing);
        var y = controllerY(terminalTag, dimensions);
        var offset = controllerSideOffset(terminalTag, dimensions);

        return switch (side) {
            case NORTH -> new RelativeBuildPos(offset, y, 0);
            case SOUTH -> new RelativeBuildPos(offset, y, dimensions.depth() - 1);
            case WEST -> new RelativeBuildPos(0, y, offset);
            case EAST -> new RelativeBuildPos(dimensions.width() - 1, y, offset);
            default -> throw new IllegalStateException("Controller side must be horizontal: " + side);
        };
    }

    /**
     * Actual construction path.
     */
    @Override
    default RelativeBuildPos controllerInDimension(
        BuildContext ctx,
        CompoundTag terminalTag,
        BuildDimensions dimensions
    ) {
        return controllerInDimension(
            controllerFacing(ctx),
            terminalTag,
            dimensions
        );
    }

    /**
     * Construct the controller state displayed by the GUI preview.
     * This is deliberately a separate hook. Future controllers may need
     * additional block-state properties besides HORIZONTAL_FACING.
     */
    default BlockState previewControllerState(Direction facing) {
        var state = controllerBlock().defaultBlockState();

        if (!state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            throw new IllegalStateException(
                "Preview controller has no horizontal facing property: " +
                    state
            );
        }

        return state.setValue(
            BlockStateProperties.HORIZONTAL_FACING,
            facing
        );
    }

    /**
     * Replace the ordinary casing / valve candidate at the canonical
     * controller coordinate with a correctly oriented controller.
     */
    @Override
    default PlacementCandidate previewCandidateFor(
        CompoundTag terminalTag,
        BuildDimensions dimensions,
        RelativeBuildPos pos,
        InventorySnapshot creativeInventory
    ) {
        var facing = previewControllerFacing();

        var controllerPos = controllerInDimension(
            facing,
            terminalTag,
            dimensions
        );

        if (pos.equals(controllerPos)) {
            return PlacementCandidate
                .simple(controllerBlock())
                .state(previewControllerState(facing));
        }

        return IControllerRelativePosition.super.previewCandidateFor(
            terminalTag,
            dimensions,
            pos,
            creativeInventory
        );
    }
}
