package org.leodreamer.sftcore.common.item;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.api.gui.SplitSideFancyMachineUIWidget;
import org.leodreamer.sftcore.common.item.terminal.MekBuilderRegistry;
import org.leodreamer.sftcore.common.item.terminal.api.BuildContext;
import org.leodreamer.sftcore.common.item.terminal.api.BuildExecutor;
import org.leodreamer.sftcore.common.item.terminal.gui.MekTerminalFancyUIProvider;

import com.gregtechceu.gtceu.api.item.component.IInteractionItem;
import com.gregtechceu.gtceu.api.item.component.IItemUIFactory;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
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

@DataGenScanned
public class MekTerminalBehavior implements IInteractionItem, IItemUIFactory {

    @RegisterLanguage("Right-click the %s with Shift to start building")
    public static final String INVALID_START = "item.sftcore.mek_terminal.invalid_start";

    @RegisterLanguage("Successfully place %s blocks")
    public static final String REPORT = "item.sftcore.mek_terminal.report";

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

        var buildContext = new BuildContext(serverLevel, player, context.getClickedPos(), stack);

        var expectedBlock = builder.clickAt();
        if (!buildContext.level().getBlockState(buildContext.clicked()).is(expectedBlock)) {
            var error = Component.translatable(INVALID_START, expectedBlock.getName()).withStyle(ChatFormatting.RED);
            player.displayClientMessage(error, true);
            return InteractionResult.FAIL;
        }

        var plan = builder.createPlan(buildContext, stack.getOrCreateTag());
        int success = BuildExecutor.execute(buildContext, plan);
        var message = Component.translatable(REPORT, success).withStyle(ChatFormatting.GREEN);

        player.displayClientMessage(message, true);
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
            .widget(new SplitSideFancyMachineUIWidget(provider, 176, 166));
    }
}
