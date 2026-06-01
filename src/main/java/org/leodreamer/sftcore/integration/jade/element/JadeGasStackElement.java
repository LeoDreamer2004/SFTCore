package org.leodreamer.sftcore.integration.jade.element;

import com.gregtechceu.gtceu.integration.jade.GTElementHelper;
import com.mojang.blaze3d.systems.RenderSystem;

import mekanism.api.chemical.gas.GasStack;
import mekanism.client.render.MekanismRenderer;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.phys.Vec2;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.jetbrains.annotations.Nullable;

import snownee.jade.api.ui.Element;
import snownee.jade.api.ui.IElement;

@OnlyIn(Dist.CLIENT)
public class JadeGasStackElement extends Element {

    private static final Vec2 DEFAULT_SIZE = new Vec2(16.0F, 16.0F);

    private final GasStack stack;

    public JadeGasStackElement(GasStack stack) {
        this.stack = stack.copy();
    }

    public static IElement smallGas(GasStack stack) {
        return new JadeGasStackElement(stack)
            .size(GTElementHelper.SMALL_FLUID_SIZE)
            .translate(GTElementHelper.SMALL_FLUID_OFFSET)
            .message(null);
    }

    @Override
    public Vec2 getSize() {
        return DEFAULT_SIZE;
    }

    @Override
    public void render(GuiGraphics guiGraphics, float x, float y, float maxX, float maxY) {
        if (stack.isEmpty()) {
            return;
        }

        var size = getCachedSize();

        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        MekanismRenderer.color(guiGraphics, stack);

        guiGraphics.blit(
            (int) x,
            (int) y,
            0,
            (int) size.x,
            (int) size.y,
            MekanismRenderer.getChemicalTexture(stack.getType())
        );

        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }

    @Override
    public @Nullable String getMessage() {
        return null;
    }
}
