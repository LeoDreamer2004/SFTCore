package org.leodreamer.sftcore.integration.ae2.feature;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

import org.jetbrains.annotations.Nullable;

/**
 * AE2 does not extract memory card interaction feature,
 * as it is totally included in {@link appeng.blockentity.AEBaseBlockEntity}.
 * <p>
 * We provide this interface for GT Machines.
 */
public interface IMemoryCardInteraction {

    String sftcore$memoryId();

    void sftcore$exportSettings(CompoundTag output, @Nullable Player player);

    void sftcore$importSettings(CompoundTag input, @Nullable Player player);
}
