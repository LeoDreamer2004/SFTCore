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

    public BlockPos toWorld(BlockPos origin) {
        return origin.offset(x, y, z);
    }
}
