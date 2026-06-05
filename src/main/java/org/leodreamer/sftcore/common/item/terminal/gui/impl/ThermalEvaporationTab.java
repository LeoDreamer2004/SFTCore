package org.leodreamer.sftcore.common.item.terminal.gui.impl;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.terminal.MekBuilderRegistry;
import org.leodreamer.sftcore.common.item.terminal.builder.impl.ThermalEvaporationBuilder;
import org.leodreamer.sftcore.common.item.terminal.builder.impl.ThermalEvaporationConfig;
import org.leodreamer.sftcore.common.item.terminal.gui.MekTerminalTab;

import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ItemStackTexture;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import mekanism.common.registries.MekanismBlocks;

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
    public IGuiTexture getTabIcon() {
        return new ItemStackTexture(
            new ItemStack(MekanismBlocks.THERMAL_EVAPORATION_CONTROLLER.getBlock())
        );
    }

    @Override
    public Widget createMainPage(FancyMachineUIWidget ui) {
        MekBuilderRegistry.setSelected(terminal, builder);
        onSave.accept(terminal);

        var config = ThermalEvaporationConfig.resolve(terminal.getOrCreateTag());

        var root = new WidgetGroup(0, 0, 158, 110);
        root.addWidget(new LabelWidget(4, 4, getTitle()));

        addIntRow(
            root,
            26,
            Component.translatable(HEIGHT),
            config::getHeight,
            value -> {
                config.setHeight(value);
                onSave.accept(terminal);
            }
        );

        return root;
    }
}
