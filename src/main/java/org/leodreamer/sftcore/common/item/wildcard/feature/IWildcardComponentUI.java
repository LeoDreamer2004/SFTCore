package org.leodreamer.sftcore.common.item.wildcard.feature;

import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import net.minecraft.network.chat.Component;

public interface IWildcardComponentUI {

    void createUILine(WidgetGroup line);

    Component createTooltip();

    default void onSave() {
    }
}
