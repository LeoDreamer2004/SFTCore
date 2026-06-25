package org.leodreamer.sftcore.common.item.wildcard.feature;

import org.leodreamer.sftcore.common.item.wildcard.WildcardPatternUIProvider;

import com.gregtechceu.gtceu.common.mui.GTGuiTextures;

import brachy.modularui.api.drawable.Text;
import brachy.modularui.api.widget.IWidget;
import brachy.modularui.utils.Alignment;
import brachy.modularui.utils.Color;
import brachy.modularui.value.sync.PanelSyncManager;
import brachy.modularui.widget.SingleChildWidget;
import brachy.modularui.widgets.ListWidget;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public abstract class WildcardComponentPage<T extends IWildcardComponentUI> {

    @Getter
    protected final List<T> components;
    private final String sampleSyncPrefix;
    private final PanelSyncManager syncManager;
    private SingleChildWidget<?> content;

    protected WildcardComponentPage(
        String sampleSyncPrefix,
        List<T> components,
        PanelSyncManager syncManager
    ) {
        this.sampleSyncPrefix = sampleSyncPrefix;
        this.syncManager = syncManager;
        this.components = createComponents(components);
    }

    public IWidget createWidget() {
        content = new SingleChildWidget<>()
            .width(WildcardPatternUIProvider.PAGE_WIDTH)
            .height(WildcardPatternUIProvider.EDITOR_HEIGHT);
        refreshContent();
        return content;
    }

    public void saveDrafts() {
        components.forEach(IWildcardComponentUI::onSave);
    }

    protected void addComponent(T component) {
        if (components.size() >= WildcardPatternUIProvider.MAX_COMPONENTS) {
            return;
        }
        saveDrafts();
        components.add(component);
        refreshContent();
    }

    protected abstract String title();

    protected abstract IWidget createAddButtons();

    private void refreshContent() {
        if (content != null) {
            content.child(createContent());
        }
    }

    private IWidget createContent() {
        var list = new ListWidget<>()
            .width(WildcardPatternUIProvider.PAGE_WIDTH)
            .height(WildcardPatternUIProvider.EDITOR_HEIGHT)
            .crossAxisAlignment(Alignment.CrossAxis.START)
            .background(GTGuiTextures.DISPLAY);

        list.child(Text.lang(title()).asWidget().height(16).color(Color.WHITE.main).marginTop(4).marginLeft(4));
        for (int i = 0; i < components.size(); i++) {
            list.child(createRow(i));
        }
        list.child(createAddButtons());
        return list;
    }

    private SingleChildWidget<?> createRow(int index) {
        var component = components.get(index);
        var key = sampleSyncPrefix + "_sample_" + index;
        return new SingleChildWidget<>()
            .width(WildcardPatternUIProvider.ROW_WIDTH)
            .height(24)
            .margin(2)
            .child(component.createLine(index + 1, () -> removeComponent(index), syncManager, key));
    }

    private void removeComponent(int index) {
        if (index < 0 || index >= components.size()) {
            return;
        }
        saveDrafts();
        components.remove(index);
        refreshContent();
    }

    private static <T extends IWildcardComponentUI> List<T> createComponents(List<T> components) {
        var result = new ArrayList<T>();
        for (var component : components) {
            if (component != null) {
                result.add(component);
                if (result.size() >= WildcardPatternUIProvider.MAX_COMPONENTS) {
                    break;
                }
            }
        }
        return result;
    }
}
