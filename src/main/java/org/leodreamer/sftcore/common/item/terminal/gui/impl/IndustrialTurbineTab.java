package org.leodreamer.sftcore.common.item.terminal.gui.impl;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.terminal.builder.impl.IndustrialTurbineBuilder;
import org.leodreamer.sftcore.common.item.terminal.config.impl.IndustrialTurbineConfig;
import org.leodreamer.sftcore.common.item.terminal.gui.MekTerminalTab;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import brachy.modularui.value.sync.PanelSyncManager;
import brachy.modularui.widgets.layout.Flow;

@DataGenScanned
public final class IndustrialTurbineTab extends MekTerminalTab<IndustrialTurbineBuilder> {

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

    public IndustrialTurbineTab(IndustrialTurbineBuilder builder, ItemStack terminal, PanelSyncManager syncManager) {
        super(builder, terminal, syncManager);
    }

    @Override
    public Component title() {
        return Component.translatable(TITLE);
    }

    @Override
    protected Flow createContent(Flow container) {
        var config = IndustrialTurbineConfig.resolve(terminal.getOrCreateTag());
        return container.child(intRow("width", WIDTH, config::getWidth, config::setWidth))
            .child(intRow("height", HEIGHT, config.height::get, config.height::set))
            .child(intRow("rotors", ROTORS, config.rotors::get, config.rotors::set))
            .child(wrappedText(HINT, 48));
    }
}
