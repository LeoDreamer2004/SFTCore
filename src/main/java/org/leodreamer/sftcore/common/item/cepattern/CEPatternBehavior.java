package org.leodreamer.sftcore.common.item.cepattern;

import com.gregtechceu.gtceu.api.item.component.IAddInformation;
import com.gregtechceu.gtceu.api.item.component.IItemUIFactory;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import com.lowdragmc.lowdraglib.gui.factory.HeldItemUIFactory;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CEPatternBehavior implements IItemUIFactory, IAddInformation {

    @Override
    public ModularUI createUI(HeldItemUIFactory.HeldItemHolder holder, Player player) {
        var hand = holder.getHand();
        var provider = new CEPatternUIProvider(
            player.level(),
            player.getItemInHand(hand),
            stack -> player.setItemInHand(hand, stack)
        );
        var ui = new ModularUI(230, 258, holder, player)
            .widget(provider.createWidget());
        ui.registerCloseListener(() -> provider.onClose(player));
        return ui;
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
