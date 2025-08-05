package org.leodreamer.sftcore.common.utils.dump.loggers;

import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class ItemDump implements BaseDump {
    @Override
    public String getTypeName() {
        return "Item";
    }

    @Override
    public Map<String, List<String>> getIdentifierMap() {
        Map<String, List<String>> id_map = new HashMap<>();
        ForgeRegistries.ITEMS.getKeys()
                .forEach(location -> {
                    String namespace = location.getNamespace();
                    if (!id_map.containsKey(namespace))
                        id_map.put(namespace, new LinkedList<>());
                    id_map.get(namespace).add(location.getPath());
                });
        return id_map;
    }

    @Override
    public Map<String, List<String>> getTagMap() {
        Map<String, List<String>> tag_map = new HashMap<>();
        Objects.requireNonNull(ForgeRegistries.ITEMS.tags()).getTagNames()
                .map(TagKey::location)
                .forEach(location -> {
                    String namespace = "#" + location.getNamespace();
                    if (!tag_map.containsKey(namespace))
                        tag_map.put(namespace, new LinkedList<>());
                    tag_map.get(namespace).add(location.getPath());
                });
        return tag_map;
    }
}
