package org.leodreamer.sftcore.api.serialization;

import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

import com.mojang.serialization.Codec;

/**
 * An interface for objects that can provide a Codec for serialization and deserialization.
 */
public interface ITagCodecSerializable<S extends ITagCodecSerializable<S, T>, T extends Tag>
    extends ISerializable<S, T, ITagCodecSerializable.CodecWrapper<S, T>> {

    Codec<S> getCodec();

    @SuppressWarnings("unchecked")
    @Override
    default T encode() {
        return (T) getCodec().encodeStart(NbtOps.INSTANCE, (S) this).getOrThrow(
            false,
            (str) -> {}
        );
    }

    static <S, T extends Tag> S decodeBy(Codec<S> codec, T nbt) {
        return ISerializable.decodeBy(new CodecWrapper<>(codec), nbt);
    }

    record CodecWrapper<S, T extends Tag>(Codec<S> codec) implements IDeserializer<S, T> {

        @Override
        public S deserialize(T nbt) {
            return codec.parse(NbtOps.INSTANCE, nbt).getOrThrow(
                false,
                (str) -> {}
            );
        }
    }
}
