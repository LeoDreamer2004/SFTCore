package org.leodreamer.sftcore.common.data;

import com.gregtechceu.gtceu.api.registry.GTRegistries;
import net.minecraft.resources.ResourceLocation;
import org.leodreamer.sftcore.SFTCore;

import java.util.Set;

public final class SFTOres {
    public static void init() {
        // Remove all the veins
        Set<ResourceLocation> keys = Set.copyOf(GTRegistries.ORE_VEINS.keys());
        keys.forEach((rl) -> {
            SFTCore.LOGGER.info("Removing ore vein: {}", rl);
            GTRegistries.ORE_VEINS.remove(rl);
        });
    }
}
