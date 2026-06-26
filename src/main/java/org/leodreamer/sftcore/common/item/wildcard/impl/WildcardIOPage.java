package org.leodreamer.sftcore.common.item.wildcard.impl;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.wildcard.WildcardPatternLogic;
import org.leodreamer.sftcore.common.item.wildcard.feature.WildcardComponentPage;
import org.leodreamer.sftcore.common.item.wildcard.feature.WildcardIOComponent;

import brachy.modularui.value.sync.PanelSyncManager;

@DataGenScanned
public class WildcardIOPage extends WildcardComponentPage<WildcardIOComponent> {

    @RegisterLanguage("Create an ingredient with a fixed item")
    public static final String ADD_SINGLE_TOOLTIP = "item.sftcore.wildcard_pattern.ui.io.add_single";

    @RegisterLanguage("Create an ingredient with the GT tag")
    public static final String ADD_TAG_TOOLTIP = "item.sftcore.wildcard_pattern.ui.io.add_tag";

    @RegisterLanguage("Input Configuration")
    public static final String INPUT_TITLE = "item.sftcore.wildcard_pattern.ui.input.title";

    @RegisterLanguage("Output Configuration")
    public static final String OUTPUT_TITLE = "item.sftcore.wildcard_pattern.ui.output.title";

    public WildcardIOPage(
        WildcardPatternLogic logic,
        WildcardPatternLogic.IO io,
        PanelSyncManager syncManager
    ) {
        super(
            io == WildcardPatternLogic.IO.IN ? INPUT_TITLE : OUTPUT_TITLE,
            io.key,
            logic.getIOComponents(io),
            syncManager
        );
        register("add_single", SimpleIOComponent::empty, SimpleIOComponent.LABEL, ADD_SINGLE_TOOLTIP);
        register("add_tag", TagIOComponent::empty, TagIOComponent.LABEL, ADD_TAG_TOOLTIP);
    }
}
