package org.leodreamer.sftcore.common.item.wildcard.impl;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.wildcard.WildcardPatternLogic;
import org.leodreamer.sftcore.common.item.wildcard.feature.WildcardComponentPage;
import org.leodreamer.sftcore.common.item.wildcard.feature.WildcardFilterComponent;

import brachy.modularui.value.sync.PanelSyncManager;

@DataGenScanned
public class WildcardFilterPage extends WildcardComponentPage<WildcardFilterComponent> {

    @RegisterLanguage("Filters Configurations")
    public static final String TITLE = "item.sftcore.wildcard_pattern.ui.filter.page_title";

    @RegisterLanguage("Create a filter of a fixed material")
    public static final String ADD_SINGLE_TOOLTIP = "item.sftcore.wildcard_pattern.ui.filter.add_single";

    @RegisterLanguage("Create a filter of materials with the given property")
    public static final String ADD_PROPERTY_TOOLTIP = "item.sftcore.wildcard_pattern.ui.filter.add_property";

    @RegisterLanguage("(ADVANCED) Create a filter of materials with the given flag")
    public static final String ADD_FLAG_TOOLTIP = "item.sftcore.wildcard_pattern.ui.filter.add_flag";

    public WildcardFilterPage(
        WildcardPatternLogic logic,
        PanelSyncManager syncManager
    ) {
        super(TITLE, "filter", logic.getFilterComponents(), syncManager);
        register("add_single", SimpleFilterComponent::empty, SimpleFilterComponent.LABEL, ADD_SINGLE_TOOLTIP);
        register("add_property", PropertyFilterComponent::empty, PropertyFilterComponent.LABEL, ADD_PROPERTY_TOOLTIP);
        register("add_flag", FlagFilterComponent::empty, FlagFilterComponent.LABEL, ADD_FLAG_TOOLTIP);
    }
}
