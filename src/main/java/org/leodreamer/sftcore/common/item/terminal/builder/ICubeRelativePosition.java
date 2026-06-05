package org.leodreamer.sftcore.common.item.terminal.builder;

import org.leodreamer.sftcore.common.item.terminal.api.BuildContext;
import org.leodreamer.sftcore.common.item.terminal.api.BuildDimensions;
import org.leodreamer.sftcore.common.item.terminal.api.RelativeBuildPos;

import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;

/**
 * A rectangular box from origin toward +X +Y +Z.
 */
public interface ICubeRelativePosition extends IMekMultiblockBuilder {

    @Override
    default Iterable<RelativeBuildPos> positions(
        BuildContext ctx,
        CompoundTag terminalTag,
        BuildDimensions dimensions
    ) {
        var positions = new ArrayList<RelativeBuildPos>(dimensions.volume());

        for (int x = 0; x < dimensions.width(); x++) {
            for (int y = 0; y < dimensions.height(); y++) {
                for (int z = 0; z < dimensions.depth(); z++) {
                    positions.add(new RelativeBuildPos(x, y, z));
                }
            }
        }

        return positions;
    }
}
