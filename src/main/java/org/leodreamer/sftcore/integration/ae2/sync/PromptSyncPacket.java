package org.leodreamer.sftcore.integration.ae2.sync;

import org.leodreamer.sftcore.integration.ae2.feature.IReceivePrompt;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import appeng.core.sync.BasePacket;
import io.netty.buffer.Unpooled;

public class PromptSyncPacket extends BasePacket {

    String prompt;

    public PromptSyncPacket(FriendlyByteBuf stream) {
        this.prompt = stream.readUtf(Short.MAX_VALUE);
    }

    public PromptSyncPacket(String prompt) {
        this.prompt = prompt;

        final FriendlyByteBuf stream = new FriendlyByteBuf(Unpooled.buffer());
        stream.writeInt(getPacketID());
        stream.writeUtf(prompt, Short.MAX_VALUE);
        this.configureWrite(stream);
    }

    @Override
    public void clientPacketData(Player player) {
        var cur = Minecraft.getInstance().screen;
        if (cur instanceof IReceivePrompt screen) {
            screen.sftcore$onReceive(prompt);
        }
    }
}
