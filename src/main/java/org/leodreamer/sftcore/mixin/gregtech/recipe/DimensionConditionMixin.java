package org.leodreamer.sftcore.mixin.gregtech.recipe;

import org.leodreamer.sftcore.common.data.SFTDimensions;
import org.leodreamer.sftcore.common.data.lang.MixinTooltips;

import com.gregtechceu.gtceu.api.gui.widget.SlotWidget;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.common.recipe.condition.DimensionCondition;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DimensionCondition.class)
public class DimensionConditionMixin {

    @Shadow(remap = false)
    private ResourceLocation dimension;

    @Inject(method = "testCondition", at = @At("HEAD"), cancellable = true, remap = false)
    private void enableVoid(GTRecipe recipe, RecipeLogic recipeLogic, CallbackInfoReturnable<Boolean> cir) {
        if (dimension.getPath().equals("overworld")) {
            Level level = recipeLogic.machine.self().getLevel();
            if (level != null && level.dimension() == SFTDimensions.VOID_DIMENSION) {
                cir.cancel();
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "setupDimensionMarkers", at = @At("RETURN"), remap = false)
    private void addHoverTooltip(int xOffset, int yOffset, CallbackInfoReturnable<SlotWidget> cir) {
        SlotWidget dimSlot = cir.getReturnValue();
        if (dimSlot != null && dimension.getPath().equals("overworld")) {
            dimSlot.appendHoverTooltips(Component.translatable(MixinTooltips.AVAILABLE_IN_VOID));
        }
    }
}
