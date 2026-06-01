package org.leodreamer.sftcore.mixin.emi;

import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.search.TooltipQuery;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TooltipQuery.class, remap = false)
public abstract class TooltipQueryMixin {

    @Shadow
    @Final
    private String name;

    /**
     * Fallback function when the user use `$` to search with tooltips
     * See {@link EmiSearchMixin}
     */
    @Inject(method = "matches", at = @At("HEAD"), cancellable = true)
    private void sftcore$fallbackTooltipSearch(EmiStack stack, CallbackInfoReturnable<Boolean> cir) {
        String needle = name.toLowerCase();

        for (var component : stack.getTooltipText()) {
            if (component.getString().toLowerCase().contains(needle)) {
                cir.setReturnValue(true);
                return;
            }
        }

        cir.setReturnValue(false);
    }
}
