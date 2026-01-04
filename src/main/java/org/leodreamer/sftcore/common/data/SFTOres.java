package org.leodreamer.sftcore.common.data;

import org.leodreamer.sftcore.SFTCore;

import com.gregtechceu.gtceu.api.registry.GTRegistries;

import net.minecraft.resources.ResourceLocation;

import java.util.Set;

public final class SFTOres {

    public static void init() {
        // Remove all the veins
        Set<ResourceLocation> keys = Set.copyOf(GTRegistries.ORE_VEINS.keys());
        keys.forEach(GTRegistries.ORE_VEINS::remove);
        SFTCore.LOGGER.info("Removed All GregTech Ore Vein!");
    }
}
