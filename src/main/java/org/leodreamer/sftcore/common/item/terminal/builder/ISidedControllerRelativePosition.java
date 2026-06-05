package org.leodreamer.sftcore.common.item.terminal.builder;

import org.leodreamer.sftcore.common.item.terminal.api.BuildContext;
import org.leodreamer.sftcore.common.item.terminal.api.BuildDimensions;
import org.leodreamer.sftcore.common.item.terminal.api.RelativeBuildPos;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * For controller-based multiblocks whose controller side can be inferred from block facing.
 */
public interface ISidedControllerRelativePosition extends IControllerRelativePosition {

    default Direction controllerFacing(BuildContext ctx) {
        var state = ctx.level().getBlockState(ctx.clicked());

        if (!state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            throw new IllegalStateException("Clicked controller has no horizontal facing property: " + state);
        }

        return state.getValue(BlockStateProperties.HORIZONTAL_FACING);
    }

    /**
     * Convert block facing to the structure side where the controller is located.
     * Default assumes the controller front faces outward.
     */
    default Direction controllerSideFromFacing(Direction facing) {
        return facing;
    }

    /**
     * How much the controller is placed above the bottom.
     */
    int controllerY(CompoundTag terminalTag, BuildDimensions dimensions);

    /**
     * How much the controller is placed away from the side of the structure.
     */
    int controllerSideOffset(CompoundTag terminalTag, BuildDimensions dimensions);

    @Override
    default RelativeBuildPos controllerInDimension(
        BuildContext ctx,
        CompoundTag terminalTag,
        BuildDimensions dimensions
    ) {
        var side = controllerSideFromFacing(controllerFacing(ctx));
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
}
