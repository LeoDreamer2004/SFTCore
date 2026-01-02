package org.leodreamer.sftcore.integration.ae2.logic;

import org.leodreamer.sftcore.common.data.SFTItems;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.inventories.InternalInventory;
import appeng.core.definitions.AEItems;
import appeng.core.localization.PlayerMessages;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.PlayerInternalInventory;
import org.jetbrains.annotations.Nullable;

/**
 * A proxy for {@link InternalInventory} which can be operated by Memory Card.
 * This will use the NBT format defined in {@link PatternProviderLogic}
 */
public record MemoryCardPatternInventoryProxy(InternalInventory inventory, Level level) {

    private static final String KEY = PatternProviderLogic.NBT_MEMORY_CARD_PATTERNS;

    /**
     * From {@link appeng.helpers.patternprovider.PatternProviderLogic#exportSettings},
     * but without wildcard patterns.
     * <p>
     * HACKY! We are manually simulating {@link AppEngInternalInventory#writeToNBT} here
     * to adapt inventory defined in GTM Machine.
     */
    public void exportSettings(CompoundTag output) {
        if (inventory.isEmpty()) {
            output.remove(KEY);
            return;
        }

        var items = new ListTag();
        for (int i = 0; i < inventory.size(); i++) {
            var stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty() && !stack.is(SFTItems.WILDCARD_PATTERN.asItem())) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("Slot", i);
                items.add(stack.save(itemTag));
            }
        }
        output.put(KEY, items);
    }

    /**
     * Totally same with {@link appeng.helpers.patternprovider.PatternProviderLogic#importSettings}.
     * Wildcard patterns don't support importing
     */
    public void importSettings(CompoundTag input, @Nullable Player player) {
        if (player != null && input.contains(KEY) && !player.level().isClientSide) {
            clearPatternInventory(player);

            var desiredPatterns = new AppEngInternalInventory(inventory.size());
            desiredPatterns.readFromNBT(input, KEY);

            // restore from blank patterns in the player inv
            var playerInv = player.getInventory();
            var blankPatternsAvailable = player.getAbilities().instabuild ? Integer.MAX_VALUE :
                playerInv.countItem(AEItems.BLANK_PATTERN.asItem());
            var blankPatternsUsed = 0;
            for (int i = 0; i < desiredPatterns.size(); i++) {
                var pattern = PatternDetailsHelper.decodePattern(desiredPatterns.getStackInSlot(i), level, true);
                if (pattern == null) {
                    continue; // skip junk / broken recipes
                }

                // Keep track of how many blank patterns we need
                ++blankPatternsUsed;
                if (blankPatternsAvailable >= blankPatternsUsed) {
                    if (!inventory.addItems(pattern.getDefinition().toStack()).isEmpty()) {
                        // the inventory is full or for some other reasons
                        blankPatternsUsed--;
                    }
                }
            }

            if (blankPatternsUsed > 0 && !player.getAbilities().instabuild) {
                new PlayerInternalInventory(playerInv)
                    .removeItems(blankPatternsUsed, AEItems.BLANK_PATTERN.stack(), null);
            }
            if (blankPatternsUsed > blankPatternsAvailable) {
                player.sendSystemMessage(
                    PlayerMessages.MissingBlankPatterns.text(blankPatternsUsed - blankPatternsAvailable)
                );
            }
        }
    }

    /**
     * clear internal and give back the blank patterns
     */
    private void clearPatternInventory(Player player) {
        // just clear for creative mode players
        if (player.getAbilities().instabuild) {
            for (int i = 0; i < inventory.size(); i++) {
                inventory.setItemDirect(i, ItemStack.EMPTY);
            }
            return;
        }

        var playerInv = player.getInventory();

        // clear out any existing patterns and give them to the player
        var blankPatternCount = 0;
        for (int i = 0; i < inventory.size(); i++) {
            var pattern = inventory.getStackInSlot(i);
            if (
                pattern.is(AEItems.CRAFTING_PATTERN.asItem()) || pattern.is(AEItems.PROCESSING_PATTERN.asItem()) ||
                    pattern.is(AEItems.SMITHING_TABLE_PATTERN.asItem()) ||
                    pattern.is(AEItems.STONECUTTING_PATTERN.asItem()) || pattern.is(AEItems.BLANK_PATTERN.asItem())
            ) {
                blankPatternCount += pattern.getCount();
            } else {
                // wildcard patterns or something else
                playerInv.placeItemBackInInventory(pattern);
            }
            inventory.setItemDirect(i, ItemStack.EMPTY);
        }

        // place back the removed blank patterns all at once
        if (blankPatternCount > 0) {
            playerInv.placeItemBackInInventory(AEItems.BLANK_PATTERN.stack(blankPatternCount), false);
        }
    }
}
