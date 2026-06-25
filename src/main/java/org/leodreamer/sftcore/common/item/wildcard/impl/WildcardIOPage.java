package org.leodreamer.sftcore.common.item.wildcard.impl;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.wildcard.WildcardPatternUIProvider;
import org.leodreamer.sftcore.common.item.wildcard.feature.WildcardComponentPage;
import org.leodreamer.sftcore.common.item.wildcard.feature.WildcardIOComponent;

import brachy.modularui.api.drawable.Text;
import brachy.modularui.api.widget.IWidget;
import brachy.modularui.value.sync.PanelSyncManager;
import brachy.modularui.widget.SingleChildWidget;
import brachy.modularui.widgets.layout.Flow;

import java.util.List;
import java.util.regex.Pattern;

@DataGenScanned
public class WildcardIOPage extends WildcardComponentPage<WildcardIOComponent> {

    public static final Pattern AMOUNT_PATTERN = Pattern.compile("[0-9]*");

    private final String title;

    @RegisterLanguage("Create an ingredient with a fixed item")
    public static final String ADD_SINGLE_TOOLTIP = "item.sftcore.wildcard_pattern.ui.io.add_single";

    @RegisterLanguage("Create an ingredient with the GT tag")
    public static final String ADD_TAG_TOOLTIP = "item.sftcore.wildcard_pattern.ui.io.add_tag";

    public WildcardIOPage(
        String title,
        String sampleSyncPrefix,
        List<WildcardIOComponent> components,
        PanelSyncManager syncManager
    ) {
        super(sampleSyncPrefix, components, syncManager);
        this.title = title;
    }

    @Override
    protected String title() {
        return title;
    }

    @Override
    protected IWidget createAddButtons() {
        return new SingleChildWidget<>()
            .width(WildcardPatternUIProvider.ROW_WIDTH)
            .height(22)
            .margin(2)
            .child(
                Flow.row()
                    .height(22)
                    .child(
                        WildcardPatternUIProvider.createButton(
                            42, Text.lang(SimpleIOComponent.LABEL).scale(0.55f),
                            () -> addComponent(SimpleIOComponent.empty())
                        ).tooltipDynamic(tooltip -> tooltip.addLine(Text.lang(ADD_SINGLE_TOOLTIP)))
                    )
                    .child(
                        WildcardPatternUIProvider.createButton(
                            42, Text.lang(TagIOComponent.LABEL).scale(0.55f),
                            () -> addComponent(TagIOComponent.empty())
                        ).tooltipDynamic(tooltip -> tooltip.addLine(Text.lang(ADD_TAG_TOOLTIP)))
                    )
            );
    }
}
