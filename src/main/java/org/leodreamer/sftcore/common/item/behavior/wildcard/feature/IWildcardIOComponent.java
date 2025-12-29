package org.leodreamer.sftcore.common.item.behavior.wildcard.feature;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;

import appeng.api.stacks.GenericStack;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import org.jetbrains.annotations.Nullable;

public interface IWildcardIOComponent {

    @Nullable
    GenericStack apply(Material material);

    IWildcardIOSerializer getSerializer();

    void createUILine(WidgetGroup line);

    default void onSave() {};
}
