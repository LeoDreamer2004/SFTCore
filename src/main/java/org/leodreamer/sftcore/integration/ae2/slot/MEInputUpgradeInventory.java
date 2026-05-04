package org.leodreamer.sftcore.integration.ae2.slot;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;

import net.minecraft.world.level.ItemLike;

import appeng.core.definitions.AEItems;

import java.util.Arrays;

public class MEInputUpgradeInventory extends NotifiableItemStackHandler {

    public static final ItemLike[] availableUpgrades = new ItemLike[] {
        AEItems.CRAFTING_CARD
    };

    public MEInputUpgradeInventory() {
        super(availableUpgrades.length, IO.NONE);
        setFilter(stack -> Arrays.stream(availableUpgrades).anyMatch(item -> stack.is(item.asItem())));
    }

    public boolean installed(ItemLike upgrade) {
        for (int i = 0; i < availableUpgrades.length; i++) {
            if (getStackInSlot(i).is(upgrade.asItem())) {
                return true;
            }
        }
        return false;
    }
}
