package org.leodreamer.sftcore.integration.ae2.slot;

import appeng.core.definitions.AEItems;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import net.minecraft.world.level.ItemLike;

import java.util.Arrays;

public class MEInputUpgradeInventory extends CustomItemStackHandler {
    public static final ItemLike[] availableUpgrades = new ItemLike[]{
        AEItems.CRAFTING_CARD
    };

    public MEInputUpgradeInventory() {
        super(availableUpgrades.length);
        setFilter(stack -> Arrays.stream(availableUpgrades).anyMatch(item -> stack.is(item.asItem())));
    }

    public boolean installed(ItemLike upgrade) {
        return stacks.stream().anyMatch(stack -> stack.is(upgrade.asItem()));
    }
}
