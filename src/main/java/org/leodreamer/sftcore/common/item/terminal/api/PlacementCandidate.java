package org.leodreamer.sftcore.common.item.terminal.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import mekanism.api.providers.IBlockProvider;

import java.util.Arrays;

import javax.annotation.Nullable;

@Accessors(fluent = true, chain = true)
public class PlacementCandidate {

    @Getter
    @Setter
    private @Nullable BlockState state;
    @Getter
    @Setter
    private @Nullable Item item;
    @Getter
    @Setter
    private boolean consumeItem = true;

    private static final PlacementCandidate AIR = new PlacementCandidate();

    public static PlacementCandidate air() {
        return AIR;
    }

    public static PlacementCandidate simple(IBlockProvider block) {
        return simple(block.getBlock());
    }

    public static PlacementCandidate simple(Block block) {
        return new PlacementCandidate().state(block.defaultBlockState()).item(block.asItem());
    }

    public static PlacementCandidate anyOf(InventorySnapshot snapshot, IBlockProvider... candidates) {
        return anyOf(snapshot, Arrays.stream(candidates).map(IBlockProvider::getBlock).toArray(Block[]::new));
    }

    public static PlacementCandidate anyOf(InventorySnapshot snapshot, Block... candidates) {
        return anyOf(
            snapshot, Arrays.stream(candidates).map(PlacementCandidate::simple).toArray(PlacementCandidate[]::new)
        );
    }

    public static PlacementCandidate anyOf(InventorySnapshot snapshot, PlacementCandidate... candidates) {
        for (var candidate : candidates) {
            if (!candidate.isAir() && snapshot.count(candidate.item()) > 0) {
                snapshot.takeVirtual(candidate.item());
                return candidate;
            }
        }
        return air();
    }

    public boolean isAir() {
        return state == null;
    }

    public Placement toPlacement(BlockPos pos) {
        if (isAir()) {
            throw new IllegalStateException("Air candidate cannot be converted to placement");
        }
        return new Placement(pos, state, item, consumeItem);
    }
}
