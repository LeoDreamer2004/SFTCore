package org.leodreamer.sftcore.common.item;

import org.leodreamer.sftcore.common.item.terminal.MekBuilderRegistry;
import org.leodreamer.sftcore.common.item.terminal.api.BuildContext;
import org.leodreamer.sftcore.common.item.terminal.api.BuildExecutor;
import org.leodreamer.sftcore.common.item.terminal.gui.MekTerminalFancyUIProvider;

import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget;
import com.gregtechceu.gtceu.api.item.component.IInteractionItem;
import com.gregtechceu.gtceu.api.item.component.IItemUIFactory;

import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import com.lowdragmc.lowdraglib.gui.factory.HeldItemUIFactory;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;

public class MekTerminalBehavior implements IInteractionItem, IItemUIFactory {

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var player = context.getPlayer();

        if (player == null || !player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }

        var level = context.getLevel();
        var stack = context.getItemInHand();

        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        var builder = MekBuilderRegistry.selected(stack).builder();
        if (builder == null) {
            return InteractionResult.PASS;
        }

        var buildContext = new BuildContext(
            serverLevel,
            player,
            context.getClickedPos(),
            stack
        );

        if (!builder.canStart(buildContext)) {
            player.displayClientMessage(
                builder.invalidStartMessage().copy().withStyle(ChatFormatting.RED),
                true
            );
            return InteractionResult.FAIL;
        }

        var plan = builder.createPlan(buildContext, stack.getOrCreateTag());
        var report = BuildExecutor.execute(buildContext, plan);

        player.displayClientMessage(report.toComponent(), true);
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(
        Item item,
        Level level,
        Player player,
        InteractionHand usedHand
    ) {
        var held = player.getItemInHand(usedHand);

        if (player.isShiftKeyDown()) {
            return InteractionResultHolder.pass(held);
        }

        return IItemUIFactory.super.use(item, level, player, usedHand);
    }

    @Override
    public ModularUI createUI(HeldItemUIFactory.HeldItemHolder holder, Player player) {
        var hand = holder.getHand();
        var stack = player.getItemInHand(hand);

        var provider = new MekTerminalFancyUIProvider(stack, newStack -> player.setItemInHand(hand, newStack));

        return new ModularUI(176, 166, holder, player)
            .widget(new FancyMachineUIWidget(provider, 176, 166));
    }
}
