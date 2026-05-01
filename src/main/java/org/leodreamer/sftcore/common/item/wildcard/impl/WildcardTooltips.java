package org.leodreamer.sftcore.common.item.wildcard.impl;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.wildcard.WildcardPatternLogic;

import java.util.List;

@DataGenScanned
public class WildcardTooltips {
    private final WildcardPatternLogic logic;

    @RegisterLanguage("Input: ")
    private static final String INPUT_TOOLTIP_KEY = "sftcore.item.wildcard_pattern.tooltip.input";

    @RegisterLanguage("Output: ")
    private static final String OUTPUT_TOOLTIP_KEY = "sftcore.item.wildcard_pattern.tooltip.output";

    @RegisterLanguage("Filter: ")
    private static final String FILTER_TOOLTIP_KEY = "sftcore.item.wildcard_pattern.tooltip.filter";

    public WildcardTooltips(WildcardPatternLogic logic) {
        this.logic = logic;
    }

    public void createTooltips(List<Component> tooltips) {
        for (var input : logic.getIOComponents(WildcardPatternLogic.IO.IN)) {
            tooltips.add(Component.translatable(INPUT_TOOLTIP_KEY)
                .withStyle(ChatFormatting.AQUA)
                .append(input.createTooltip()));
        }
        for (var output : logic.getIOComponents(WildcardPatternLogic.IO.OUT)) {
            tooltips.add(Component.translatable(OUTPUT_TOOLTIP_KEY)
                .withStyle(ChatFormatting.GREEN)
                .append(output.createTooltip()));
        }
        for (var filter : logic.getFilterComponents()) {
            tooltips.add(Component.translatable(FILTER_TOOLTIP_KEY)
                .withStyle(ChatFormatting.GOLD)
                .append(filter.createTooltip()));
        }
    }
}
