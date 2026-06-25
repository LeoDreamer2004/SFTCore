package org.leodreamer.sftcore.common.item.terminal.gui.impl;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.terminal.builder.impl.ThermalBoilerBuilder;
import org.leodreamer.sftcore.common.item.terminal.config.impl.ThermalBoilerConfig;
import org.leodreamer.sftcore.common.item.terminal.gui.MekTerminalTab;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import brachy.modularui.value.sync.PanelSyncManager;
import brachy.modularui.widgets.layout.Flow;

@DataGenScanned
public final class ThermalBoilerTab extends MekTerminalTab<ThermalBoilerBuilder> {

    @RegisterLanguage("Thermoelectric Boiler")
    public static final String TITLE = "item.sftcore.mek_terminal.tab.thermal_boiler";

    @RegisterLanguage("Width")
    public static final String WIDTH = "item.sftcore.mek_terminal.thermal_boiler.width";

    @RegisterLanguage("Height")
    public static final String HEIGHT = "item.sftcore.mek_terminal.thermal_boiler.height";

    @RegisterLanguage("Depth")
    public static final String DEPTH = "item.sftcore.mek_terminal.thermal_boiler.depth";

    @RegisterLanguage("Lower Layers")
    public static final String LOWER_HEIGHT = "item.sftcore.mek_terminal.thermal_boiler.lower_height";

    public ThermalBoilerTab(ThermalBoilerBuilder builder, ItemStack terminal, PanelSyncManager syncManager) {
        super(builder, terminal, syncManager);
    }

    @Override
    public Component title() {
        return Component.translatable(TITLE);
    }

    @Override
    protected Flow createContent(Flow container) {
        var config = ThermalBoilerConfig.resolve(terminal.getOrCreateTag());
        return container.child(intRow("width", Component.translatable(WIDTH), config.width::get, config.width::set))
            .child(intRow("height", Component.translatable(HEIGHT), config.height::get, config.height::set))
            .child(intRow("depth", Component.translatable(DEPTH), config.depth::get, config.depth::set))
            .child(
                intRow(
                    "lower", Component.translatable(LOWER_HEIGHT), config.lowerHeight::get, config.lowerHeight::set
                )
            );
    }
}
