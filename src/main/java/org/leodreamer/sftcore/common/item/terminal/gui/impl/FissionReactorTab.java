package org.leodreamer.sftcore.common.item.terminal.gui.impl;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.terminal.builder.impl.FissionReactorBuilder;
import org.leodreamer.sftcore.common.item.terminal.config.impl.FissionReactorConfig;
import org.leodreamer.sftcore.common.item.terminal.gui.MekTerminalTab;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;

import java.util.function.Consumer;

@DataGenScanned
public final class FissionReactorTab extends MekTerminalTab<FissionReactorBuilder> {

    @RegisterLanguage("Fission Reactor")
    public static final String TITLE = "item.sftcore.mek_terminal.tab.fission_reactor";

    @RegisterLanguage("Width")
    public static final String WIDTH = "item.sftcore.mek_terminal.fission_reactor.width";

    @RegisterLanguage("Height")
    public static final String HEIGHT = "item.sftcore.mek_terminal.fission_reactor.height";

    @RegisterLanguage("Depth")
    public static final String DEPTH = "item.sftcore.mek_terminal.fission_reactor.depth";

    @RegisterLanguage("Fuel columns use checkerboard layout")
    public static final String CHECKERBOARD = "item.sftcore.mek_terminal.fission_reactor.checkerboard";

    public FissionReactorTab(
        FissionReactorBuilder builder,
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
        var config = FissionReactorConfig.resolve(terminal.getOrCreateTag());

        addIntRow(content, 22, Component.translatable(WIDTH), config.width::get, config.width::set);
        addIntRow(content, 40, Component.translatable(HEIGHT), config.height::get, config.height::set);
        addIntRow(content, 58, Component.translatable(DEPTH), config.depth::get, config.depth::set);

        addWrappedText(content, 12, 84, 134, 14, CHECKERBOARD);
    }
}
