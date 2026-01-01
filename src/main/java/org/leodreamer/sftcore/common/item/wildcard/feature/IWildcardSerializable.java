package org.leodreamer.sftcore.common.item.wildcard.feature;

public interface IWildcardSerializable<T> {

    IWildcardSerializer<T> getSerializer();
}
