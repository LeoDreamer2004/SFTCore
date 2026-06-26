package org.leodreamer.sftcore.common.item.wildcard.feature;

import org.leodreamer.sftcore.common.item.wildcard.WildcardPatternUIProvider;

import com.gregtechceu.gtceu.common.mui.GTGuiTextures;

import brachy.modularui.api.ISyncedAction;
import brachy.modularui.api.drawable.Text;
import brachy.modularui.api.widget.IWidget;
import brachy.modularui.utils.Alignment;
import brachy.modularui.utils.Color;
import brachy.modularui.value.sync.PanelSyncManager;
import brachy.modularui.widget.SingleChildWidget;
import brachy.modularui.widgets.ButtonWidget;
import brachy.modularui.widgets.ListWidget;
import brachy.modularui.widgets.layout.Flow;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class WildcardComponentPage<T extends WildcardComponentUI> {

    public static final int MAX_COMPONENTS = 6;

    @Getter
    protected final List<T> components;
    protected final String syncPrefix;
    protected final PanelSyncManager syncManager;

    private final String title;
    private SingleChildWidget<?> content;
    private final List<Supplier<ButtonWidget<?>>> addButtonSuppliers = new ArrayList<>();

    private static String removeAction(int index) {
        return "remove_" + index;
    }

    protected WildcardComponentPage(
        String title,
        String syncPrefix,
        List<T> components,
        PanelSyncManager syncManager
    ) {
        this.title = title;
        this.syncPrefix = syncPrefix;
        this.syncManager = syncManager;
        this.components = components.stream()
            .filter(Objects::nonNull)
            .limit(MAX_COMPONENTS)
            .collect(Collectors.toCollection(ArrayList::new));

        for (int i = 0; i < MAX_COMPONENTS; i++) {
            int index = i;
            registerPageAction(removeAction(index), packet -> removeComponent(index));
        }
    }

    public IWidget createWidget() {
        content = new SingleChildWidget<>()
            .width(WildcardPatternUIProvider.PAGE_WIDTH)
            .height(WildcardPatternUIProvider.EDITOR_HEIGHT);
        refreshContent();
        return content;
    }

    private void refreshContent() {
        if (content == null) {
            return;
        }

        var list = new ListWidget<>()
            .width(WildcardPatternUIProvider.PAGE_WIDTH)
            .height(WildcardPatternUIProvider.EDITOR_HEIGHT)
            .crossAxisAlignment(Alignment.CrossAxis.START)
            .background(GTGuiTextures.DISPLAY);

        // add buttons
        var buttons = Flow.row()
            .height(18)
            .childPadding(2);
        for (var button : addButtonSuppliers) {
            buttons.child(button.get());
        }

        // header
        list.child(
            Flow.row()
                .width(WildcardPatternUIProvider.ROW_WIDTH)
                .height(22)
                .margin(2)
                .crossAxisAlignment(Alignment.CrossAxis.CENTER)
                .child(Text.lang(title).asWidget().height(16).color(Color.WHITE.main).expanded())
                .child(buttons)
        );

        // lines
        for (int i = 0; i < components.size(); i++) {
            var component = components.get(i);
            String key = syncPrefix + "_sample_" + i;
            int index = i;
            list.child(
                new SingleChildWidget<>()
                    .width(WildcardPatternUIProvider.ROW_WIDTH)
                    .height(24)
                    .margin(2)
                    .child(component.createLine(i + 1, () -> callPageAction(removeAction(index)), syncManager, key))
            );
        }
        content.child(list);
    }

    protected void addComponent(T component) {
        if (components.size() >= MAX_COMPONENTS) {
            return;
        }
        saveDrafts();
        components.add(component);
        refreshContent();
    }

    private void removeComponent(int index) {
        if (index < 0 || index >= components.size()) {
            return;
        }
        saveDrafts();
        components.remove(index);
        refreshContent();
    }

    public void saveDrafts() {
        components.forEach(WildcardComponentUI::onSave);
    }

    protected void register(String action, Supplier<T> componentSupplier, String label, String tooltip) {
        registerPageAction(action, packet -> addComponent(componentSupplier.get()));
        addButtonSuppliers.add(
            () -> WildcardPatternUIProvider.createButton(
                46, Text.lang(label).scale(0.72f),
                () -> callPageAction(action)
            ).tooltipDynamic(tooltips -> tooltips.addLine(Text.lang(tooltip)))
        );
    }

    protected void registerPageAction(String action, ISyncedAction handler) {
        syncManager.registerSyncedAction(syncPrefix + "_" + action, handler);
    }

    protected void callPageAction(String action) {
        syncManager.callSyncedAction(syncPrefix + "_" + action);
    }
}
