package org.leodreamer.sftcore.common.item.terminal.gui.impl;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.terminal.builder.impl.SPSBuilder;
import org.leodreamer.sftcore.common.item.terminal.gui.MekTerminalTab;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;

import java.util.function.Consumer;

@DataGenScanned
public final class SPSTab extends MekTerminalTab<SPSBuilder> {

    @RegisterLanguage("Supercritical Phase Shifter")
    public static final String TITLE = "item.sftcore.mek_terminal.tab.sps";

    @RegisterLanguage("Fixed 7x7x7 pattern")
    public static final String FIXED_PATTERN = "item.sftcore.mek_terminal.sps.fixed_pattern";

    @RegisterLanguage("Shift right-click the bottom north center SPS Casing")
    public static final String START_HINT = "item.sftcore.mek_terminal.sps.start_hint";

    public SPSTab(
        SPSBuilder builder,
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
        addWrappedText(content, 12, 26, 134, 14, FIXED_PATTERN);
        addWrappedText(content, 12, 44, 134, 30, START_HINT);
    }
}
