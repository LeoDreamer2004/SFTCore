package org.leodreamer.sftcore.common.item.wildcard.feature;

import org.leodreamer.sftcore.api.serialization.ICustomSerializable;

import net.minecraft.nbt.CompoundTag;

public interface IWildcardSerializable<T extends IWildcardSerializable<T>>
    extends ICustomSerializable<T, CompoundTag, IWildcardSerializable.IWildcardSerializer<T>> {

    interface IWildcardSerializer<T> extends ISerializer<T, CompoundTag> {

        String key();
    }
}
