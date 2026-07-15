package org.leodreamer.sftcore.integration.ae2.gui;

import org.leodreamer.sftcore.api.gui.gas.GasGuiHelper;

import com.gregtechceu.gtceu.common.mui.GTGuiTextures;
import com.gregtechceu.gtceu.integration.ae2.gui.AEGuiHelper;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import appeng.api.stacks.GenericStack;
import brachy.modularui.api.ITheme;
import brachy.modularui.screen.viewport.ModularGuiContext;
import brachy.modularui.theme.WidgetThemeEntry;
import brachy.modularui.widget.Widget;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Copy version for gas from {@link com.gregtechceu.gtceu.integration.ae2.gui.AEStackDisplayWidget}
 */
public class AEGasStackDisplayWidget extends Widget<AEGasStackDisplayWidget> {

    private final List<GenericStack> source;
    private final int index;

    public AEGasStackDisplayWidget(List<GenericStack> source, int index) {
        this.source = source;
        this.index = index;
        size(18);
    }

    private @Nullable GenericStack getStack() {
        return index < source.size() ? source.get(index) : null;
    }

    @Override
    protected WidgetThemeEntry<?> getWidgetThemeInternal(ITheme theme) {
        return theme.getItemSlotTheme();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        var graphics = context.getGraphics();
        GTGuiTextures.SLOT_DARK.draw(context, 0, 0, 18, 18);

        var stack = getStack();
        if (stack == null) {
            return;
        }

        var gas = GasGuiHelper.getGasStack(stack);
        if (gas.isEmpty()) {
            return;
        }

        GasGuiHelper.drawGas(graphics, gas, 1, 1);
        GasGuiHelper.drawAmountOverlay(graphics, gas.getAmount(), 1, 1);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void drawForeground(ModularGuiContext context) {
        if (!isHovering()) {
            return;
        }

        var stack = getStack();
        if (stack == null) {
            return;
        }

        if (GasGuiHelper.getGasStack(stack).isEmpty()) {
            return;
        }

        AEGuiHelper.drawSelectionOverlay(context.getGraphics(), 1, 1, 16, 16);
        GasGuiHelper.renderTooltip(
            context.getGraphics(),
            stack,
            context.getAbsMouseX(),
            context.getAbsMouseY()
        );
    }
}
