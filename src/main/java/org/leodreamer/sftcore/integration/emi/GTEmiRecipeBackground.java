package org.leodreamer.sftcore.integration.emi;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.integration.emi.recipe.GTEmiRecipe;
import com.gregtechceu.gtceu.utils.GTUtil;

import net.minecraft.client.gui.GuiGraphics;

import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.runtime.EmiDrawContext;

public class GTEmiRecipeBackground extends Widget {

    int x, y, width, height, tier;

    public GTEmiRecipeBackground(int x, int y, int width, int height, GTEmiRecipe recipe) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        var gtRecipe = ((IGTEmiRecipe) recipe).sftcore$recipe();
        long voltage = RecipeHelper.getRealEUtWithIO(gtRecipe).voltage();
        this.tier = GTUtil.getTierByVoltage(voltage);
    }

    @Override
    public Bounds getBounds() {
        return Bounds.EMPTY;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        int color = GTValues.VCM[tier];
        int inner = color | 0x9F000000;
        // generate a color with darker border
        int r = (int) (0.7 * ((color >> 16) & 0xFF));
        int g = (int) (0.7 * ((color >> 8) & 0xFF));
        int b = (int) (0.7 * (color & 0xFF));
        int outer = (r << 16) | (g << 8) | b | 0x9F000000;

        // draw a border with the color
        var wrap = EmiDrawContext.wrap(guiGraphics);
        wrap.fill(x, y, width, 1, outer); // top
        wrap.fill(x + 1, y + 1, width - 2, 1, inner); // top
        wrap.fill(x + 1, y + height, width - 2, 1, inner); // bottom
        wrap.fill(x, y + height + 1, width, 1, outer); // bottom
        wrap.fill(x, y + 1, 1, height, outer); // left
        wrap.fill(x + 1, y + 2, 1, height - 2, inner); // left
        wrap.fill(x + width - 2, y + 2, 1, height - 2, inner); // right
        wrap.fill(x + width - 1, y + 1, 1, height, outer); // right
    }
}
