package org.leodreamer.sftcore.api.gui.gas;

import org.leodreamer.sftcore.api.recipe.capability.GasRecipeCapability;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.api.recipe.gui.ContentOverlay;
import com.gregtechceu.gtceu.utils.FormattingUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import brachy.modularui.api.ITheme;
import brachy.modularui.api.widget.Interactable;
import brachy.modularui.screen.RichTooltip;
import brachy.modularui.screen.viewport.ModularGuiContext;
import brachy.modularui.theme.WidgetThemeEntry;
import brachy.modularui.widget.Widget;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.jemi.JemiUtil;
import mekanism.api.chemical.gas.GasStack;
import mekanism.client.jei.MekanismJEI;

public class GasRecipeSlotWidget extends Widget<GasRecipeSlotWidget> implements Interactable {

    private GasStack gas = GasStack.EMPTY;
    private long amount;
    private ContentOverlay contentOverlay;
    private SlotWidget emiSlot = new SlotWidget(EmiStack.EMPTY, 0, 0).drawBack(false);

    public GasRecipeSlotWidget() {
        size(18);
        tooltip().autoUpdate(true);
    }

    public GasRecipeSlotWidget value(
        Content content,
        IO io,
        boolean perTick,
        GTRecipe recipe
    ) {
        this.gas = GasRecipeCapability.CAP.of(content.content());
        this.amount = gas.isEmpty() ? 0 : gas.getAmount();
        this.contentOverlay = new ContentOverlay(content, perTick);
        if (gas.isEmpty()) {
            emiSlot = new SlotWidget(EmiStack.EMPTY, 0, 0).drawBack(false);
        } else {
            float chance = (float) content.chance() / content.maxChance();
            var stack = JemiUtil.getStack(MekanismJEI.TYPE_GAS, gas.copy())
                .setAmount(amount)
                .setChance(chance);
            emiSlot = new SlotWidget(stack, 0, 0).drawBack(false);
        }
        tooltipBuilder(
            tooltip -> {
                addTooltip(tooltip);
                Content.addChanceTooltips(
                    tooltip,
                    content,
                    recipe.getChanceLogicForCapability(GasRecipeCapability.CAP, io, perTick)
                );
                if (perTick) {
                    tooltip.addLine(Component.translatable("gtceu.gui.content.per_tick"));
                }
            }
        );
        return this;
    }

    @Override
    public Result onMousePressed(int button) {
        emiSlot.mouseClicked(getContext().getMouseX(), getContext().getAbsMouseY(), button);
        return Result.SUCCESS;
    }

    @Override
    public Result onKeyPressed(int keyCode, int scanCode, int modifiers) {
        return emiSlot.keyPressed(keyCode, scanCode, modifiers) ? Result.SUCCESS : Result.ACCEPT;
    }

    @Override
    protected WidgetThemeEntry<?> getWidgetThemeInternal(ITheme theme) {
        return theme.getFluidSlotTheme();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        if (gas == null || gas.isEmpty()) {
            return;
        }
        GasGuiHelper.drawGas(context.getGraphics(), gas, 1, 1);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void drawOverlay(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        super.drawOverlay(context, widgetTheme);
        if (emiSlot.shouldDrawSlotHighlight(context.getMouseX(), context.getMouseY())) {
            emiSlot.drawSlotHighlight(context.getGraphics(), emiSlot.getBounds());
        }
        if (contentOverlay != null) {
            contentOverlay.draw(context, 0, 0, 18, 18, widgetTheme.theme());
        }
        if (amount > 0) {
            GasGuiHelper.drawAmountOverlay(context.getGraphics(), amount, 1, 1);
        }
    }

    private void addTooltip(RichTooltip tooltip) {
        if (gas == null || gas.isEmpty()) {
            return;
        }
        tooltip.addLine(gas.getTextComponent());
        tooltip.addLine(Component.literal(FormattingUtil.formatNumbers(amount) + " mB").withStyle(ChatFormatting.GRAY));
    }
}
