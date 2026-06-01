package org.leodreamer.sftcore.common.item.wildcard;

import org.leodreamer.sftcore.common.item.wildcard.feature.IWildcardFilterComponent;
import org.leodreamer.sftcore.common.item.wildcard.feature.IWildcardIOComponent;
import org.leodreamer.sftcore.common.item.wildcard.impl.*;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class WildcardComponentCodecs {

    private WildcardComponentCodecs() {
    }

    private static final Map<String, Entry<? extends IWildcardIOComponent>> IO = new Object2ObjectOpenHashMap<>();
    private static final Map<String, Entry<? extends IWildcardFilterComponent>> FILTER = new Object2ObjectOpenHashMap<>();

    static {
        register(IO, "simple", SimpleIOComponent.class, SimpleIOComponent.CODEC);
        register(IO, "tag_prefix", TagIOComponent.class, TagIOComponent.CODEC);

        register(FILTER, "simple", SimpleFilterComponent.class, SimpleFilterComponent.CODEC);
        register(FILTER, "flag", FlagFilterComponent.class, FlagFilterComponent.CODEC);
        register(FILTER, "property", PropertyFilterComponent.class, PropertyFilterComponent.CODEC);
    }

    public static List<IWildcardIOComponent> readIO(CompoundTag root, WildcardPatternLogic.IO io) {
        return read(IO, root, io.key);
    }

    public static void writeIO(
        CompoundTag root, WildcardPatternLogic.IO io, List<? extends IWildcardIOComponent> components
    ) {
        write(IO, root, io.key, components);
    }

    public static List<IWildcardFilterComponent> readFilters(CompoundTag root) {
        return read(FILTER, root, "filter");
    }

    public static void writeFilters(CompoundTag root, List<? extends IWildcardFilterComponent> components) {
        write(FILTER, root, "filter", components);
    }

    private static <B, T extends B> void register(
        Map<String, Entry<? extends B>> map,
        String id,
        Class<T> type,
        Codec<T> codec
    ) {
        map.put(id, new Entry<>(id, type, codec));
    }

    private static <T> List<T> read(
        Map<String, Entry<? extends T>> registry,
        CompoundTag root,
        String key
    ) {
        if (!(root.get(key) instanceof ListTag list)) {
            return List.of();
        }

        var result = new ArrayList<T>();

        for (var rawEntry : list) {
            if (!(rawEntry instanceof CompoundTag entry)) {
                continue;
            }

            String id = entry.getString("id");
            var codec = registry.get(id);

            if (codec == null) {
                throw new IllegalStateException("Unknown wildcard component id: " + id);
            }

            result.add(codec.decode(entry.get("value")));
        }

        return List.copyOf(result);
    }

    private static <T> void write(
        Map<String, Entry<? extends T>> registry,
        CompoundTag root,
        String key,
        List<? extends T> components
    ) {
        var list = new ListTag();

        for (T component : components) {
            var entryCodec = find(registry, component);

            var entry = new CompoundTag();
            entry.putString("id", entryCodec.id());
            entry.put("value", entryCodec.encode(component));

            list.add(entry);
        }

        root.put(key, list);
    }

    @SuppressWarnings("unchecked")
    private static <T> Entry<T> find(Map<String, Entry<? extends T>> registry, T component) {
        for (var entry : registry.values()) {
            if (entry.type().isInstance(component)) {
                return (Entry<T>) entry;
            }
        }

        throw new IllegalStateException("No wildcard codec registered for " + component.getClass().getName());
    }

    private record Entry<T>(String id, Class<T> type, Codec<T> codec) {

        Tag encode(T value) {
            return codec.encodeStart(NbtOps.INSTANCE, value)
                .getOrThrow(false, error -> {
                });
        }

        T decode(Tag tag) {
            return codec.parse(NbtOps.INSTANCE, tag)
                .getOrThrow(false, error -> {
                });
        }
    }
}
