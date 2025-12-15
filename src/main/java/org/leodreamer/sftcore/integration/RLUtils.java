package org.leodreamer.sftcore.integration;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import org.leodreamer.sftcore.SFTCore;

public class RLUtils {
    public static ResourceLocation getItemRL(ItemLike item) {
        return ForgeRegistries.ITEMS.getKey(item.asItem());
    }

    public static @Nullable Item getItemByName(String name) {
        return getItemByRL(ResourceLocation.parse(name));
    }

    public static @Nullable Item getItemById(String modId, String path) {
        return getItemByRL(ResourceLocation.fromNamespaceAndPath(modId, path));
    }

    public static @Nullable Item getItemByRL(ResourceLocation rl) {
        Item item = ForgeRegistries.ITEMS.getValue(rl);
        if (item == null) {
            SFTCore.LOGGER.warn("Could not find item with: {}", rl);
        }
        return item;
    }
}
