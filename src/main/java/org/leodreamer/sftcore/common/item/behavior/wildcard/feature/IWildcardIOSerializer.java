package org.leodreamer.sftcore.common.item.behavior.wildcard.feature;

import net.minecraft.nbt.CompoundTag;

public interface IWildcardIOSerializer {

    String key();

    CompoundTag writeToNBT(IWildcardIOComponent component);

    IWildcardIOComponent readFromNBT(CompoundTag nbt);
}
