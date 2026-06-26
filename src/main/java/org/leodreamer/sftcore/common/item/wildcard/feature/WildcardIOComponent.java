package org.leodreamer.sftcore.common.item.wildcard.feature;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.common.mui.GTGuiTextures;

import appeng.api.stacks.GenericStack;
import brachy.modularui.utils.Alignment;
import brachy.modularui.utils.Color;
import brachy.modularui.value.StringValue;
import brachy.modularui.widgets.textfield.TextFieldWidget;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public abstract class WildcardIOComponent extends WildcardComponentUI {

    public static final Pattern AMOUNT_PATTERN = Pattern.compile("[0-9]*");

    public abstract @Nullable GenericStack apply(Material material);

    protected TextFieldWidget amountField(int width, Supplier<String> getter, Consumer<String> setter) {
        return new TextFieldWidget()
            .size(width, 16)
            .value(new StringValue.Dynamic(getter, setter))
            .setPattern(AMOUNT_PATTERN)
            .setMaxLength(18)
            .autoUpdateOnChange(true)
            .setTextAlignment(Alignment.Center)
            .setTextColor(Color.WHITE.main)
            .background(GTGuiTextures.DISPLAY);
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
