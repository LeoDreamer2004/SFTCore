package org.leodreamer.sftcore.common.item.terminal.gui;

import org.leodreamer.sftcore.common.item.MekTerminalBehavior;
import org.leodreamer.sftcore.common.item.terminal.MekBuilderRegistry;
import org.leodreamer.sftcore.common.item.terminal.builder.IMekMultiblockBuilder;

import com.gregtechceu.gtceu.common.mui.GTGuiTextures;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import brachy.modularui.api.drawable.IDrawable;
import brachy.modularui.api.drawable.Text;
import brachy.modularui.api.widget.IWidget;
import brachy.modularui.drawable.ItemDrawable;
import brachy.modularui.utils.Alignment;
import brachy.modularui.utils.Color;
import brachy.modularui.value.StringValue;
import brachy.modularui.value.sync.IntSyncValue;
import brachy.modularui.value.sync.PanelSyncManager;
import brachy.modularui.widgets.TextWidget;
import brachy.modularui.widgets.layout.Flow;
import brachy.modularui.widgets.textfield.TextFieldWidget;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

public abstract class MekTerminalTab<T extends IMekMultiblockBuilder> {

    protected final T builder;
    protected final ItemStack terminal;
    protected final PanelSyncManager syncManager;

    protected MekTerminalTab(T builder, ItemStack terminal, PanelSyncManager syncManager) {
        this.builder = builder;
        this.terminal = terminal;
        this.syncManager = syncManager;
    }

    public abstract Component title();

    public IDrawable tabIcon() {
        return new ItemDrawable(builder.clickAt());
    }

    public boolean isSelected() {
        return MekBuilderRegistry.selected(terminal).id().equals(builder.id());
    }

    public void select() {
        MekBuilderRegistry.setSelected(terminal, builder);
    }

    public IWidget createPage() {
        return Flow.column()
            .width(MekTerminalBehavior.PAGE_WIDTH)
            .height(MekTerminalBehavior.PAGE_HEIGHT)
            .padding(MekTerminalBehavior.PAGE_PADDING)
            .childPadding(4)
            .crossAxisAlignment(Alignment.CrossAxis.START)
            .background(GTGuiTextures.DISPLAY)
            .child(
                Text.of(title()).style(ChatFormatting.YELLOW, ChatFormatting.BOLD)
                    .asWidget()
                    .widthRel(1)
                    .height(16)
                    .textAlign(Alignment.Center)
            )
            .child(
                createContent(
                    Flow.column()
                        .widthRel(1)
                        .coverChildrenHeight()
                        .crossAxisAlignment(Alignment.CrossAxis.START)
                        .childPadding(2)
                )
            );
    }

    protected abstract Flow createContent(Flow container);

    protected IWidget intRow(String key, String label, IntSupplier getter, IntConsumer setter) {
        var value = new IntSyncValue(getter, setter).allowC2S();
        syncManager.syncValue("mek_terminal_" + builder.id().getPath() + "_" + key, value);
        return Flow.row()
            .widthRel(1)
            .height(18)
            .child(Text.lang(label).color(0x999999).asWidget().expanded())
            .child(
                new TextFieldWidget()
                    .size(42, 16)
                    .value(new StringValue.Dynamic(value::getStringValue, value::setStringValue))
                    .setNumbers(0, 64)
                    .setTextAlignment(Alignment.Center)
                    .setTextColor(Color.WHITE.main)
            );
    }

    protected IWidget wrappedText(String translationKey, int height) {
        return new TextWidget<>(Text.lang(translationKey))
            .width(MekTerminalBehavior.PAGE_WIDTH - MekTerminalBehavior.PAGE_PADDING * 2)
            .maxWidth(MekTerminalBehavior.PAGE_WIDTH - MekTerminalBehavior.PAGE_PADDING * 2)
            .height(height)
            .textAlign(Alignment.TopLeft)
            .color(Color.WHITE.main);
    }
}
