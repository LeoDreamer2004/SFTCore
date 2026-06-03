package org.leodreamer.sftcore.common.item.terminal.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;

public record Placement(
    BlockPos pos,
    BlockState state,
    Item requiredItem,
    PlacementRole role,
    boolean consumeItem
) {}
