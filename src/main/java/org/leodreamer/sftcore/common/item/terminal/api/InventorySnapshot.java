package org.leodreamer.sftcore.common.item.terminal.api;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;

import java.util.Map;

public class InventorySnapshot {

    private final Map<Item, Integer> counts = new Object2IntArrayMap<>();
    private boolean isCreative = false;

    public static InventorySnapshot of(Player player) {
        var snapshot = new InventorySnapshot();
        if (player.isCreative()) {
            snapshot.isCreative = true;
            return snapshot;
        }

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            var stack = player.getInventory().getItem(i);
            if (!stack.isEmpty()) {
                snapshot.counts.merge(stack.getItem(), stack.getCount(), Integer::sum);
            }
        }

        return snapshot;
    }

    public static InventorySnapshot creative() {
        var snapshot = new InventorySnapshot();
        snapshot.isCreative = true;
        return snapshot;
    }

    public int count(Item item) {
        if (isCreative) {
            return Integer.MAX_VALUE;
        }
        return counts.getOrDefault(item, 0);
    }

    public void takeVirtual(Item item) {
        if (isCreative) {
            return;
        }
        int count = count(item);
        if (count <= 0) {
            return;
        }
        counts.put(item, count - 1);
    }
}
