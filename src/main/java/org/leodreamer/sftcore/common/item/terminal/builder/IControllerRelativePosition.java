package org.leodreamer.sftcore.common.item.terminal.builder;

import org.leodreamer.sftcore.common.item.terminal.api.BuildContext;
import org.leodreamer.sftcore.common.item.terminal.api.BuildDimensions;
import org.leodreamer.sftcore.common.item.terminal.api.RelativeBuildPos;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

/**
 * For multiblocks whose build anchor is the clicked controller.
 */
public interface IControllerRelativePosition extends IMekMultiblockBuilder {

    /**
     * Controller position inside the dimension-space structure.
     */
    RelativeBuildPos controllerInDimension(
        CompoundTag terminalTag,
        BuildDimensions dimensions
    );

    @Override
    default BlockPos origin(BuildContext ctx, CompoundTag terminalTag, BuildDimensions dimensions) {
        return controllerInDimension(terminalTag, dimensions).minusTo(ctx.clicked());
    }
}
