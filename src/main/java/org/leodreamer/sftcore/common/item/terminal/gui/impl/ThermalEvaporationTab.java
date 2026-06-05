package org.leodreamer.sftcore.common.item.terminal.gui.impl;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.api.gui.IntConfigButtonGroup;
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
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

@DataGenScanned
public final class ThermalEvaporationTab extends MekTerminalTab<ThermalEvaporationBuilder> {

    @RegisterLanguage("Thermal Evaporation Tower")
    public static final String TITLE = "item.sftcore.mek_terminal.tab.thermal_evaporation";

    @RegisterLanguage("Height")
    public static final String HEIGHT = "item.sftcore.mek_terminal.thermal_evaporation.height";

    @RegisterLanguage("Controller X")
    public static final String CONTROLLER_X = "item.sftcore.mek_terminal.thermal_evaporation.controller_x";

    @RegisterLanguage("Controller Y")
    public static final String CONTROLLER_Y = "item.sftcore.mek_terminal.thermal_evaporation.controller_y";

    @RegisterLanguage("Controller Z")
    public static final String CONTROLLER_Z = "item.sftcore.mek_terminal.thermal_evaporation.controller_z";

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

        addConfigRow(root, 26, Component.translatable(HEIGHT), config::getHeight, config::setHeight);
        addConfigRow(root, 46, Component.translatable(CONTROLLER_X), config::getControllerX, config::setControllerX);
        addConfigRow(root, 66, Component.translatable(CONTROLLER_Y), config::getControllerY, config::setControllerY);
        addConfigRow(root, 86, Component.translatable(CONTROLLER_Z), config::getControllerZ, config::setControllerZ);

        return root;
    }

    private void addConfigRow(
        WidgetGroup root,
        int y,
        Component label,
        IntSupplier getter,
        IntConsumer setter
    ) {
        root.addWidget(new LabelWidget(12, y + 4, label));

        root.addWidget(new IntConfigButtonGroup(92, y, getter, value -> {
            setter.accept(value);
            onSave.accept(terminal);
        }));
    }
}
