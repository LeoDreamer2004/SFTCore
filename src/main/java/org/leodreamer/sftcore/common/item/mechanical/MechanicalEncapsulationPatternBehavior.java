package org.leodreamer.sftcore.common.item.mechanical;

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

public class MechanicalEncapsulationPatternBehavior implements IItemUIFactory, IAddInformation {

    @Override
    public ModularUI createUI(HeldItemUIFactory.HeldItemHolder holder, Player player) {
        var hand = holder.getHand();
        var provider = new MechanicalEncapsulationPatternUIProvider(
            player.level(),
            () -> player.getItemInHand(hand),
            stack -> {
                player.setItemInHand(hand, stack);
                holder.held = stack.copy();
            }
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
        List<Component> tooltipComponents,
        TooltipFlag isAdvanced
    ) {
        tooltipComponents.addAll(MechanicalEncapsulationPatternLogic.getTooltip(stack, level));
    }
}
