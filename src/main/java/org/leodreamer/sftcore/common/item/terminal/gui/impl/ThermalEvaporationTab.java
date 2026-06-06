package org.leodreamer.sftcore.common.item.terminal.gui.impl;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.terminal.builder.impl.ThermalEvaporationBuilder;
import org.leodreamer.sftcore.common.item.terminal.config.impl.ThermalEvaporationConfig;
import org.leodreamer.sftcore.common.item.terminal.gui.MekTerminalTab;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;

import java.util.function.Consumer;

@DataGenScanned
public final class ThermalEvaporationTab extends MekTerminalTab<ThermalEvaporationBuilder> {

    @RegisterLanguage("Thermal Evaporation Tower")
    public static final String TITLE = "item.sftcore.mek_terminal.tab.thermal_evaporation";

    @RegisterLanguage("Height")
    public static final String HEIGHT = "item.sftcore.mek_terminal.thermal_evaporation.height";

    public ThermalEvaporationTab(
        ThermalEvaporationBuilder builder,
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
        var config = ThermalEvaporationConfig.resolve(terminal.getOrCreateTag());

        addIntRow(content, 26, Component.translatable(HEIGHT), config.height::get, config.height::set);
    }
}
