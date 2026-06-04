package org.leodreamer.sftcore.common.item.terminal.gui;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.terminal.MekBuilderRegistry;
import org.leodreamer.sftcore.common.item.terminal.MekTerminalTags;
import org.leodreamer.sftcore.common.item.terminal.api.MekMultiblockBuilder;
import org.leodreamer.sftcore.common.item.terminal.api.MekTerminalTab;
import org.leodreamer.sftcore.common.item.terminal.builder.InductionMatrixBuilder;
import org.leodreamer.sftcore.common.item.terminal.builder.InductionMatrixConfig;

import mekanism.common.registries.MekanismBlocks;

import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ItemStackTexture;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Consumer;

@DataGenScanned
public final class InductionMatrixTab implements MekTerminalTab {

    @RegisterLanguage("Induction Matrix")
    public static final String TITLE = "item.sftcore.mek_terminal.tab.induction";

    @RegisterLanguage("Width")
    public static final String WIDTH = "item.sftcore.mek_terminal.width";

    @RegisterLanguage("Height")
    public static final String HEIGHT = "item.sftcore.mek_terminal.height";

    @RegisterLanguage("Depth")
    public static final String DEPTH = "item.sftcore.mek_terminal.depth";

    private final InductionMatrixBuilder builder;
    private final ItemStack stack;
    private final Consumer<ItemStack> onSave;

    public InductionMatrixTab(
        InductionMatrixBuilder builder,
        ItemStack stack,
        Consumer<ItemStack> onSave
    ) {
        this.builder = builder;
        this.stack = stack;
        this.onSave = onSave;
    }

    @Override
    public ResourceLocation id() {
        return builder.id();
    }

    @Override
    public MekMultiblockBuilder builder() {
        return builder;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(TITLE);
    }

    @Override
    public Widget createMainPage(FancyMachineUIWidget ui) {
        MekBuilderRegistry.setSelected(stack, id());
        onSave.accept(stack);

        InductionMatrixConfig.getOrCreate(stack);

        var root = new WidgetGroup(0, 0, 158, 80);

        root.addWidget(new LabelWidget(4, 4, getTitle()));

        addDimensionRow(root, 26, Component.translatable(WIDTH), MekTerminalTags.INDUCTION_WIDTH);
        addDimensionRow(root, 46, Component.translatable(HEIGHT), MekTerminalTags.INDUCTION_HEIGHT);
        addDimensionRow(root, 66, Component.translatable(DEPTH), MekTerminalTags.INDUCTION_DEPTH);

        return root;
    }

    @Override
    public IGuiTexture getTabIcon() {
        return new ItemStackTexture(new ItemStack(MekanismBlocks.INDUCTION_CASING.getBlock()));
    }

    @Override
    public List<Component> getTabTooltips() {
        return List.of(getTitle());
    }

    private void addDimensionRow(
        WidgetGroup root,
        int y,
        Component label,
        String key
    ) {
        root.addWidget(new LabelWidget(12, y + 4, label));

        root.addWidget(new IntConfigButtonGroup(
            92,
            y,
            () -> InductionMatrixConfig.readDimension(stack, key),
            value -> {
                InductionMatrixConfig.setDimension(stack, key, value);
                onSave.accept(stack);
            }
        ));
    }
}
