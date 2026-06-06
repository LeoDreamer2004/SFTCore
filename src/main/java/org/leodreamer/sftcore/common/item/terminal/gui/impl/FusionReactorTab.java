package org.leodreamer.sftcore.common.item.terminal.gui.impl;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.terminal.builder.impl.FusionReactorBuilder;
import org.leodreamer.sftcore.common.item.terminal.gui.MekTerminalTab;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;

import java.util.function.Consumer;

@DataGenScanned
public final class FusionReactorTab
    extends MekTerminalTab<FusionReactorBuilder> {

    @RegisterLanguage("Fusion Reactor")
    public static final String TITLE = "item.sftcore.mek_terminal.tab.fusion_reactor";

    @RegisterLanguage("Fixed 5x5x5 pattern")
    public static final String FIXED_PATTERN = "item.sftcore.mek_terminal.fusion_reactor.fixed_pattern";

    @RegisterLanguage("Shift right-click the top-center Fusion Reactor Controller")
    public static final String START_HINT = "item.sftcore.mek_terminal.fusion_reactor.start_hint";

    @RegisterLanguage("The four horizontal face centers prefer Fusion Reactor Ports")
    public static final String PORT_HINT = "item.sftcore.mek_terminal.fusion_reactor.port_hint";

    public FusionReactorTab(
        FusionReactorBuilder builder,
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
        addWrappedText(content, 12, 44, 134, 28, START_HINT);
        addWrappedText(content, 12, 76, 134, 28, PORT_HINT);
    }
}
