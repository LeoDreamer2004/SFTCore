package org.leodreamer.sftcore.common.item.terminal.gui.impl;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.terminal.builder.impl.IndustrialTurbineBuilder;
import org.leodreamer.sftcore.common.item.terminal.builder.impl.IndustrialTurbineConfig;
import org.leodreamer.sftcore.common.item.terminal.gui.MekTerminalTab;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;

import java.util.function.Consumer;

@DataGenScanned
public final class IndustrialTurbineTab
    extends MekTerminalTab<IndustrialTurbineBuilder> {

    @RegisterLanguage("Industrial Turbine")
    public static final String TITLE = "item.sftcore.mek_terminal.tab.industrial_turbine";

    @RegisterLanguage("Base Width")
    public static final String WIDTH = "item.sftcore.mek_terminal.industrial_turbine.width";

    @RegisterLanguage("Total Height")
    public static final String HEIGHT = "item.sftcore.mek_terminal.industrial_turbine.height";

    @RegisterLanguage("Turbine Rotors")
    public static final String ROTORS = "item.sftcore.mek_terminal.industrial_turbine.rotors";

    @RegisterLanguage(
        "Install Turbine Blades manually. Saturating Condensers do not need to fill the entire upper chamber."
    )
    public static final String HINT = "item.sftcore.mek_terminal.industrial_turbine.hint";

    public IndustrialTurbineTab(
        IndustrialTurbineBuilder builder,
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
    protected void addContentWidgets(
        WidgetGroup content
    ) {
        var config = IndustrialTurbineConfig.resolve(terminal.getOrCreateTag());

        addIntRow(content, 22, Component.translatable(WIDTH), config::getWidth, config::setWidth);
        addIntRow(content, 40, Component.translatable(HEIGHT), config::getHeight, config::setHeight);
        addIntRow(content, 58, Component.translatable(ROTORS), config::getRotorCount, config::setRotorCount);

        addWrappedText(content, 12, 78, 120, 28, HINT);
    }
}
