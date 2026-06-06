package org.leodreamer.sftcore.common.item.terminal.builder;

import net.minecraft.nbt.CompoundTag;
import org.leodreamer.sftcore.common.item.terminal.api.*;

/**
 * Helper builder for cube-shaped multiblocks, where the position of each block determines its role in the structure.
 */
public interface ICubeShapedBuilder extends IMekMultiblockBuilder {

    enum Part {
        CORNER,
        EDGE,
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

        return switch (borders) {
            case 3 -> Part.CORNER;
            case 2 -> Part.EDGE;
            case 1 -> Part.FACE;
            default -> Part.INNER;
        };
    }

    @Override
    default PlacementCandidate candidateFor(
        CompoundTag terminalTag, BuildDimensions dimensions, RelativeBuildPos pos, InventorySnapshot inventory
    ) {
        return candidateForPart(terminalTag, partAt(dimensions, pos), pos, inventory);
    }
}
