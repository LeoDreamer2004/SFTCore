package org.leodreamer.sftcore.common.item.terminal.builder;

import org.leodreamer.sftcore.common.item.terminal.api.BuildDimensions;
import org.leodreamer.sftcore.common.item.terminal.api.InventorySnapshot;
import org.leodreamer.sftcore.common.item.terminal.api.PlacementCandidate;
import org.leodreamer.sftcore.common.item.terminal.api.RelativeBuildPos;

import net.minecraft.nbt.CompoundTag;

/**
 * Helper builder for cube-shaped multiblocks, where the position of each block determines its role in the structure.
 */
public interface ICubeShapedBuilder extends IMekMultiblockBuilder {

    enum Part {
        FRAME,
        FACE,
        INNER
    }

    PlacementCandidate candidateForPart(
        CompoundTag terminalTag,
        Part part,
        RelativeBuildPos pos,
        InventorySnapshot inventory
    );

    default Part partAt(
        BuildDimensions dimensions,
        RelativeBuildPos pos
    ) {
        int borders = 0;

        if (pos.x() == 0 || pos.x() == dimensions.width() - 1) {
            borders++;
        }
        if (pos.y() == 0 || pos.y() == dimensions.height() - 1) {
            borders++;
        }
        if (pos.z() == 0 || pos.z() == dimensions.depth() - 1) {
            borders++;
        }

        if (borders >= 2) {
            return Part.FRAME;
        }
        if (borders == 1) {
            return Part.FACE;
        }
        return Part.INNER;
    }

    @Override
    default PlacementCandidate candidateFor(
        CompoundTag terminalTag, BuildDimensions dimensions, RelativeBuildPos pos, InventorySnapshot inventory
    ) {
        return candidateForPart(terminalTag, partAt(dimensions, pos), pos, inventory);
    }
}
