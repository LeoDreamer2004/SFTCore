package org.leodreamer.sftcore.integration.ae2.logic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.leodreamer.sftcore.api.serialization.ITagCodecSerializable;

import java.util.List;
import java.util.Set;

/**
 * Wireless Grid Data.
 * This will be serialized to NBT for saving/loading purposes.
 */
public record WirelessGrid(ResourceKey<Level> level, BlockPos center, Set<BlockPos> members)
    implements ITagCodecSerializable<WirelessGrid, CompoundTag> {

    public static final Codec<WirelessGrid> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            ResourceKey.codec(Registries.DIMENSION).fieldOf("level").forGetter(WirelessGrid::level),
            BlockPos.CODEC.fieldOf("center").forGetter(WirelessGrid::center),
            BlockPos.CODEC.listOf().fieldOf("members").forGetter((a) -> a.members().stream().toList())
        ).apply(instance, WirelessGrid::new)
    );

    public WirelessGrid(ResourceKey<Level> level, BlockPos center, List<BlockPos> members) {
        this(level, center, new ObjectOpenHashSet<>(members));
    }

    public WirelessGrid(ResourceKey<Level> level, BlockPos center) {
        this(level, center, new ObjectOpenHashSet<>());
    }

    @Override
    public Codec<WirelessGrid> getCodec() {
        return CODEC;
    }

    public static WirelessGrid decode(CompoundTag tag) {
        return ITagCodecSerializable.decodeBy(CODEC, tag);
    }
}
