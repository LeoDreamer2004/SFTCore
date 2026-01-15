package org.leodreamer.sftcore.mixin.gregtech.recipe;

import com.gregtechceu.gtceu.api.machine.feature.IOverclockMachine;
import com.gregtechceu.gtceu.api.recipe.OverclockingLogic;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;

import net.minecraft.Util;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(GTRecipeModifiers.class)
public class GTRecipeModifiersMixin {

    @Final
    @Shadow(remap = false)
    @Mutable
    public static Function<OverclockingLogic, RecipeModifier> ELECTRIC_OVERCLOCK;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void fixOverlock(CallbackInfo ci) {
        ELECTRIC_OVERCLOCK = Util
            .memoize(logic -> (machine, recipe) -> {
                if (recipe.duration == 0) recipe.duration = 1;

                if (!(machine instanceof IOverclockMachine overclockMachine)) return ModifierFunction.IDENTITY;
                if (RecipeHelper.getRecipeEUtTier(recipe) > overclockMachine.getMaxOverclockTier()) {
                    return ModifierFunction.NULL;
                }
                return logic.getModifier(machine, recipe, overclockMachine.getOverclockVoltage());
            });
    }
}
