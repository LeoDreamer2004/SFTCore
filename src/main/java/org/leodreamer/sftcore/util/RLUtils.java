package org.leodreamer.sftcore.util;

import org.leodreamer.sftcore.SFTCore;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;

import org.jetbrains.annotations.Nullable;

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

    public static @Nullable Fluid getFluidByName(String name) {
        return getFluidByRL(ResourceLocation.parse(name));
    }

    public static @Nullable Fluid getFluidById(String modId, String path) {
        return getFluidByRL(ResourceLocation.fromNamespaceAndPath(modId, path));
    }

    public static @Nullable Fluid getFluidByRL(ResourceLocation rl) {
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(rl);
        if (fluid == null) {
            SFTCore.LOGGER.warn("Could not find fluid with: {}", rl);
        }
        return fluid;
    }
}
