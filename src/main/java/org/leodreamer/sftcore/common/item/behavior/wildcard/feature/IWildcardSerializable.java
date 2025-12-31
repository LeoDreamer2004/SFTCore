package org.leodreamer.sftcore.common.item.behavior.wildcard.feature;

public interface IWildcardSerializable<T> {

    IWildcardSerializer<T> getSerializer();
}
