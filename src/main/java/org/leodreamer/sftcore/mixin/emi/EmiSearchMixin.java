package org.leodreamer.sftcore.mixin.emi;

import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.search.EmiSearch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.awt.*;
import java.util.List;

@Mixin(value = EmiSearch.class, remap = false)
public abstract class EmiSearchMixin {

    @Redirect(
        method = "bake",
        at = @At(
            value = "INVOKE",
            target = "Ldev/emi/emi/api/stack/EmiStack;getTooltipText()Ljava/util/List;"
        )
    )
    private static List<Component> sftcore$skipTooltipBake(EmiStack stack) {
        return List.of();
    }
}
