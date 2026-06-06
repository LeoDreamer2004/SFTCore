package org.leodreamer.sftcore.common.item.terminal.gui.impl;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.terminal.builder.impl.ThermalBoilerBuilder;
import org.leodreamer.sftcore.common.item.terminal.builder.impl.ThermalBoilerConfig;
import org.leodreamer.sftcore.common.item.terminal.gui.MekTerminalTab;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;

import java.util.function.Consumer;

@DataGenScanned
public final class ThermalBoilerTab
    extends MekTerminalTab<ThermalBoilerBuilder> {

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

    public ThermalBoilerTab(
        ThermalBoilerBuilder builder,
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
        var config = ThermalBoilerConfig.resolve(
            terminal.getOrCreateTag()
        );

        addIntRow(content, 22, Component.translatable(WIDTH), config::getWidth, config::setWidth);
        addIntRow(content, 40, Component.translatable(HEIGHT), config::getHeight, config::setHeight);
        addIntRow(content, 58, Component.translatable(DEPTH), config::getDepth, config::setDepth);
        addIntRow(content, 76, Component.translatable(LOWER_HEIGHT), config::getLowerHeight, config::setLowerHeight);
    }
}
