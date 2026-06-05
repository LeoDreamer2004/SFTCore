package org.leodreamer.sftcore.common.item.terminal.builder;

import org.leodreamer.sftcore.common.item.terminal.api.BuildContext;
import org.leodreamer.sftcore.common.item.terminal.api.BuildDimensions;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

/**
 * A rectangular box with no controller.
 */
public interface ISimpleRelativePosition extends IMekMultiblockBuilder {

    @Override
    default BlockPos origin(BuildContext ctx, CompoundTag terminalTag, BuildDimensions dimensions) {
        // The origin is exactly at the clicked block
        return ctx.clicked();
    };
}
