package org.leodreamer.sftcore.common.item.terminal.gui.impl;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.terminal.builder.impl.FissionReactorBuilder;
import org.leodreamer.sftcore.common.item.terminal.config.impl.FissionReactorConfig;
import org.leodreamer.sftcore.common.item.terminal.gui.MekTerminalTab;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import brachy.modularui.value.sync.PanelSyncManager;
import brachy.modularui.widgets.layout.Flow;

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

    public FissionReactorTab(FissionReactorBuilder builder, ItemStack terminal, PanelSyncManager syncManager) {
        super(builder, terminal, syncManager);
    }

    @Override
    public Component title() {
        return Component.translatable(TITLE);
    }

    @Override
    protected Flow createContent(Flow container) {
        var config = FissionReactorConfig.resolve(terminal.getOrCreateTag());
        return container.child(intRow("width", Component.translatable(WIDTH), config.width::get, config.width::set))
            .child(intRow("height", Component.translatable(HEIGHT), config.height::get, config.height::set))
            .child(intRow("depth", Component.translatable(DEPTH), config.depth::get, config.depth::set))
            .child(wrappedText(CHECKERBOARD, 24));
    }
}
