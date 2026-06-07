package org.leodreamer.sftcore.api.gui;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.integration.ae2.gui.widget.slot.AEConfigSlotWidget;
import com.gregtechceu.gtceu.utils.FormattingUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import mekanism.api.chemical.gas.GasStack;
import mekanism.client.gui.GuiUtils;
import mekanism.client.render.MekanismRenderer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Optional;

public class AEChemicalDisplayWidget extends Widget {

    private final AEChemicalListGridWidget gridWidget;
    private final int index;

    public AEChemicalDisplayWidget(int x, int y, AEChemicalListGridWidget gridWidget, int index) {
        super(new Position(x, y), new Size(18 + 140, 18));
        this.gridWidget = gridWidget;
        this.index = index;
    }

    @Override
    public void drawInBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawInBackground(graphics, mouseX, mouseY, partialTicks);

        var pos = getPosition();

        GuiTextures.FLUID_SLOT.draw(graphics, mouseX, mouseY, pos.x, pos.y, 18, 18);
        GuiTextures.NUMBER_BACKGROUND.draw(graphics, mouseX, mouseY, pos.x + 18, pos.y, 140, 18);

        var gas = getDisplayGas();
        if (!gas.isEmpty()) {
            drawGas(graphics, gas, pos.x + 1, pos.y + 1);

            String amountStr = "x" + FormattingUtil.formatNumbers(gas.getAmount()) + "B";
            DrawerHelper.drawText(graphics, amountStr, pos.x + 21, pos.y + 5, 1, 0xFFFFFFFF);
        }

        if (isMouseOverElement(mouseX, mouseY)) {
            AEConfigSlotWidget.drawSelectionOverlay(graphics, pos.x + 1, pos.y + 1, 16, 16);
        }
    }

    /**
     * easy version to display gas instead of {@link DisplayGasHandler}
     */
    private void drawGas(GuiGraphics graphics, GasStack gas, int x, int y) {
        if (gas.isEmpty()) {
            return;
        }

        var sprite = MekanismRenderer.getChemicalTexture(gas.getType());

        MekanismRenderer.color(graphics, gas);
        GuiUtils.drawTiledSprite(
            graphics,
            x,
            y + 16,
            0,
            16,
            16,
            sprite,
            16,
            16,
            100,
            GuiUtils.TilingDirection.UP_RIGHT,
            true
        );
        MekanismRenderer.resetColor(graphics);
    }

    @Override
    public void drawInForeground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (!isMouseOverElement(mouseX, mouseY)) {
            return;
        }

        var gas = getDisplayGas();
        if (gas.isEmpty()) {
            return;
        }

        var tooltip = new ArrayList<Component>();
        tooltip.add(gas.getTextComponent());
        tooltip.add(
            Component.translatable(
                GasTankWidget.GAS_STORED,
                FormattingUtil.formatNumbers(gas.getAmount()),
                FormattingUtil.formatNumbers(gas.getAmount())
            )
        );

        graphics.renderTooltip(
            Minecraft.getInstance().font,
            tooltip,
            Optional.empty(),
            mouseX,
            mouseY
        );
    }

    private GasStack getDisplayGas() {
        var stack = this.gridWidget.getAt(this.index);
        if (stack == null || !(stack.what() instanceof MekanismKey key)) {
            return GasStack.EMPTY;
        }

        if (key.getForm() != MekanismKey.GAS || !(key.getStack() instanceof GasStack gas)) {
            return GasStack.EMPTY;
        }

        long amount = stack.amount();
        if (amount <= 0) {
            return GasStack.EMPTY;
        }

        return new GasStack(gas, amount);
    }
}
