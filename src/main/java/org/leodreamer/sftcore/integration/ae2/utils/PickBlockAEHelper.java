package org.leodreamer.sftcore.integration.ae2.utils;

import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;

import appeng.api.config.Actionable;
import appeng.api.networking.IGrid;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.StorageHelper;
import appeng.helpers.WirelessTerminalMenuHost;
import appeng.items.tools.powered.WirelessTerminalItem;
import appeng.menu.locator.MenuLocators;
import appeng.menu.me.crafting.CraftAmountMenu;
import org.jetbrains.annotations.Nullable;

public final class PickBlockAEHelper {

    private PickBlockAEHelper() {}

    /**
     * Helper function for player to extract item from the AE storage
     */
    public static void handle(ServerPlayer player, ItemStack picked) {
        if (player.gameMode.getGameModeForPlayer() != GameType.SURVIVAL) {
            return;
        }
        if (picked.isEmpty()) {
            return;
        }

        var inventory = player.getInventory();
        if (inventory.getFreeSlot() == -1) {
            return;
        }

        int existingSlot = inventory.findSlotMatchingItem(picked);
        if (existingSlot != -1) {
            // vanilla: select the item from the inventory
            selectInventorySlot(player, existingSlot);
            return;
        }

        var key = AEItemKey.of(picked);
        if (key == null) {
            return;
        }

        var terminal = findFirstWirelessTerminal(player);
        if (terminal == null) {
            return;
        }
        var storage = terminal.host.getInventory();
        if (storage == null) {
            return;
        }

        long extracted = StorageHelper.poweredExtraction(
            terminal.host,
            storage,
            key,
            key.getMaxStackSize(),
            IActionSource.ofPlayer(player),
            Actionable.MODULATE
        );
        if (extracted > 0) {
            // extract the item from the storage
            selectExtractedStack(player, key.toStack((int) extracted));
        } else if (terminal.grid.getCraftingService().isCraftable(key)) {
            // try to craft the item if not exists
            CraftAmountMenu.open(player, MenuLocators.forInventorySlot(terminal.slot), key, key.getAmountPerUnit());
        }
    }

    private static void selectInventorySlot(ServerPlayer player, int slot) {
        var inventory = player.getInventory();
        if (Inventory.isHotbarSlot(slot)) {
            inventory.selected = slot;
            player.connection.send(new ClientboundSetCarriedItemPacket(inventory.selected));
            return;
        }

        inventory.pickSlot(slot);
        syncPickedSlots(player, slot);
    }

    private static void selectExtractedStack(ServerPlayer player, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }

        var inventory = player.getInventory();
        int targetSlot = inventory.selected;
        var selected = inventory.getItem(targetSlot);
        if (!selected.isEmpty()) {
            int freeSlot = inventory.getFreeSlot();
            if (freeSlot != -1) {
                inventory.setItem(freeSlot, selected);
                syncInventorySlot(player, freeSlot);
            } else {
                targetSlot = inventory.getSuitableHotbarSlot();
            }
        }

        inventory.selected = targetSlot;
        inventory.setItem(targetSlot, stack);
        syncInventorySlot(player, targetSlot);
        player.connection.send(new ClientboundSetCarriedItemPacket(targetSlot));
        player.containerMenu.broadcastChanges();
    }

    private static void syncPickedSlots(ServerPlayer player, int sourceSlot) {
        var inventory = player.getInventory();
        syncInventorySlot(player, inventory.selected);
        syncInventorySlot(player, sourceSlot);
        player.connection.send(new ClientboundSetCarriedItemPacket(inventory.selected));
    }

    private static void syncInventorySlot(ServerPlayer player, int slot) {
        player.connection.send(new ClientboundContainerSetSlotPacket(-2, 0, slot, player.getInventory().getItem(slot)));
    }

    @Nullable
    private static TerminalContext findFirstWirelessTerminal(ServerPlayer player) {
        var inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            var stack = inventory.getItem(slot);
            if (stack.isEmpty() || !(stack.getItem() instanceof WirelessTerminalItem terminal)) {
                continue;
            }

            var grid = terminal.getLinkedGrid(stack, player.level(), player);
            if (grid != null) {
                var host = terminal.getMenuHost(player, slot, stack, null);
                if (host instanceof WirelessTerminalMenuHost wirelessHost && wirelessHost.rangeCheck()) {
                    return new TerminalContext(slot, wirelessHost, grid);
                }
            }
        }
        return null;
    }

    private record TerminalContext(int slot, WirelessTerminalMenuHost host, IGrid grid) {}
}
