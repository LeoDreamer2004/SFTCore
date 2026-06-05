package org.leodreamer.sftcore.common.item.terminal.gui.impl;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.terminal.MekBuilderRegistry;
import org.leodreamer.sftcore.common.item.terminal.builder.impl.InductionMatrixBuilder;
import org.leodreamer.sftcore.common.item.terminal.builder.impl.InductionMatrixConfig;
import org.leodreamer.sftcore.common.item.terminal.gui.MekTerminalTab;

import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ItemStackTexture;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import mekanism.common.registries.MekanismBlocks;

import java.util.function.Consumer;

@DataGenScanned
public final class InductionMatrixTab extends MekTerminalTab<InductionMatrixBuilder> {

    @RegisterLanguage("Induction Matrix")
    public static final String TITLE = "item.sftcore.mek_terminal.tab.induction";

    @RegisterLanguage("Width")
    public static final String WIDTH = "item.sftcore.mek_terminal.induction_matrix.width";

    @RegisterLanguage("Height")
    public static final String HEIGHT = "item.sftcore.mek_terminal.induction_matrix.height";

    @RegisterLanguage("Depth")
    public static final String DEPTH = "item.sftcore.mek_terminal.induction_matrix.depth";

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

        var config = InductionMatrixConfig.resolve(
            terminal.getOrCreateTag()
        );

        var root = rootWidget();

        addIntRow(root, 26, Component.translatable(WIDTH), config::getWidth, config::setWidth);
        addIntRow(root, 46, Component.translatable(HEIGHT), config::getHeight, config::setHeight);
        addIntRow(root, 66, Component.translatable(DEPTH), config::getDepth, config::setDepth);

        return root;
    }
}
