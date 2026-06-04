package org.leodreamer.sftcore.common.item.terminal.gui;

import com.gregtechceu.gtceu.api.gui.GuiTextures;

import net.minecraft.network.chat.Component;

import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.widget.ButtonWidget;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

public class IntConfigButtonGroup extends WidgetGroup {

    public IntConfigButtonGroup(
        int x,
        int y,
        IntSupplier getter,
        IntConsumer setter
    ) {
        super(x, y, 64, 16);

        addWidget(
            new ButtonWidget(
                0,
                0,
                16,
                16,
                new GuiTextureGroup(GuiTextures.BUTTON, new TextTexture("-")),
                clickData -> setter.accept(getter.getAsInt() - 1)
            ).setHoverTooltips(Component.literal("-"))
        );

        addWidget(
            new LabelWidget(
                24,
                4,
                () -> String.valueOf(getter.getAsInt())
            ).setClientSideWidget()
        );

        addWidget(
            new ButtonWidget(
                48,
                0,
                16,
                16,
                new GuiTextureGroup(GuiTextures.BUTTON, new TextTexture("+")),
                clickData -> setter.accept(getter.getAsInt() + 1)
            ).setHoverTooltips(Component.literal("+"))
        );
    }
}
