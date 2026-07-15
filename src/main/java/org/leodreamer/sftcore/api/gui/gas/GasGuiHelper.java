package org.leodreamer.sftcore.api.gui.gas;

import com.gregtechceu.gtceu.utils.FormattingUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.InventoryMenu;

import appeng.api.stacks.GenericStack;
import com.mojang.blaze3d.systems.RenderSystem;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import mekanism.api.chemical.gas.GasStack;
import mekanism.client.gui.GuiUtils;
import mekanism.client.render.MekanismRenderer;

import java.util.List;

public final class GasGuiHelper {

    private GasGuiHelper() {}

    public static void drawGas(GuiGraphics graphics, GasStack gas, int x, int y) {
        drawGas(graphics, gas, x, y, 16, 16);
    }

    public static void drawGas(GuiGraphics graphics, GasStack gas, int x, int y, int width, int height) {
        if (gas == null || gas.isEmpty() || width <= 0 || height <= 0) {
            return;
        }

        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        var sprite = MekanismRenderer.getChemicalTexture(gas.getType());
        MekanismRenderer.color(graphics, gas);
        GuiUtils.drawTiledSprite(
            graphics,
            x,
            y + height,
            0,
            width,
            height,
            sprite,
            16,
            16,
            100,
            GuiUtils.TilingDirection.UP_RIGHT,
            true
        );
        MekanismRenderer.resetColor(graphics);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void drawAmountOverlay(GuiGraphics graphics, long amount, int x, int y) {
        // format amount
        String text = FormattingUtil.formatBuckets(amount);
        var font = Minecraft.getInstance().font;
        if (font.width(text) > 32) {
            text = FormattingUtil.formatNumberReadable(amount, true, FormattingUtil.DECIMAL_FORMAT_1F, "B");
        }
        if (font.width(text) > 32) {
            text = FormattingUtil.formatNumberReadable(amount, true, FormattingUtil.DECIMAL_FORMAT_0F, "B");
        }

        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 400);
        graphics.pose().scale(0.5F, 0.5F, 1.0F);
        int textX = (x + 16) * 2 - font.width(text);
        int textY = (y + 16) * 2 - font.lineHeight;
        graphics.drawString(font, text, textX, textY, 0xFFFFFF, true);
        graphics.pose().popPose();
    }

    public static GasStack getGasStack(GenericStack stack) {
        if (!isGas(stack)) {
            return GasStack.EMPTY;
        }
        var key = (MekanismKey) stack.what();
        if (!(key.getStack() instanceof GasStack gas)) {
            return GasStack.EMPTY;
        }
        return new GasStack(gas, stack.amount());
    }

    public static boolean isGas(GenericStack stack) {
        return stack != null && stack.what() instanceof MekanismKey key && key.getForm() == MekanismKey.GAS;
    }

    public static void renderTooltip(GuiGraphics graphics, GenericStack stack, int mouseX, int mouseY) {
        var gas = getGasStack(stack);
        if (gas.isEmpty()) {
            return;
        }
        graphics.renderComponentTooltip(
            Minecraft.getInstance().font,
            List.of(
                gas.getTextComponent(),
                Component.literal("x" + String.format("%,d", gas.getAmount())).withStyle(ChatFormatting.GRAY)
            ),
            mouseX,
            mouseY
        );
    }
}
