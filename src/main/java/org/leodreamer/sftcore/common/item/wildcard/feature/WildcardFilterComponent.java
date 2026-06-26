package org.leodreamer.sftcore.common.item.wildcard.feature;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.wildcard.WildcardPatternUIProvider;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.common.mui.GTGuiTextures;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import brachy.modularui.api.drawable.Text;
import brachy.modularui.api.widget.IWidget;
import brachy.modularui.utils.Alignment;
import brachy.modularui.utils.Color;
import brachy.modularui.value.StringValue;
import brachy.modularui.widgets.layout.Flow;
import brachy.modularui.widgets.textfield.TextFieldWidget;
import lombok.Getter;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

@DataGenScanned
public abstract class WildcardFilterComponent extends WildcardComponentUI implements Predicate<Material> {

    @Getter
    protected boolean whitelist;

    protected WildcardFilterComponent(boolean whitelist) {
        this.whitelist = whitelist;
    }

    @RegisterLanguage("[Whitelist]")
    private static final String WHITELIST_KEY = "item.sftcore.wildcard_pattern.tooltip.filter.whitelist";

    @RegisterLanguage("[Blacklist]")
    private static final String BLACKLIST_KEY = "item.sftcore.wildcard_pattern.tooltip.filter.blacklist";

    @RegisterLanguage("Whitelist")
    private static final String WHITELIST_BUTTON_TOOLTIP = "item.sftcore.wildcard_pattern.ui.filter.whitelist";

    @RegisterLanguage("Blacklist")
    private static final String BLACKLIST_BUTTON_TOOLTIP = "item.sftcore.wildcard_pattern.ui.filter.blacklist";

    public MutableComponent whitelistTooltip() {
        return Component.translatable(isWhitelist() ? WHITELIST_KEY : BLACKLIST_KEY)
            .withStyle(ChatFormatting.GRAY);
    }

    protected IWidget createWhitelistButton(Flow row) {
        return WildcardPatternUIProvider.createButton(
            22, Text.dynamic(() -> Component.literal(whitelist ? "W" : "B")).scale(0.55f),
            () -> {
                whitelist = !whitelist;
                row.background(rowBackground());
            }
        ).tooltipDynamic(
            tooltip -> tooltip.addLine(
                Text.lang(whitelist ? WHITELIST_BUTTON_TOOLTIP : BLACKLIST_BUTTON_TOOLTIP)
            )
        );
    }

    protected TextFieldWidget detailField(
        Supplier<String> getter,
        Consumer<String> setter
    ) {
        return new TextFieldWidget()
            .size(84, 16)
            .value(new StringValue.Dynamic(getter, setter))
            .setMaxLength(96)
            .autoUpdateOnChange(true)
            .setTextAlignment(Alignment.Center)
            .setTextColor(Color.WHITE.main)
            .background(GTGuiTextures.DISPLAY);
    }
}
