package org.leodreamer.sftcore.common.item.terminal.builder;

import org.leodreamer.sftcore.common.item.terminal.api.BuildContext;
import org.leodreamer.sftcore.common.item.terminal.api.BuildDimensions;
import org.leodreamer.sftcore.common.item.terminal.api.RelativeBuildPos;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;

/**
 * For multiblocks whose build anchor is the clicked controller.
 */
public interface IControllerRelativePosition extends IMekMultiblockBuilder {

    /**
     * The controller block used both for click validation and GUI preview.
     */
    Block controllerBlock();

    /**
     * Controller builders always start from their controller.
     */
    @Override
    default Block clickAt() {
        return controllerBlock();
    }

    /**
     * Controller position inside the dimension-space structure.
     */
    RelativeBuildPos controllerInDimension(
        BuildContext ctx,
        CompoundTag terminalTag,
        BuildDimensions dimensions
    );

    @Override
    default BlockPos origin(BuildContext ctx, CompoundTag terminalTag, BuildDimensions dimensions) {
        return controllerInDimension(ctx, terminalTag, dimensions).minusTo(ctx.clicked());
    }
}
