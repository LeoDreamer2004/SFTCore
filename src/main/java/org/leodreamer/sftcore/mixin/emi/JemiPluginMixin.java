package org.leodreamer.sftcore.mixin.emi;

import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.jemi.JemiPlugin;
import mezz.jei.api.recipe.IRecipeCategoriesLookup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.stream.Stream;

@Mixin(value = JemiPlugin.class, remap = false)
public abstract class JemiPluginMixin {

    @Redirect(
        method = "register",
        at = @At(
            value = "INVOKE",
            target = "Lmezz/jei/api/recipe/IRecipeCategoriesLookup;get()Ljava/util/stream/Stream;"
        )
    )
    private Stream<IRecipeCategory<?>> sftcore$skipNativeTagCategories(IRecipeCategoriesLookup lookup) {
        return lookup.get().filter(category -> {
            String path = category.getRecipeType().getUid().getPath();
            return !"tag_recipes/block".equals(path) && !"tag_recipes/item".equals(path) &&
                !"tag_recipes/fluid".equals(path);
        });
    }

    // use emi local recipe registration
    @Inject(method = "addCraftingRecipes", at = @At("HEAD"), cancellable = true)
    private void sftcore$skipVanillaCrafting(EmiRegistry registry, IRecipeCategory<?> category, CallbackInfo ci) {
        ci.cancel();
    }
}
