package org.leodreamer.sftcore.integration.ae2.sync;

import org.leodreamer.sftcore.integration.ae2.feature.IGTTransferPanel;
import org.leodreamer.sftcore.integration.ae2.logic.AvailableGTRow;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import appeng.client.gui.me.items.PatternEncodingTermScreen;
import appeng.core.sync.BasePacket;
import io.netty.buffer.Unpooled;

import java.util.ArrayList;
import java.util.List;

public class AvailableGTMachinesPacket extends BasePacket {

    private final List<AvailableGTRow> rows;

    public AvailableGTMachinesPacket(FriendlyByteBuf stream) {
        int size = stream.readVarInt();
        this.rows = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            this.rows.add(AvailableGTRow.read(stream));
        }
    }

    public AvailableGTMachinesPacket(List<AvailableGTRow> rows) {
        this.rows = rows;

        final FriendlyByteBuf stream = new FriendlyByteBuf(Unpooled.buffer());
        stream.writeInt(getPacketID());
        stream.writeVarInt(rows.size());
        for (AvailableGTRow row : rows) {
            row.write(stream);
        }
        this.configureWrite(stream);
    }

    public static AvailableGTMachinesPacket empty() {
        return new AvailableGTMachinesPacket(new ArrayList<>());
    }

    @Override
    public void clientPacketData(Player player) {
        var cur = Minecraft.getInstance().screen;
        if (cur instanceof PatternEncodingTermScreen<?> screen) {
            ((IGTTransferPanel) screen).sftcore$gtPanel().setRows(rows);
        }
    }
}
