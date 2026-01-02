package org.leodreamer.sftcore.api.serialization;

import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

import com.mojang.serialization.Codec;

/**
 * An interface for objects that can provide a Codec for serialization and deserialization.
 */
public interface ITagCodecSerializable<SELF extends ITagCodecSerializable<SELF, DATA>, DATA extends Tag>
    extends ICustomSerializable<SELF, DATA, ITagCodecSerializable.CodecWrapper<SELF, DATA>> {

    Codec<SELF> getCodec();

    @Override
    default CodecWrapper<SELF, DATA> getSerializer() {
        return new CodecWrapper<>(getCodec());
    }

    static <S, T extends Tag> S decodeBy(Codec<S> codec, T nbt) {
        return ISerializable.decodeBy(new CodecWrapper<>(codec), nbt);
    }

    record CodecWrapper<S, T extends Tag>(Codec<S> codec) implements ISerializer<S, T> {

        @Override
        public S deserialize(T nbt) {
            return codec.parse(NbtOps.INSTANCE, nbt).getOrThrow(
                false,
                (str) -> {}
            );
        }

        @Override
        @SuppressWarnings("unchecked")
        public T serialize(S obj) {
            return (T) codec.encodeStart(NbtOps.INSTANCE, obj).getOrThrow(
                false,
                (str) -> {}
            );
        }
    }
}
