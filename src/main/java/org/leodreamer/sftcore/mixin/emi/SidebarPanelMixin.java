package org.leodreamer.sftcore.mixin.emi;

import org.leodreamer.sftcore.integration.emi.EmiRecipeAutocraft;

import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.config.SidebarType;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.screen.EmiScreenManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EmiScreenManager.SidebarPanel.class)
public abstract class SidebarPanelMixin {

    @Shadow(remap = false)
    public abstract SidebarType getType();

    @Shadow(remap = false)
    public EmiScreenManager.ScreenSpace space;

    @Inject(method = "drawHeader", at = @At("TAIL"), remap = false)
    private void addFocusGTRecipeInfoDisplay(
        EmiDrawContext context, int mouseX, int mouseY, float delta, int page, int totalPages, CallbackInfo ci
    ) {
        if (getType() != SidebarType.FAVORITES) {
            return;
        }
        if (EmiRecipeAutocraft.curScreen() == EmiRecipeAutocraft.AutocraftScreen.GT) {
            var focus = EmiRecipeAutocraft.findFocus();
            if (focus == null) return;
            var recipe = focus.getRecipe();
            if (recipe == null) return;
            int x = (space.tx + space.tw * 18 - recipe.getDisplayWidth()) / 2;
            int y = space.ty + space.th * 18 - recipe.getDisplayHeight() - 2;
            EmiRenderHelper.renderRecipe(recipe, context, x, y, false, -1);
        }
    }
}
