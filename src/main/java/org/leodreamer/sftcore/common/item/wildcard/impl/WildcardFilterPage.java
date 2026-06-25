package org.leodreamer.sftcore.common.item.wildcard.impl;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.wildcard.WildcardPatternUIProvider;
import org.leodreamer.sftcore.common.item.wildcard.feature.WildcardComponentPage;
import org.leodreamer.sftcore.common.item.wildcard.feature.WildcardFilterComponent;

import brachy.modularui.api.drawable.Text;
import brachy.modularui.api.widget.IWidget;
import brachy.modularui.value.sync.PanelSyncManager;
import brachy.modularui.widget.SingleChildWidget;
import brachy.modularui.widgets.layout.Flow;

import java.util.List;
import java.util.regex.Pattern;

@DataGenScanned
public class WildcardFilterPage extends WildcardComponentPage<WildcardFilterComponent> {

    public static final Pattern ID_PATTERN = Pattern.compile("[a-zA-Z0-9_.:/-]*");

    @RegisterLanguage("Filters")
    public static final String TITLE = "item.sftcore.wildcard_pattern.ui.filter.page_title";

    @RegisterLanguage("Single")
    public static final String SINGLE_LABEL = "item.sftcore.wildcard_pattern.ui.filter.single";

    @RegisterLanguage("Prop")
    public static final String PROPERTY_SHORT_LABEL = "item.sftcore.wildcard_pattern.ui.filter.property_short";

    @RegisterLanguage("Create a filter of a fixed material")
    public static final String ADD_SINGLE_TOOLTIP = "item.sftcore.wildcard_pattern.ui.filter.add_single";

    @RegisterLanguage("Create a filter of materials with the given property")
    public static final String ADD_PROPERTY_TOOLTIP = "item.sftcore.wildcard_pattern.ui.filter.add_property";

    @RegisterLanguage("(ADVANCED) Create a filter of materials with the given flag")
    public static final String ADD_FLAG_TOOLTIP = "item.sftcore.wildcard_pattern.ui.filter.add_flag";

    public WildcardFilterPage(
        String sampleSyncPrefix,
        List<WildcardFilterComponent> components,
        PanelSyncManager syncManager
    ) {
        super(sampleSyncPrefix, components, syncManager);
    }

    @Override
    protected String title() {
        return TITLE;
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
                            42, Text.lang(SINGLE_LABEL).scale(0.55f),
                            () -> addComponent(SimpleFilterComponent.empty())
                        ).tooltipDynamic(tooltip -> tooltip.addLine(Text.lang(ADD_SINGLE_TOOLTIP)))
                    )
                    .child(
                        WildcardPatternUIProvider.createButton(
                            42, Text.lang(PROPERTY_SHORT_LABEL).scale(0.55f),
                            () -> addComponent(PropertyFilterComponent.empty())
                        ).tooltipDynamic(
                            tooltip -> tooltip.addLine(Text.lang(ADD_PROPERTY_TOOLTIP))
                        )
                    )
                    .child(
                        WildcardPatternUIProvider.createButton(
                            42, Text.lang(FlagFilterComponent.LABEL).scale(0.55f),
                            () -> addComponent(FlagFilterComponent.empty())
                        ).tooltipDynamic(
                            tooltip -> tooltip.addLine(Text.lang(ADD_FLAG_TOOLTIP))
                        )
                    )
            );
    }
}
