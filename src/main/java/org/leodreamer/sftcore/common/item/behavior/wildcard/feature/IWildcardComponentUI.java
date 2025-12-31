package org.leodreamer.sftcore.common.item.behavior.wildcard.feature;

import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;

public interface IWildcardComponentUI {

    void createUILine(WidgetGroup line);

    default void onSave() {}
}
