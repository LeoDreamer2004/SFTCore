package org.leodreamer.sftcore.common.item.terminal.gui.impl;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.terminal.MekBuilderRegistry;
import org.leodreamer.sftcore.common.item.terminal.builder.impl.InductionMatrixBuilder;
import org.leodreamer.sftcore.common.item.terminal.builder.impl.InductionMatrixConfig;

import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ItemStackTexture;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import mekanism.common.registries.MekanismBlocks;
import org.leodreamer.sftcore.common.item.terminal.gui.IntConfigButtonGroup;
import org.leodreamer.sftcore.common.item.terminal.gui.MekTerminalTab;

import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

@DataGenScanned
public final class InductionMatrixTab extends MekTerminalTab<InductionMatrixBuilder> {

    @RegisterLanguage("Induction Matrix")
    public static final String TITLE = "item.sftcore.mek_terminal.tab.induction";

    @RegisterLanguage("Width")
    public static final String WIDTH = "item.sftcore.mek_terminal.width";

    @RegisterLanguage("Height")
    public static final String HEIGHT = "item.sftcore.mek_terminal.height";

    @RegisterLanguage("Depth")
    public static final String DEPTH = "item.sftcore.mek_terminal.depth";

    public InductionMatrixTab(
        InductionMatrixBuilder builder,
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
        return new ItemStackTexture(new ItemStack(MekanismBlocks.INDUCTION_CASING.getBlock()));
    }

    @Override
    public Widget createMainPage(FancyMachineUIWidget ui) {
        MekBuilderRegistry.setSelected(terminal, builder);
        onSave.accept(terminal);

        var config = InductionMatrixConfig.resolve(terminal.getOrCreateTag());

        var root = new WidgetGroup(0, 0, 158, 80);
        root.addWidget(new LabelWidget(4, 4, getTitle()));

        addDimensionRow(root, 26, Component.translatable(WIDTH), config::getWidth, config::setWidth);
        addDimensionRow(root, 46, Component.translatable(HEIGHT), config::getHeight, config::setHeight);
        addDimensionRow(root, 66, Component.translatable(DEPTH), config::getDepth, config::setDepth);

        return root;
    }

    private void addDimensionRow(
        WidgetGroup root,
        int y,
        Component label,
        IntSupplier getter,
        IntConsumer setter
    ) {
        root.addWidget(new LabelWidget(12, y + 4, label));
        root.addWidget(new IntConfigButtonGroup(92, y, getter, num -> {
            setter.accept(num);
            onSave.accept(terminal);
        }));
    }
}
