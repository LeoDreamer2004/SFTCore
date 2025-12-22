package org.leodreamer.sftcore.integration.ae2.logic;

import appeng.api.stacks.AEItemKey;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public record AvailableGTRow(AEItemKey item, Component title, List<Component> tooltips, String prompt) {

    public static AvailableGTRow of(AEItemKey item, Component title, List<Component> tooltips) {
        return new AvailableGTRow(item, title, tooltips, "");
    }

    public AvailableGTRow withPrompt(String prompt) {
        return new AvailableGTRow(item, title, tooltips, prompt);
    }

    public void write(FriendlyByteBuf stream) {
        item.writeToPacket(stream);
        stream.writeComponent(title);
        stream.writeVarInt(tooltips.size());
        for (var tooltip : tooltips) {
            stream.writeComponent(tooltip);
        }
        stream.writeUtf(prompt);
    }

    public static AvailableGTRow read(FriendlyByteBuf stream) {
        var item = AEItemKey.fromPacket(stream);
        var title = stream.readComponent();
        var tooltipCount = stream.readVarInt();
        var tooltips = new ArrayList<Component>(tooltipCount);
        for (int i = 0; i < tooltipCount; i++) {
            tooltips.add(stream.readComponent());
        }
        var prompt = stream.readUtf();
        return new AvailableGTRow(item, title, tooltips, prompt);
    }
}
