package org.leodreamer.sftcore.mixin.forge;

import org.leodreamer.sftcore.util.RLUtils;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.IForgeRegistry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FluidStack.class)
public class FluidStackMixin {

    @Shadow(remap = false)
    private boolean isEmpty;

    @Unique
    private Fluid sftcore$fluid;

    @Redirect(
        method = "<init>(Lnet/minecraft/world/level/material/Fluid;I)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraftforge/registries/IForgeRegistry;getKey(Ljava/lang/Object;)Lnet/minecraft/resources/ResourceLocation;",
            ordinal = 0
        ), remap = false
    )
    private <V> ResourceLocation getKey(IForgeRegistry<?> instance, V v) {
        return RLUtils.EMPTY;
    }

    @Redirect(
        method = "<init>(Lnet/minecraft/world/level/material/Fluid;I)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraftforge/registries/IForgeRegistry;getDelegateOrThrow(Ljava/lang/Object;)Lnet/minecraft/core/Holder$Reference;"
        ), remap = false
    )
    private <V> Holder.Reference<V> getDelegateOrThrow(IForgeRegistry<?> instance, V v) {
        sftcore$fluid = (Fluid) v;
        return null;
    }

    /**
     * @author LeoDreamer
     * @reason Skip the low efficiency holder system.
     */
    @Overwrite(remap = false)
    public final Fluid getFluid() {
        return isEmpty ? Fluids.EMPTY : sftcore$fluid;
    }

    /**
     * @author LeoDreamer
     * @reason Skip the low efficiency holder system.
     */
    @Overwrite(remap = false)
    public final Fluid getRawFluid() {
        return sftcore$fluid;
    }
}
