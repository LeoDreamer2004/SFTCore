package org.leodreamer.sftcore.common.item;

import com.gregtechceu.gtceu.api.item.component.IAddInformation;
import com.gregtechceu.gtceu.api.item.component.IItemUIFactory;
import com.lowdragmc.lowdraglib.gui.factory.HeldItemUIFactory;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.common.item.wildcard.WildcardPatternLogic;
import org.leodreamer.sftcore.common.item.wildcard.gui.WildcardFancyUIProvider;
import org.leodreamer.sftcore.common.item.wildcard.gui.WildcardHeldItemUI;
import org.leodreamer.sftcore.common.item.wildcard.impl.WildcardTooltips;

import java.util.List;
import java.util.function.Consumer;

@DataGenScanned
public class WildcardPatternBehavior implements IItemUIFactory, IAddInformation {

    @Override
    public ModularUI createUI(HeldItemUIFactory.HeldItemHolder heldItemHolder, Player player) {
        var hand = heldItemHolder.getHand();
        var stack = player.getItemInHand(hand);
        var logic = WildcardPatternLogic.on(stack);

        Consumer<ItemStack> save = s -> player.setItemInHand(hand, s);
        int heldSlotIndex = hand == InteractionHand.MAIN_HAND ? player.getInventory().selected : 40; // offhand slot

        var provider = new WildcardFancyUIProvider(logic, player.level(), save, heldSlotIndex);

        return new ModularUI(176, 166, heldItemHolder, player)
            .widget(new WildcardHeldItemUI(provider, 176, 166));
    }

    @Override
    public void appendHoverText(
        ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced
    ) {
        var logic = WildcardPatternLogic.on(stack);
        new WildcardTooltips(logic).createTooltips(tooltipComponents);
    }
}
