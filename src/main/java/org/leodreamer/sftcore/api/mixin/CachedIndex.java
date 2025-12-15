package org.leodreamer.sftcore.api.mixin;

import net.minecraft.server.packs.PackType;

import java.io.File;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class CachedIndex {
    public long lastModified;
    public long size;

    public Map<PackType, Map<String, List<String>>> index = new EnumMap<>(PackType.class);

    public boolean isValid(File file) {
        return file.lastModified() == lastModified && file.length() == size;
    }
}