package org.leodreamer.sftcore.common.advancement.trigger;

import com.gregtechceu.gtceu.api.machine.MetaMachine;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

public final class TriggerUtils {

    public static final double DETECT_PLAYER_RANGE = 128.0D;

    private TriggerUtils() {}

    /**
     * Find the nearest player to the given position. The search range is defined by DETECT_PLAYER_RANGE.
     */
    public static @Nullable Player findNearestPlayer(Level level, BlockPos pos) {
        return level.getNearestPlayer(
            pos.getX() + 0.5D,
            pos.getY() + 0.5D,
            pos.getZ() + 0.5D,
            DETECT_PLAYER_RANGE,
            false
        );
    }

    /**
     * Find the owner of the machine, if the owner is not online or in spectator mode, find the nearest player to the
     * machine.
     * 
     * @param machine The GT Machine
     * @return Player found. Null if the world is client side or player not found
     */
    public static @Nullable ServerPlayer findOwnerOrNearestPlayer(MetaMachine machine) {
        if (!(machine.getLevel() instanceof ServerLevel level)) {
            return null;
        }

        var ownerUUID = machine.getOwnerUUID();
        if (ownerUUID != null) {
            // return the player, although null when the player is offline
            return level.getServer().getPlayerList().getPlayer(ownerUUID);
        }

        var nearest = findNearestPlayer(level, machine.getBlockPos());
        return nearest instanceof ServerPlayer serverPlayer ? serverPlayer : null;
    }
}
