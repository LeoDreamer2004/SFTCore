package org.leodreamer.sftcore.common.item;

import org.leodreamer.sftcore.common.item.wildcard.WildcardPatternLogic;
import org.leodreamer.sftcore.common.item.wildcard.gui.WildcardFancyUIProvider;
import org.leodreamer.sftcore.common.item.wildcard.impl.WildcardTooltips;

import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget;
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
import java.util.function.Consumer;

public class WildcardPatternBehavior implements IItemUIFactory, IAddInformation {

    @Override
    public ModularUI createUI(HeldItemUIFactory.HeldItemHolder heldItemHolder, Player player) {
        var hand = heldItemHolder.getHand();
        var stack = player.getItemInHand(hand);
        var logic = WildcardPatternLogic.on(stack);

        Consumer<ItemStack> onSave = s -> player.setItemInHand(hand, s);
        var provider = new WildcardFancyUIProvider(logic, player.level(), onSave);

        return new ModularUI(176, 166, heldItemHolder, player)
            .widget(new FancyMachineUIWidget(provider, 176, 166));
    }

    @Override
    public void appendHoverText(
        ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced
    ) {
        var logic = WildcardPatternLogic.on(stack);
        new WildcardTooltips(logic).createTooltips(tooltipComponents);
    }
}
