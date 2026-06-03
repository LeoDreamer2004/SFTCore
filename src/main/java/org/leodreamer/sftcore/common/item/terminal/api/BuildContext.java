package org.leodreamer.sftcore.common.item.terminal.api;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public record BuildContext(
    ServerLevel level,
    Player player,
    BlockPos origin,
    ItemStack terminalStack
) {}
