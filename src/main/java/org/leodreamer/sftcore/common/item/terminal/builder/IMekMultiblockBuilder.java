package org.leodreamer.sftcore.common.item.terminal.builder;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.leodreamer.sftcore.common.item.terminal.api.*;

public interface IMekMultiblockBuilder {

    ResourceLocation id();

    Component title();

    boolean canStart(BuildContext ctx);

    BuildDimensions dimensions(BuildContext ctx, CompoundTag rootTag);

    /**
     * Get a concrete block candidate.
     * Return PlacementCandidate.air() if this position should be left empty.
     * The InventorySnapshot is virtual. Builders may consume it to avoid selecting
     * more variable candidates than the player actually has.
     */
    PlacementCandidate candidateFor(
        CompoundTag terminalTag,
        BuildDimensions dimensions,
        RelativeBuildPos pos,
        InventorySnapshot inventory
    );

    /**
     * Relative positions to place blocks
     */
    Iterable<RelativeBuildPos> positions(
        BuildContext ctx,
        CompoundTag terminalTag,
        BuildDimensions dimensions
    );

    default BuildPlan createPlan(BuildContext ctx, CompoundTag terminalTag) {
        var dimensions = dimensions(ctx, terminalTag);
        var inventory = InventorySnapshot.of(ctx.player());
        var plan = new BuildPlan();

        for (var pos : positions(ctx, terminalTag, dimensions)) {
            var worldPos = pos.toWorld(ctx.origin());
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
