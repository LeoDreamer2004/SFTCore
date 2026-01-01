package org.leodreamer.sftcore.api.serialization;

public interface ISerializable<SELF extends ISerializable<SELF, DATA, DE>, DATA, DE extends ISerializable.IDeserializer<SELF, DATA>> {
    DATA encode();

    static <SELF, DATA, DE extends IDeserializer<SELF, DATA>> SELF decodeBy(DE deserializer, DATA data) {
        return deserializer.deserialize(data);
    }

    @FunctionalInterface
    interface IDeserializer<SELF, DATA> {
        SELF deserialize(DATA data);
    }
}
