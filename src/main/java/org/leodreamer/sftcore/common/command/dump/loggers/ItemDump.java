package org.leodreamer.sftcore.common.command.dump.loggers;

import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class ItemDump implements IDump {

    @Override
    public String getTypeName() {
        return "Item";
    }

    @Override
    public Map<String, List<String>> getIdentifierMap() {
        Map<String, List<String>> idMap = new HashMap<>();
        ForgeRegistries.ITEMS
            .getKeys()
            .forEach(
                location -> {
                    String namespace = location.getNamespace();
                    if (!idMap.containsKey(namespace)) idMap.put(namespace, new LinkedList<>());
                    idMap.get(namespace).add(location.getPath());
                }
            );
        return idMap;
    }

    @Override
    public Map<String, List<String>> getTagMap() {
        Map<String, List<String>> tagMap = new HashMap<>();
        Objects.requireNonNull(ForgeRegistries.ITEMS.tags())
            .getTagNames()
            .map(TagKey::location)
            .forEach(
                location -> {
                    String namespace = "#" + location.getNamespace();
                    if (!tagMap.containsKey(namespace)) tagMap.put(namespace, new LinkedList<>());
                    tagMap.get(namespace).add(location.getPath());
                }
            );
        return tagMap;
    }
}
