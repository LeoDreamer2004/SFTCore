package org.leodreamer.sftcore.common.item.terminal.builder;

import org.leodreamer.sftcore.common.item.terminal.api.*;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;

public interface IMekMultiblockBuilder {

    ResourceLocation id();

    boolean canStart(BuildContext ctx);

    BuildDimensions dimensions(CompoundTag rootTag);

    /**
     * The world pos of the origin block,
     * which means the coordinate with smallest X, Y, Z
     */
    BlockPos origin(BuildContext ctx, CompoundTag terminalTag, BuildDimensions dimensions);

    /**
     * Relative positions to the origin
     * Default: origin ~ origin + (X,Y,Z)
     */
    default Iterable<RelativeBuildPos> positions(
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

    /**
     * Get a concrete block candidate.
     * Return {@link PlacementCandidate#air()} if this position should be left empty.
     * The {@link InventorySnapshot} is virtual. Builders may consume it to avoid selecting
     * more variable candidates than the player actually has.
     */
    PlacementCandidate candidateFor(
        CompoundTag terminalTag,
        BuildDimensions dimensions,
        RelativeBuildPos pos,
        InventorySnapshot inventory
    );

    default BuildPlan createPlan(BuildContext ctx, CompoundTag terminalTag) {
        var dimensions = dimensions(terminalTag);
        var inventory = InventorySnapshot.of(ctx.player());
        var plan = new BuildPlan();

        var origin = origin(ctx, terminalTag, dimensions);
        for (var pos : positions(terminalTag, dimensions)) {
            var worldPos = pos.addTo(origin);
            if (!ctx.level().getBlockState(worldPos).isAir()) {
                continue; // only place blocks on air
            }

            var candidate = candidateFor(terminalTag, dimensions, pos, inventory);
            if (candidate == null || candidate.isAir()) {
                continue;
            }
            plan.add(candidate.toPlacement(worldPos));
        }

        return plan;
    }

    default Component invalidStartMessage() {
        return Component.translatable(BuildReport.INVALID_START);
    }
}
