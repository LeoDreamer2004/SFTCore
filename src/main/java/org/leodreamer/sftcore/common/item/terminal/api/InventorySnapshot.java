package org.leodreamer.sftcore.common.item.terminal.api;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;

public class InventorySnapshot {

    private final Map<Item, Integer> counts = new HashMap<>();

    public static InventorySnapshot of(Player player) {
        var snapshot = new InventorySnapshot();

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            var stack = player.getInventory().getItem(i);
            if (!stack.isEmpty()) {
                snapshot.counts.merge(stack.getItem(), stack.getCount(), Integer::sum);
            }
        }

        return snapshot;
    }

    public int count(Item item) {
        return counts.getOrDefault(item, 0);
    }

    public void takeVirtual(Item item) {
        int count = count(item);
        if (count <= 0) {
            return;
        }
        counts.put(item, count - 1);
    }
}
