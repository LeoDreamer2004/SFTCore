package org.leodreamer.sftcore.common.item.wildcard;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.wildcard.impl.WildcardFilterPage;
import org.leodreamer.sftcore.common.item.wildcard.impl.WildcardIOPage;

import com.gregtechceu.gtceu.api.mui.IItemUIHolder;
import com.gregtechceu.gtceu.common.mui.GTGuiTextures;

import net.minecraft.ChatFormatting;
import net.minecraft.world.item.ItemStack;

import brachy.modularui.api.drawable.IDrawable;
import brachy.modularui.api.drawable.Text;
import brachy.modularui.drawable.GuiTextures;
import brachy.modularui.factory.PlayerInventoryGuiData;
import brachy.modularui.screen.ModularPanel;
import brachy.modularui.screen.RichTooltip;
import brachy.modularui.screen.UISettings;
import brachy.modularui.value.sync.PanelSyncManager;
import brachy.modularui.widgets.ButtonWidget;
import brachy.modularui.widgets.PageButton;
import brachy.modularui.widgets.PagedWidget;
import brachy.modularui.widgets.layout.Flow;
import com.mojang.blaze3d.platform.InputConstants;

import java.util.function.Consumer;

@DataGenScanned
public class WildcardPatternUIProvider implements IItemUIHolder {

    public static final String PANEL_NAME = "wildcard_pattern";
    private static final String SAVE_ACTION = "wildcard_pattern_save";
    public static final int MAX_COMPONENTS = 6;
    public static final int EDITOR_HEIGHT = 252;
    public static final int PAGE_WIDTH = 214;
    public static final int ROW_WIDTH = 205;

    private final ItemStack stack;
    private final Consumer<ItemStack> onSave;
    private final WildcardPatternLogic logic;
    private WildcardIOPage inputPage;
    private WildcardIOPage outputPage;
    private WildcardFilterPage filterPage;

    @RegisterLanguage("Save")
    public static final String SAVE_LABEL = "item.sftcore.wildcard_pattern.ui.save";

    @RegisterLanguage("IN")
    public static final String INPUT_TAB_LABEL = "item.sftcore.wildcard_pattern.ui.tab.input";

    @RegisterLanguage("OUT")
    public static final String OUTPUT_TAB_LABEL = "item.sftcore.wildcard_pattern.ui.tab.output";

    @RegisterLanguage("FIL")
    public static final String FILTER_TAB_LABEL = "item.sftcore.wildcard_pattern.ui.tab.filter";

    @RegisterLanguage("Input Configuration")
    public static final String INPUT_TITLE = "item.sftcore.wildcard_pattern.ui.input.title";

    @RegisterLanguage("Output Configuration")
    public static final String OUTPUT_TITLE = "item.sftcore.wildcard_pattern.ui.output.title";

    @RegisterLanguage("Material Filter Configuration")
    public static final String FILTER_TITLE = "item.sftcore.wildcard_pattern.ui.filter.title";

    public WildcardPatternUIProvider(ItemStack stack, Consumer<ItemStack> onSave) {
        this.stack = stack;
        this.onSave = onSave;
        this.logic = WildcardPatternLogic.on(stack);
    }

    @Override
    public ModularPanel<?> buildUI(
        PlayerInventoryGuiData<?> guiData, PanelSyncManager syncManager, UISettings settings
    ) {
        syncManager.registerServerSyncedAction(SAVE_ACTION, packet -> save());
        this.inputPage = new WildcardIOPage(
            INPUT_TITLE,
            "input",
            logic.getIOComponents(WildcardPatternLogic.IO.IN),
            syncManager
        );
        this.outputPage = new WildcardIOPage(
            OUTPUT_TITLE,
            "output",
            logic.getIOComponents(WildcardPatternLogic.IO.OUT),
            syncManager
        );
        this.filterPage = new WildcardFilterPage("filter", logic.getFilterComponents(), syncManager);

        var tabController = new PagedWidget.Controller();
        var editor = new PagedWidget<>()
            .size(PAGE_WIDTH, EDITOR_HEIGHT)
            .controller(tabController)
            .addPage(inputPage.createWidget())
            .addPage(outputPage.createWidget())
            .addPage(filterPage.createWidget());

        var mainContent = Flow.column()
            .background(GTGuiTextures.BACKGROUND)
            .padding(5)
            .childPadding(4)
            .coverChildren()
            .child(Text.of(stack.getHoverName()).asWidget().horizontalCenter())
            .child(editor)
            .child(
                createButton(
                    54, Text.lang(SAVE_LABEL).style(ChatFormatting.WHITE).scale(0.55f),
                    () -> syncManager.callSyncedAction(SAVE_ACTION)
                ).horizontalCenter()
            );
        var panel = new ModularPanel<>(PANEL_NAME)
            .background(IDrawable.EMPTY)
            .disableHoverBackground()
            .child(mainContent)
            .coverChildren();

        // tab column
        panel.child(
            Flow.column()
                .coverChildren()
                .child(createTabButton(0, -1, tabController, INPUT_TAB_LABEL, INPUT_TITLE))
                .child(createTabButton(1, 0, tabController, OUTPUT_TAB_LABEL, OUTPUT_TITLE))
                .child(createTabButton(2, 1, tabController, FILTER_TAB_LABEL, FILTER_TITLE))
                .relative(mainContent)
                .rightRel(1.0f)
                .top(0)
                .decoration()
        );
        return panel;
    }

    private PageButton createTabButton(
        int index,
        int location,
        PagedWidget.Controller tabController,
        String labelKey,
        String tooltipKey
    ) {
        return new PageButton(index, tabController)
            .tab(GuiTextures.TAB_LEFT, location)
            .overlay(Text.lang(labelKey).style(ChatFormatting.WHITE).scale(0.55f))
            .tooltip(new RichTooltip().addLine(Text.lang(tooltipKey)));
    }

    public static ButtonWidget<?> createButton(int width, IDrawable label, Runnable action) {
        return new ButtonWidget<>()
            .size(width, 18)
            .overlay(label)
            .onMousePressed((context, button) -> {
                if (button == InputConstants.MOUSE_BUTTON_LEFT) {
                    action.run();
                    return true;
                }
                return false;
            });
    }

    private void save() {
        inputPage.saveDrafts();
        outputPage.saveDrafts();
        filterPage.saveDrafts();
        logic.setIOComponents(WildcardPatternLogic.IO.IN, inputPage.getComponents());
        logic.setIOComponents(WildcardPatternLogic.IO.OUT, outputPage.getComponents());
        logic.setFilterComponents(filterPage.getComponents());
        onSave.accept(stack);
    }
}
