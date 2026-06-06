package org.leodreamer.sftcore.common.item.terminal.gui.impl;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.terminal.builder.impl.InductionMatrixBuilder;
import org.leodreamer.sftcore.common.item.terminal.builder.impl.InductionMatrixConfig;
import org.leodreamer.sftcore.common.item.terminal.gui.MekTerminalTab;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;

import java.util.function.Consumer;

@DataGenScanned
public final class InductionMatrixTab extends MekTerminalTab<InductionMatrixBuilder> {

    @RegisterLanguage("Induction Matrix")
    public static final String TITLE = "item.sftcore.mek_terminal.tab.induction";

    @RegisterLanguage("Width")
    public static final String WIDTH = "item.sftcore.mek_terminal.induction_matrix.width";

    @RegisterLanguage("Height")
    public static final String HEIGHT = "item.sftcore.mek_terminal.induction_matrix.height";

    @RegisterLanguage("Depth")
    public static final String DEPTH = "item.sftcore.mek_terminal.induction_matrix.depth";

    public InductionMatrixTab(
        InductionMatrixBuilder builder,
        ItemStack terminal,
        Consumer<ItemStack> onSave
    ) {
        super(builder, terminal, onSave);
    }

    @Override
    public Component getTitle() {
        return Component.translatable(TITLE);
    }

    @Override
    protected void addContentWidgets(WidgetGroup content) {
        var config = InductionMatrixConfig.resolve(terminal.getOrCreateTag());

        addIntRow(content, 26, Component.translatable(WIDTH), config::getWidth, config::setWidth);
        addIntRow(content, 46, Component.translatable(HEIGHT), config::getHeight, config::setHeight);
        addIntRow(content, 66, Component.translatable(DEPTH), config::getDepth, config::setDepth);
    }
}
