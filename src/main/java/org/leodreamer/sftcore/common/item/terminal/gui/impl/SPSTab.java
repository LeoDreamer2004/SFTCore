package org.leodreamer.sftcore.common.item.terminal.gui.impl;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.terminal.builder.impl.SPSBuilder;
import org.leodreamer.sftcore.common.item.terminal.gui.MekTerminalTab;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import brachy.modularui.value.sync.PanelSyncManager;
import brachy.modularui.widgets.layout.Flow;

@DataGenScanned
public final class SPSTab extends MekTerminalTab<SPSBuilder> {

    @RegisterLanguage("Supercritical Phase Shifter")
    public static final String TITLE = "item.sftcore.mek_terminal.tab.sps";

    @RegisterLanguage("Fixed 7x7x7 pattern")
    public static final String FIXED_PATTERN = "item.sftcore.mek_terminal.sps.fixed_pattern";

    public SPSTab(SPSBuilder builder, ItemStack terminal, PanelSyncManager syncManager) {
        super(builder, terminal, syncManager);
    }

    @Override
    public Component title() {
        return Component.translatable(TITLE);
    }

    @Override
    protected Flow createContent(Flow container) {
        return container.child(wrappedText(FIXED_PATTERN, 20));
    }
}
