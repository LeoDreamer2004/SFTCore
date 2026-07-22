package org.leodreamer.sftcore.mixin.create;

import com.simibubi.create.compat.jei.category.CreateRecipeCategory;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.simibubi.create.compat.jei.CreateJEI$CategoryBuilder", remap = false)
public class CreateJEICategoryBuilderMixin {

    // Skip automatic recipe categories
    @Inject(method = "build", at = @At("HEAD"), cancellable = true)
    private void sftcore$skipCreateAutoCategories(
        String name,
        CreateRecipeCategory.Factory<?> factory,
        CallbackInfoReturnable<CreateRecipeCategory<?>> cir
    ) {
        if ("automatic_shaped".equals(name) || "automatic_shapeless".equals(name)) {
            cir.setReturnValue(null);
        }
    }
}
