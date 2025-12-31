package org.leodreamer.sftcore.common.item.behavior.wildcard.feature;

import net.minecraft.nbt.CompoundTag;

import org.jetbrains.annotations.NotNull;

public interface IWildcardSerializer<T> {

    String key();

    @NotNull
    CompoundTag writeToNBT(T component);

    @NotNull
    T readFromNBT(CompoundTag nbt);
}
