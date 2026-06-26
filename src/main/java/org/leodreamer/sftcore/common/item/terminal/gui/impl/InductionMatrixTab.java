package org.leodreamer.sftcore.common.item.terminal.gui.impl;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.terminal.builder.impl.InductionMatrixBuilder;
import org.leodreamer.sftcore.common.item.terminal.config.impl.InductionMatrixConfig;
import org.leodreamer.sftcore.common.item.terminal.gui.MekTerminalTab;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import brachy.modularui.value.sync.PanelSyncManager;
import brachy.modularui.widgets.layout.Flow;

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

    public InductionMatrixTab(InductionMatrixBuilder builder, ItemStack terminal, PanelSyncManager syncManager) {
        super(builder, terminal, syncManager);
    }

    @Override
    public Component title() {
        return Component.translatable(TITLE);
    }

    @Override
    protected Flow createContent(Flow container) {
        var config = InductionMatrixConfig.resolve(terminal.getOrCreateTag());
        return container.child(intRow("width", WIDTH, config.width::get, config.width::set))
            .child(intRow("height", HEIGHT, config.height::get, config.height::set))
            .child(intRow("depth", DEPTH, config.depth::get, config.depth::set));
    }
}
