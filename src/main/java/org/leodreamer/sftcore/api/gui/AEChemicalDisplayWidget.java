package org.leodreamer.sftcore.api.gui;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.integration.ae2.gui.widget.list.AEListGridWidget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

import static com.gregtechceu.gtceu.integration.ae2.gui.widget.slot.AEConfigSlotWidget.drawSelectionOverlay;
import static com.lowdragmc.lowdraglib.gui.util.DrawerHelper.drawText;

public class AEChemicalDisplayWidget extends Widget {

    private final AEListGridWidget gridWidget;
    private final int index;

    public AEChemicalDisplayWidget(int x, int y, AEListGridWidget gridWidget, int index) {
        super(new Position(x, y), new Size(18, 18));
        this.gridWidget = gridWidget;
        this.index = index;
    }

    @Override
    public void drawInBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawInBackground(graphics, mouseX, mouseY, partialTicks);

        var pos = getPosition();
        var stack = gridWidget.getAt(index);

        GuiTextures.FLUID_SLOT.draw(graphics, mouseX, mouseY, pos.x, pos.y, 18, 18);
        GuiTextures.NUMBER_BACKGROUND.draw(graphics, mouseX, mouseY, pos.x + 18, pos.y, 140, 18);

        int stackX = pos.x + 1;
        int stackY = pos.y + 1;

        if (stack != null && stack.what() instanceof MekanismKey key) {
            String name = key.getDisplayName().getString();
            String amount = String.format("x%,d B", stack.amount());

            drawText(graphics, name, stackX + 20, stackY + 1, 1, 0xFFFFFFFF);
            drawText(graphics, amount, stackX + 20, stackY + 10, 1, 0xFFBDEBFF);
        }

        if (isMouseOverElement(mouseX, mouseY)) {
            drawSelectionOverlay(graphics, stackX, stackY, 16, 16);
        }
    }

    @Override
    public void drawInForeground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (!isMouseOverElement(mouseX, mouseY)) {
            return;
        }

        var stack = gridWidget.getAt(index);
        if (stack != null && stack.what() instanceof MekanismKey key) {
            graphics.renderTooltip(
                Minecraft.getInstance().font,
                List.of(
                    key.getDisplayName(),
                    Component.literal(String.format("%,d B", stack.amount()))
                ),
                Optional.empty(),
                mouseX,
                mouseY
            );
        }
    }
}
