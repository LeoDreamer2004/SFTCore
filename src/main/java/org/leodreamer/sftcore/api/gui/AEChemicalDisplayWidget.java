package org.leodreamer.sftcore.api.gui;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.integration.ae2.gui.widget.slot.AEConfigSlotWidget;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import mekanism.api.chemical.gas.GasStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AEChemicalDisplayWidget extends WidgetGroup {

    private final AEChemicalListGridWidget gridWidget;
    private final int index;

    public AEChemicalDisplayWidget(int x, int y, AEChemicalListGridWidget gridWidget, int index) {
        super(x, y, 18, 18);
        this.gridWidget = gridWidget;
        this.index = index;

        var gasSlot = new GasTankWidget(
            new DisplayGasHandler(List.of(getDisplayGas())),
            0, 0, 0, 18, 18
        )
            .setShowAmount(false)
            .setShowAmountOverlay(false)
            .setDrawHoverTips(false)
            .setDrawHoverOverlay(false)
            .setAllowClickFilled(false)
            .setAllowClickDrained(false)
            .setClientSideWidget();
        this.addWidget(gasSlot);
    }

    @Override
    public void drawInBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        var pos = getPosition();

        GuiTextures.FLUID_SLOT.draw(graphics, mouseX, mouseY, pos.x, pos.y, 18, 18);
        GuiTextures.NUMBER_BACKGROUND.draw(graphics, mouseX, mouseY, pos.x + 18, pos.y, 140, 18);

        super.drawInBackground(graphics, mouseX, mouseY, partialTicks);

        var gas = getDisplayGas();
        if (!gas.isEmpty()) {
            String amountStr = "x" + FormattingUtil.formatNumbers(gas.getAmount()) + "B";
            DrawerHelper.drawText(graphics, amountStr, pos.x + 21, pos.y + 5, 1, 0xFFFFFFFF);
        }

        if (isMouseOverElement(mouseX, mouseY)) {
            AEConfigSlotWidget.drawSelectionOverlay(graphics, pos.x + 1, pos.y + 1, 16, 16);
        }
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
        tooltip.add(Component.translatable(
            GasTankWidget.GAS_STORED,
            FormattingUtil.formatNumbers(gas.getAmount()),
            FormattingUtil.formatNumbers(gas.getAmount())
        ));

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
