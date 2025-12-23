package org.leodreamer.sftcore.integration.ae2.sync;

import net.minecraft.network.FriendlyByteBuf;

import appeng.core.sync.BasePacket;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nullable;

public class SFTPacketHandler {

    private static final int OFFSET = 500;

    private static final Map<Class<? extends BasePacket>, PacketTypes> REVERSE_LOOKUP = new HashMap<>();

    public enum PacketTypes {

        AVAILABLE_GT_MACHINES(AvailableGTMachinesPacket.class, AvailableGTMachinesPacket::new),
        PROMPT_SYNC(PromptSyncPacket.class, PromptSyncPacket::new);

        private final Function<FriendlyByteBuf, BasePacket> factory;

        PacketTypes(
                    Class<? extends BasePacket> packetClass, Function<FriendlyByteBuf, BasePacket> factory) {
            this.factory = factory;

            REVERSE_LOOKUP.put(packetClass, this);
        }

        public static boolean isSFTType(int id) {
            return id >= OFFSET;
        }

        public static PacketTypes getPacket(int id) {
            return values()[id - OFFSET];
        }

        public int getPacketId() {
            return ordinal() + OFFSET;
        }

        @Nullable
        public static PacketTypes getID(Class<? extends BasePacket> c) {
            return REVERSE_LOOKUP.get(c);
        }

        public BasePacket parsePacket(FriendlyByteBuf in) throws IllegalArgumentException {
            return this.factory.apply(in);
        }
    }
}
