package org.leodreamer.sftcore.common.item;

import org.leodreamer.sftcore.common.item.wildcard.WildcardPatternLogic;
import org.leodreamer.sftcore.common.item.wildcard.WildcardPatternUIProvider;
import org.leodreamer.sftcore.common.item.wildcard.impl.WildcardTooltips;

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

public class WildcardPatternBehavior implements IItemUIHolder, IAddInformation {

    @Override
    public ModularPanel<?> buildUI(PlayerInventoryGuiData<?> data, PanelSyncManager syncManager, UISettings settings) {
        var player = data.getPlayer();
        var openedStack = data.getUsedItemStack();
        var hand = player.getOffhandItem() == openedStack ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        var provider = new WildcardPatternUIProvider(
            openedStack,
            stack -> {
                if (player.getItemInHand(hand) == openedStack) {
                    player.setItemInHand(hand, stack);
                }
            }
        );
        return provider.buildUI(data, syncManager, settings);
    }

    @Override
    public void appendHoverText(
        ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced
    ) {
        var logic = WildcardPatternLogic.on(stack);
        new WildcardTooltips(logic).createTooltips(tooltipComponents);
    }
}
