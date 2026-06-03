package org.leodreamer.sftcore.common.item;

import com.lowdragmc.lowdraglib.gui.widget.Widget;
import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.terminal.MekBuilderRegistry;
import org.leodreamer.sftcore.common.item.terminal.MekTerminalTags;
import org.leodreamer.sftcore.common.item.terminal.api.*;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.item.component.IInteractionItem;
import com.gregtechceu.gtceu.api.item.component.IItemUIFactory;
import com.lowdragmc.lowdraglib.gui.factory.HeldItemUIFactory;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.widget.ButtonWidget;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
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
import org.leodreamer.sftcore.common.item.terminal.builder.InductionMatrixBuilder;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

@DataGenScanned
public class MekTerminalBehavior implements IInteractionItem, IItemUIFactory {

    @RegisterLanguage("Mekanism Terminal")
    public static final String TITLE = "item.sftcore.mek_terminal.title";

    @RegisterLanguage("Please sneak right click a valid Mekanism multiblock starting point")
    public static final String INVALID_START = "item.sftcore.mek_terminal.invalid_start";

    @RegisterLanguage("Width")
    public static final String WIDTH = "item.sftcore.mek_terminal.width";

    @RegisterLanguage("Height")
    public static final String HEIGHT = "item.sftcore.mek_terminal.height";

    @RegisterLanguage("Depth")
    public static final String DEPTH = "item.sftcore.mek_terminal.depth";

    @RegisterLanguage("Balanced")
    public static final String BALANCED = "item.sftcore.mek_terminal.strategy.balanced";

    @RegisterLanguage("Cell First")
    public static final String CELL_FIRST = "item.sftcore.mek_terminal.strategy.cell_first";

    @RegisterLanguage("Provider First")
    private static final String PROVIDER_FIRST = "item.sftcore.mek_terminal.strategy.provider_first";

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

        var builder = MekBuilderRegistry.selected(stack);
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
            player.displayClientMessage(builder.invalidStartMessage().copy().withStyle(ChatFormatting.RED), true);
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
        var stack = holder.getHeld();
        return new ModularUI(176, 166, holder, player)
            .widget(createMainWidget(stack));
    }

    private WidgetGroup createMainWidget(ItemStack stack) {
        var root = new WidgetGroup(0, 0, 176, 166);
        root.setBackground(GuiTextures.BACKGROUND_INVERSE);

        root.addWidget(new LabelWidget(8, 8, Component.translatable(TITLE)));

        root.addWidget(new ButtonWidget(
            8,
            26,
            72,
            18,
            null,
            click -> MekBuilderRegistry.setSelected(stack, MekBuilderRegistry.INDUCTION)
        ).setHoverTooltips(Component.translatable(InductionMatrixBuilder.TITLE)));

        addInductionPage(root, stack);

        return root;
    }

    private void addInductionPage(WidgetGroup root, ItemStack stack) {
        var induction = getInductionConfig(stack);

        int x = 16;
        int y = 56;

        root.addWidget(new LabelWidget(x, y, Component.translatable(WIDTH)));
        root.addWidget(numberInput(
            120,
            y - 3,
            () -> getInt(induction, MekTerminalTags.INDUCTION_WIDTH, 5),
            v -> setInt(induction, stack, MekTerminalTags.INDUCTION_WIDTH, v, 3, 18)
        ));

        y += 20;
        root.addWidget(new LabelWidget(x, y, Component.translatable(HEIGHT)));
        root.addWidget(numberInput(
            120,
            y - 3,
            () -> getInt(induction, MekTerminalTags.INDUCTION_HEIGHT, 5),
            v -> setInt(induction, stack, MekTerminalTags.INDUCTION_HEIGHT, v, 3, 18)
        ));

        y += 20;
        root.addWidget(new LabelWidget(x, y, Component.translatable(DEPTH)));
        root.addWidget(numberInput(
            120,
            y - 3,
            () -> getInt(induction, MekTerminalTags.INDUCTION_DEPTH, 5),
            v -> setInt(induction, stack, MekTerminalTags.INDUCTION_DEPTH, v, 3, 18)
        ));

        y += 28;
        root.addWidget(strategyButton(
            16,
            y,
            Component.translatable(BALANCED),
            stack,
            MekTerminalTags.STRATEGY_BALANCED
        ));
        root.addWidget(strategyButton(
            72,
            y,
            Component.translatable(CELL_FIRST),
            stack,
            MekTerminalTags.STRATEGY_CELL_FIRST
        ));
        root.addWidget(strategyButton(
            128,
            y,
            Component.translatable(PROVIDER_FIRST),
            stack,
            MekTerminalTags.STRATEGY_PROVIDER_FIRST
        ));
    }

    private WidgetGroup numberInput(
        int x,
        int y,
        IntSupplier getter,
        IntConsumer setter
    ) {
        WidgetGroup group = new WidgetGroup(x, y, 48, 16);

        group.addWidget(new ButtonWidget(
            0,
            0,
            16,
            16,
            null,
            click -> setter.accept(Math.max(3, getter.getAsInt() - 1))
        ).setHoverTooltips(Component.literal("-")));

        group.addWidget(new LabelWidget(
            20,
            4,
            Component.literal(String.valueOf(getter.getAsInt()))
        ));

        group.addWidget(new ButtonWidget(
            32,
            0,
            16,
            16,
            null,
            click -> setter.accept(Math.min(18, getter.getAsInt() + 1))
        ).setHoverTooltips(Component.literal("+")));

        return group;
    }


    private Widget strategyButton(
        int x,
        int y,
        Component text,
        ItemStack stack,
        String strategy
    ) {
        return new ButtonWidget(
            x,
            y,
            48,
            18,
            null,
            click -> {
                CompoundTag config = getInductionConfig(stack);
                config.putString(MekTerminalTags.INDUCTION_FILL_STRATEGY, strategy);
                saveInductionConfig(stack, config);
            }
        ).setHoverTooltips(text);
    }

    private CompoundTag getInductionConfig(ItemStack stack) {
        var tag = stack.getOrCreateTag();
        var root = tag.getCompound(MekTerminalTags.ROOT);
        var config = root.getCompound(MekTerminalTags.INDUCTION);

        if (!config.contains(MekTerminalTags.INDUCTION_WIDTH)) {
            config.putInt(MekTerminalTags.INDUCTION_WIDTH, 5);
        }
        if (!config.contains(MekTerminalTags.INDUCTION_HEIGHT)) {
            config.putInt(MekTerminalTags.INDUCTION_HEIGHT, 5);
        }
        if (!config.contains(MekTerminalTags.INDUCTION_DEPTH)) {
            config.putInt(MekTerminalTags.INDUCTION_DEPTH, 5);
        }
        if (!config.contains(MekTerminalTags.INDUCTION_FILL_STRATEGY)) {
            config.putString(
                MekTerminalTags.INDUCTION_FILL_STRATEGY,
                MekTerminalTags.STRATEGY_BALANCED
            );
        }

        root.put(MekTerminalTags.INDUCTION, config);
        tag.put(MekTerminalTags.ROOT, root);
        return config;
    }

    private void saveInductionConfig(ItemStack stack, CompoundTag config) {
        var tag = stack.getOrCreateTag();
        var root = tag.getCompound(MekTerminalTags.ROOT);
        root.put(MekTerminalTags.INDUCTION, config);
        tag.put(MekTerminalTags.ROOT, root);
    }

    private int getInt(CompoundTag tag, String key, int def) {
        return tag.contains(key) ? tag.getInt(key) : def;
    }

    private void setInt(
        CompoundTag config,
        ItemStack stack,
        String key,
        int value,
        int min,
        int max
    ) {
        int clamped = Math.max(min, Math.min(max, value));
        config.putInt(key, clamped);
        saveInductionConfig(stack, config);
    }
}
