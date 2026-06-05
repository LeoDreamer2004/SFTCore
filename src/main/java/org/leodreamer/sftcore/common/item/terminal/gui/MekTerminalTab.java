package org.leodreamer.sftcore.common.item.terminal.gui;

import org.leodreamer.sftcore.common.item.terminal.builder.IMekMultiblockBuilder;

import com.gregtechceu.gtceu.api.gui.fancy.IFancyUIProvider;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import lombok.Getter;

import java.util.List;
import java.util.function.Consumer;

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
}
