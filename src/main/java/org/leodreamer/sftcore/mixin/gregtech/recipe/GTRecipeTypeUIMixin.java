package org.leodreamer.sftcore.mixin.gregtech.recipe;

import org.leodreamer.sftcore.api.recipe.capability.GasRecipeCapability;

import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.recipe.ui.GTRecipeTypeUI;

import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GTRecipeTypeUI.class, remap = false)
public abstract class GTRecipeTypeUIMixin {

    @Shadow
    private Byte2ObjectMap<IGuiTexture> slotOverlays;

    @Inject(method = "getOverlaysForSlot", at = @At("HEAD"), cancellable = true)
    private void sftcore$useFluidSlotForGas(
        boolean isOutput,
        RecipeCapability<?> capability,
        boolean isLast,
        boolean isSteam,
        boolean isHighPressure,
        CallbackInfoReturnable<IGuiTexture> cir
    ) {
        if (capability != GasRecipeCapability.CAP) {
            return;
        }

        var base = GuiTextures.FLUID_SLOT;
        // output bit + fluid bit + last bit
        byte overlayKey = (byte) ((isOutput ? 2 : 0) + 1 + (isLast ? 4 : 0));

        if (slotOverlays.containsKey(overlayKey)) {
            cir.setReturnValue(new GuiTextureGroup(base, slotOverlays.get(overlayKey)));
        } else {
            cir.setReturnValue(base);
        }
    }
}
