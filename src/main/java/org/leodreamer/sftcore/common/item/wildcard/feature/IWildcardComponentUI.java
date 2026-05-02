package org.leodreamer.sftcore.common.item.wildcard.feature;

import net.minecraft.network.chat.Component;

import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;

public interface IWildcardComponentUI {

    void createUILine(WidgetGroup line);

    Component createTooltip();

    default void onSave() {}
}
