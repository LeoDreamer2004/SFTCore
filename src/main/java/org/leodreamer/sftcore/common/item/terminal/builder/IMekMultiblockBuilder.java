package org.leodreamer.sftcore.common.item.terminal.builder;

import org.leodreamer.sftcore.common.item.terminal.api.*;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Basic interface for builder of mek multiblocks
 */
public interface IMekMultiblockBuilder {

    ResourceLocation id();

    Block clickAt();

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

    /**
     * Candidate used by the GUI preview.
     * Default behavior is identical to actual construction, except that the
     * inventory is treated as creative. Individual builders may override this
     * method if they need a more representative preview arrangement.
     */
    default PlacementCandidate previewCandidateFor(
        CompoundTag terminalTag,
        BuildDimensions dimensions,
        RelativeBuildPos pos,
        InventorySnapshot creativeInventory
    ) {
        return candidateFor(
            terminalTag,
            dimensions,
            pos,
            creativeInventory
        );
    }

    /**
     * Generate a canonical preview using relative coordinates.
     */
    default Map<BlockPos, BlockState> previewStates(CompoundTag terminalTag) {
        var dimensions = dimensions(terminalTag);
        var inventory = InventorySnapshot.creative();
        var states = new LinkedHashMap<BlockPos, BlockState>();

        for (var pos : positions(terminalTag, dimensions)) {
            var candidate = previewCandidateFor(
                terminalTag,
                dimensions,
                pos,
                inventory
            );

            if (candidate == null || candidate.isAir()) {
                continue;
            }

            states.put(
                new BlockPos(pos.x(), pos.y(), pos.z()),
                candidate.state()
            );
        }

        return states;
    }
}
