package org.leodreamer.sftcore.common.item.terminal.gui;

import org.leodreamer.sftcore.api.gui.IntConfigButtonGroup;
import org.leodreamer.sftcore.common.item.terminal.builder.IMekMultiblockBuilder;

import com.gregtechceu.gtceu.api.gui.fancy.IFancyUIProvider;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import lombok.Getter;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

public abstract class MekTerminalTab<T extends IMekMultiblockBuilder> implements IFancyUIProvider {

    @Getter
    protected final T builder;
    protected final ItemStack terminal;
    protected final Consumer<ItemStack> onSave;

    protected MekTerminalTab(T builder, ItemStack terminal, Consumer<ItemStack> onSave) {
        this.builder = builder;
        this.terminal = terminal;
        this.onSave = onSave;
    }

    @Override
    public List<Component> getTabTooltips() {
        return List.of(getTitle());
    }

    protected void addIntRow(
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
