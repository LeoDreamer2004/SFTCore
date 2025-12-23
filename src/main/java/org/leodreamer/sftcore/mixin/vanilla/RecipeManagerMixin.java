package org.leodreamer.sftcore.mixin.vanilla;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.api.recipe.remove.RecipeRemoval;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.common.crafting.conditions.ICondition;

import com.google.gson.JsonObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {

    @Inject(
        method = "fromJson(Lnet/minecraft/resources/ResourceLocation;Lcom/google/gson/JsonObject;Lnet/minecraftforge/common/crafting/conditions/ICondition$IContext;)Lnet/minecraft/world/item/crafting/Recipe;",
        remap = false,
        cancellable = true,
        at = @At("TAIL")
    )
    private static void sftcore$removeRecipe(
        ResourceLocation id,
        JsonObject json,
        ICondition.IContext context,
        CallbackInfoReturnable<Recipe<?>> cir
    ) {
        var recipe = cir.getReturnValue();
        if (recipe == null) return;
        try {
            boolean remove = RecipeRemoval.INSTANCE.remove(id, recipe);
            if (remove) {
                SFTCore.LOGGER.info("Removed recipe {}", id);
                cir.setReturnValue(null);
            }
        } catch (RuntimeException ignored) {}
    }
}
