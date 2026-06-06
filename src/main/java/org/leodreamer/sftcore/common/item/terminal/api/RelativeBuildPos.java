package org.leodreamer.sftcore.common.item.terminal.api;

import net.minecraft.core.BlockPos;

public record RelativeBuildPos(
    int x,
    int y,
    int z
) {

    public boolean isOrigin() {
        return x == 0 && y == 0 && z == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof RelativeBuildPos that) {
            return x == that.x && y == that.y && z == that.z;
        }
        return false;
    }

    public BlockPos addTo(BlockPos origin) {
        return origin.offset(x, y, z);
    }

    public BlockPos minusTo(BlockPos origin) {
        return origin.offset(-x, -y, -z);
    }
}
