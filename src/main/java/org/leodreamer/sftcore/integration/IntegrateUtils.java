package org.leodreamer.sftcore.integration;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import org.leodreamer.sftcore.SFTCore;

public class IntegrateUtils {
    public static @Nullable Item getItemById(String modId, String path) {
        Item item = ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath(modId, path));
        if (item == null) {
            SFTCore.LOGGER.warn("Could not find item with path: {}:{}", modId, path);
        }
        return item;
    }
}
