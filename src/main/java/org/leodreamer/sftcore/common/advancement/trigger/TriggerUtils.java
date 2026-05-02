package org.leodreamer.sftcore.common.advancement.trigger;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class TriggerUtils {
    public static final double DETECT_PLAYER_RANGE = 128.0D;

    public static @Nullable Player findNearestPlayer(Level level, BlockPos pos) {
        return level.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), DETECT_PLAYER_RANGE, false);
    }
}

