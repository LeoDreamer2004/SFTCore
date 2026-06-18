package org.leodreamer.sftcore.integration.ae2.sync;

import org.leodreamer.sftcore.integration.ae2.utils.PickBlockAEHelper;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import appeng.core.sync.BasePacket;
import io.netty.buffer.Unpooled;

public class PickBlockAERequestPacket extends BasePacket {

    private final ItemStack picked;

    public PickBlockAERequestPacket(FriendlyByteBuf stream) {
        this.picked = stream.readItem();
    }

    public PickBlockAERequestPacket(ItemStack picked) {
        this.picked = picked.copy();

        final FriendlyByteBuf stream = new FriendlyByteBuf(Unpooled.buffer());
        stream.writeInt(getPacketID());
        stream.writeItem(this.picked);
        this.configureWrite(stream);
    }

    @Override
    public void serverPacketData(ServerPlayer player) {
        PickBlockAEHelper.handle(player, picked);
    }
}
