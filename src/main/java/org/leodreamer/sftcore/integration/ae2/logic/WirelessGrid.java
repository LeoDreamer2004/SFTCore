package org.leodreamer.sftcore.integration.ae2.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Wireless Grid Data.
 * This will be serialized to NBT for saving/loading purposes.
 */
public record WirelessGrid(
    ResourceKey<Level> level,
    BlockPos center,
    Set<BlockPos> members
) {

    public static final Codec<WirelessGrid> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            ResourceKey.codec(Registries.DIMENSION)
                .fieldOf("level")
                .forGetter(WirelessGrid::level),

            BlockPos.CODEC
                .fieldOf("center")
                .forGetter(WirelessGrid::center),

            BlockPos.CODEC
                .listOf()
                .fieldOf("members")
                .forGetter(WirelessGrid::orderedMembers)
        ).apply(instance, WirelessGrid::new)
    );

    public WirelessGrid {
        members = new ObjectOpenHashSet<>(members);
    }

    public WirelessGrid(ResourceKey<Level> level, BlockPos center, List<BlockPos> members) {
        this(level, center, new ObjectOpenHashSet<>(members));
    }

    public WirelessGrid(ResourceKey<Level> level, BlockPos center) {
        this(level, center, new ObjectOpenHashSet<>());
    }

    public static CompoundTag encode(WirelessGrid grid) {
        Tag tag = CODEC.encodeStart(NbtOps.INSTANCE, grid)
            .getOrThrow(false, error -> {});

        if (tag instanceof CompoundTag compoundTag) {
            return compoundTag;
        }

        throw new IllegalStateException("WirelessGrid CODEC did not encode to CompoundTag: " + tag);
    }

    public static WirelessGrid decode(CompoundTag tag) {
        return CODEC.parse(NbtOps.INSTANCE, tag)
            .getOrThrow(false, error -> {});
    }

    private List<BlockPos> orderedMembers() {
        return members.stream()
            .sorted(Comparator.comparingLong(BlockPos::asLong))
            .toList();
    }
}
