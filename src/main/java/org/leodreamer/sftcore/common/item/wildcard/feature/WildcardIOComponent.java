package org.leodreamer.sftcore.common.item.wildcard.feature;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.wildcard.impl.WildcardIOPage;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;

import appeng.api.stacks.GenericStack;
import brachy.modularui.utils.Alignment;
import brachy.modularui.utils.Color;
import brachy.modularui.value.StringValue;
import brachy.modularui.widgets.textfield.TextFieldWidget;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

@DataGenScanned
public abstract class WildcardIOComponent extends IWildcardComponentUI {

    @RegisterLanguage("Delete this ingredient")
    public static final String DELETE_TOOLTIP = "item.sftcore.wildcard_pattern.ui.io.delete";

    public abstract @Nullable GenericStack apply(Material material);

    @Override
    protected String deleteTooltipKey() {
        return DELETE_TOOLTIP;
    }

    protected TextFieldWidget amountField(int width, Supplier<String> getter, Consumer<String> setter) {
        return new TextFieldWidget()
            .size(width, 16)
            .value(new StringValue.Dynamic(getter, setter))
            .setPattern(WildcardIOPage.AMOUNT_PATTERN)
            .setMaxLength(18)
            .autoUpdateOnChange(true)
            .setTextAlignment(Alignment.Center)
            .setTextColor(Color.WHITE.main);
    }

    protected static long parseLongAmount(String text) {
        if (text == null || text.isBlank()) {
            return 1;
        }
        try {
            return Math.max(1, Long.parseLong(text));
        } catch (NumberFormatException ignored) {
            return 1;
        }
    }

    protected static int parseIntAmount(String text) {
        return (int) Math.min(Integer.MAX_VALUE, parseLongAmount(text));
    }
}
