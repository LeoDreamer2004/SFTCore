package org.leodreamer.sftcore.common.item.terminal.gui.impl;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.terminal.MekBuilderRegistry;
import org.leodreamer.sftcore.common.item.terminal.builder.impl.SPSBuilder;
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
public final class SPSTab extends MekTerminalTab<SPSBuilder> {

    @RegisterLanguage("Supercritical Phase Shifter")
    public static final String TITLE = "item.sftcore.mek_terminal.tab.sps";

    @RegisterLanguage("Fixed 7x7x7 pattern")
    public static final String FIXED_PATTERN = "item.sftcore.mek_terminal.sps.fixed_pattern";

    @RegisterLanguage("Shift right-click the bottom north center SPS Casing")
    public static final String START_HINT = "item.sftcore.mek_terminal.sps.start_hint";

    public SPSTab(
        SPSBuilder builder,
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
        return new ItemStackTexture(new ItemStack(MekanismBlocks.SPS_CASING.getBlock()));
    }

    @Override
    public Widget createMainPage(FancyMachineUIWidget ui) {
        MekBuilderRegistry.setSelected(terminal, builder);
        onSave.accept(terminal);

        var root = rootWidget();

        root.addWidget(wrappedText(12, 6, 134, 14, FIXED_PATTERN));
        root.addWidget(wrappedText(12, 24, 134, 30, START_HINT));

        return root;
    }
}
