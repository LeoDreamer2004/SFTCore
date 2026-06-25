package org.leodreamer.sftcore.common.item.terminal.gui.impl;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.terminal.builder.impl.ThermalEvaporationBuilder;
import org.leodreamer.sftcore.common.item.terminal.config.impl.ThermalEvaporationConfig;
import org.leodreamer.sftcore.common.item.terminal.gui.MekTerminalTab;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import brachy.modularui.value.sync.PanelSyncManager;
import brachy.modularui.widgets.layout.Flow;

@DataGenScanned
public final class ThermalEvaporationTab extends MekTerminalTab<ThermalEvaporationBuilder> {

    @RegisterLanguage("Thermal Evaporation Tower")
    public static final String TITLE = "item.sftcore.mek_terminal.tab.thermal_evaporation";

    @RegisterLanguage("Height")
    public static final String HEIGHT = "item.sftcore.mek_terminal.thermal_evaporation.height";

    public ThermalEvaporationTab(ThermalEvaporationBuilder builder, ItemStack terminal, PanelSyncManager syncManager) {
        super(builder, terminal, syncManager);
    }

    @Override
    public Component title() {
        return Component.translatable(TITLE);
    }

    @Override
    protected Flow createContent(Flow container) {
        var config = ThermalEvaporationConfig.resolve(terminal.getOrCreateTag());
        return container
            .child(intRow("height", Component.translatable(HEIGHT), config.height::get, config.height::set));
    }
}
