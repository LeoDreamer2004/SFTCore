package org.leodreamer.sftcore.common.item.cepattern;

import com.gregtechceu.gtceu.api.item.component.IAddInformation;
import com.gregtechceu.gtceu.api.mui.IItemUIHolder;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import brachy.modularui.factory.PlayerInventoryGuiData;
import brachy.modularui.screen.ModularPanel;
import brachy.modularui.screen.UISettings;
import brachy.modularui.value.sync.PanelSyncManager;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CEPatternBehavior implements IItemUIHolder, IAddInformation {

    @Override
    public ModularPanel<?> buildUI(PlayerInventoryGuiData<?> data, PanelSyncManager syncManager, UISettings settings) {
        var player = data.getPlayer();
        var openedStack = data.getUsedItemStack();
        var hand = player.getOffhandItem() == openedStack ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        int lockedPlayerSlot = hand == InteractionHand.MAIN_HAND ? player.getInventory().selected : -1;
        var provider = new CEPatternUIProvider(
            player.level(),
            openedStack,
            stack -> {
                if (player.getItemInHand(hand) == openedStack) {
                    player.setItemInHand(hand, stack);
                }
            },
            lockedPlayerSlot
        );
        return provider.buildUI(data, syncManager, settings);
    }

    @Override
    public void appendHoverText(
        ItemStack stack,
        @Nullable Level level,
        List<Component> tooltips,
        TooltipFlag isAdvanced
    ) {
        tooltips.addAll(CEPatternTooltips.getTooltip(stack, level));
    }
}
