package org.leodreamer.sftcore.common.item.behavior.wildcard;

import org.leodreamer.sftcore.common.item.behavior.wildcard.feature.IWildcardIOComponent;
import org.leodreamer.sftcore.common.item.behavior.wildcard.feature.IWildcardIOSerializer;
import org.leodreamer.sftcore.common.item.behavior.wildcard.impl.SimpleIOComponent;
import org.leodreamer.sftcore.common.item.behavior.wildcard.impl.TagPrefixIOComponent;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class WildcardIOSerializers {

    public static final Map<String, IWildcardIOSerializer> REGISTERED = new Object2ObjectOpenHashMap<>();

    public static final IWildcardIOSerializer SIMPLE = register(SimpleIOComponent.Serializer::new);
    public static final IWildcardIOSerializer TAG_PREFIX = register(TagPrefixIOComponent.Serializer::new);

    public static IWildcardIOSerializer register(Supplier<IWildcardIOSerializer> factory) {
        var serializer = factory.get();
        REGISTERED.put(serializer.key(), serializer);
        return serializer;
    }

    public static List<IWildcardIOComponent> fromNBT(ListTag tag) {
        return tag.stream().map(nbt -> {
            var compound = (CompoundTag) nbt;
            var type = compound.getString("type");
            var serializer = REGISTERED.get(type);
            if (serializer == null) {
                throw new IllegalStateException("Unknown wildcard component type: " + type);
            }
            return serializer.readFromNBT(compound.getCompound("data"));
        }).toList();
    }

    public static ListTag toNBT(List<IWildcardIOComponent> components) {
        var listTag = new ListTag();
        for (var component : components) {
            var serializer = component.getSerializer();
            var compound = new CompoundTag();
            compound.putString("type", serializer.key());
            compound.put("data", serializer.writeToNBT(component));
            listTag.add(compound);
        }
        return listTag;
    }
}
