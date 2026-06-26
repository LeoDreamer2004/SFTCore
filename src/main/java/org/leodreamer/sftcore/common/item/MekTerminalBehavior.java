package org.leodreamer.sftcore.common.item;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.terminal.MekBuilderRegistry;
import org.leodreamer.sftcore.common.item.terminal.api.BuildContext;
import org.leodreamer.sftcore.common.item.terminal.api.BuildExecutor;
import org.leodreamer.sftcore.common.item.terminal.gui.MekTerminalTab;

import com.gregtechceu.gtceu.api.item.component.IInteractionItem;
import com.gregtechceu.gtceu.api.mui.IItemUIHolder;
import com.gregtechceu.gtceu.common.mui.GTGuiTextures;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
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

import brachy.modularui.api.drawable.IDrawable;
import brachy.modularui.api.drawable.Text;
import brachy.modularui.api.widget.Interactable;
import brachy.modularui.drawable.GuiTextures;
import brachy.modularui.factory.PlayerInventoryGuiData;
import brachy.modularui.screen.ModularPanel;
import brachy.modularui.screen.RichTooltip;
import brachy.modularui.screen.UISettings;
import brachy.modularui.value.sync.PanelSyncManager;
import brachy.modularui.widgets.PageButton;
import brachy.modularui.widgets.PagedWidget;
import brachy.modularui.widgets.layout.Flow;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@DataGenScanned
public class MekTerminalBehavior implements IInteractionItem, IItemUIHolder {

    public static final int PAGE_WIDTH = 220;
    public static final int PAGE_HEIGHT = 160;
    public static final int PAGE_PADDING = 4;
    private static final String SELECT_TAB_ACTION = "mek_terminal_select_tab";

    @RegisterLanguage("Right-click the %s with Shift to start building")
    public static final String INVALID_START = "item.sftcore.mek_terminal.invalid_start";

    @RegisterLanguage("Successfully place %s blocks")
    public static final String REPORT = "item.sftcore.mek_terminal.report";

    @RegisterLanguage("Mekanism Structure Terminal")
    public static final String TITLE = "item.sftcore.mek_terminal.title";

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

        return IItemUIHolder.super.use(item, level, player, usedHand);
    }

    @Override
    public ModularPanel<?> buildUI(PlayerInventoryGuiData<?> data, PanelSyncManager syncManager, UISettings settings) {
        var stack = data.getUsedItemStack();

        // tabs
        var tabs = new ArrayList<MekTerminalTab<?>>();
        for (var entry : MekBuilderRegistry.entries()) {
            tabs.add(entry.createTab(stack, syncManager));
        }
        var tabController = new PagedWidget.Controller();
        syncManager.registerServerSyncedAction(
            SELECT_TAB_ACTION,
            packet -> selectTab(tabs, packet.readVarInt())
        );

        // editor pages
        var editor = new PagedWidget<>()
            .size(PAGE_WIDTH, PAGE_HEIGHT)
            .initialPage(selectedTabIndex(tabs))
            .controller(tabController);

        for (var tab : tabs) {
            editor.addPage(tab.createPage());
        }

        // main content
        var mainContent = Flow.column()
            .size(230, 190)
            .background(GTGuiTextures.BACKGROUND)
            .child(
                Flow.column()
                    .margin(4)
                    .childPadding(3)
                    .widthRel(1)
                    .heightRel(1)
                    .child(Text.lang(TITLE).asWidget().horizontalCenter())
                    .child(editor)
            );

        var panel = new ModularPanel<>("mek_terminal")
            .background(IDrawable.EMPTY)
            .disableHoverBackground()
            .child(mainContent)
            .coverChildren();
        panel.child(
            createTabColumn(tabs, tabController, syncManager)
                .relative(mainContent)
                .rightRel(1.0f)
                .top(4)
                .decoration()
        );
        return panel;
    }

    private Flow createTabColumn(
        List<MekTerminalTab<?>> tabs,
        PagedWidget.Controller tabController,
        PanelSyncManager syncManager
    ) {
        var column = Flow.column().coverChildren();
        for (int i = 0; i < tabs.size(); i++) {
            column.child(createTabButton(i, tabLocation(i, tabs.size()), tabController, tabs.get(i), syncManager));
        }
        return column;
    }

    private PageButton createTabButton(
        int index,
        int location,
        PagedWidget.Controller tabController,
        MekTerminalTab<?> tab,
        PanelSyncManager syncManager
    ) {
        return new PageButton(index, tabController) {

            @Override
            public Interactable.Result onMousePressed(int button) {
                var result = super.onMousePressed(button);
                if (result == Interactable.Result.SUCCESS) {
                    syncManager.callSyncedAction(SELECT_TAB_ACTION, buf -> buf.writeVarInt(index));
                }
                return result;
            }
        }
            .tab(GuiTextures.TAB_LEFT, location)
            .padding(4, 12, 4, 4)
            .overlay(tab.tabIcon().asIcon())
            .tooltip(new RichTooltip().addLine(Text.of(tab.title())));
    }

    private void selectTab(List<MekTerminalTab<?>> tabs, int index) {
        if (index >= 0 && index < tabs.size()) {
            tabs.get(index).select();
        }
    }

    private int selectedTabIndex(List<MekTerminalTab<?>> tabs) {
        for (int i = 0; i < tabs.size(); i++) {
            if (tabs.get(i).isSelected()) {
                return i;
            }
        }
        return 0;
    }

    private int tabLocation(int index, int size) {
        if (index == 0) {
            return -1;
        }
        if (index == size - 1) {
            return 1;
        }
        return 0;
    }
}
