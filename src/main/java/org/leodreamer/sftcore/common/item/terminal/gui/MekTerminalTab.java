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
import brachy.modularui.screen.RichTooltip;
import brachy.modularui.utils.Alignment;
import brachy.modularui.utils.Color;
import brachy.modularui.value.StringValue;
import brachy.modularui.value.sync.IntSyncValue;
import brachy.modularui.value.sync.InteractionSyncHandler;
import brachy.modularui.value.sync.PanelSyncManager;
import brachy.modularui.widgets.ButtonWidget;
import brachy.modularui.widgets.TextWidget;
import brachy.modularui.widgets.layout.Flow;
import brachy.modularui.widgets.textfield.TextFieldWidget;
import com.mojang.blaze3d.platform.InputConstants;

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

    public IWidget createPage() {
        return Flow.column()
            .width(MekTerminalBehavior.PAGE_WIDTH)
            .height(MekTerminalBehavior.PAGE_HEIGHT)
            .padding(7)
            .childPadding(5)
            .background(GTGuiTextures.DISPLAY)
            .child(
                Text.of(title()).style(ChatFormatting.YELLOW, ChatFormatting.BOLD)
                    .asWidget()
                    .height(16)
                    .horizontalCenter()
            )
            .child(createSetButton())
            .child(
                createContent(
                    Flow.column()
                        .widthRel(1)
                        .coverChildren()
                        .childPadding(2)
                )
            );
    }

    protected abstract Flow createContent(Flow container);

    protected IWidget intRow(String key, Component label, IntSupplier getter, IntConsumer setter) {
        var value = new IntSyncValue(getter, setter).allowC2S();
        syncManager.syncValue("mek_terminal_" + builder.id().getPath() + "_" + key, value);
        return Flow.row()
            .widthRel(1)
            .height(18)
            .child(Text.of(label).asWidget().expanded())
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
            .width(MekTerminalBehavior.PAGE_WIDTH - 14)
            .maxWidth(MekTerminalBehavior.PAGE_WIDTH - 14)
            .height(height)
            .textAlign(Alignment.TopLeft)
            .color(Color.WHITE.main);
    }

    private IWidget createSetButton() {
        var selected = MekBuilderRegistry.selected(terminal).id().equals(builder.id());
        return new ButtonWidget<>()
            .size(62, 18)
            .overlay(Text.str(selected ? "Selected" : "Set").style(ChatFormatting.WHITE).scale(0.6f))
            .horizontalCenter()
            .tooltip(new RichTooltip().addLine(Text.of(builder.clickAt().getName())))
            .syncHandler(new InteractionSyncHandler().setOnMousePressed(mouse -> {
                if (mouse.mouseButton() == InputConstants.MOUSE_BUTTON_LEFT) {
                    MekBuilderRegistry.setSelected(terminal, builder);
                }
            }));
    }
}
