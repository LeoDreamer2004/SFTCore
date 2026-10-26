package org.leodreamer.sftcore.mixin.ae2.crafting;

import org.leodreamer.sftcore.integration.ae2.feature.IExecutingCraftingJob;

import appeng.crafting.CraftingLink;
import appeng.crafting.execution.ExecutingCraftingJob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

// I Hate you AE2! Why your code are all private??
@Mixin(value = ExecutingCraftingJob.class, remap = false)
public interface ExecutingCraftingJobAccessor extends IExecutingCraftingJob {

    @Accessor(value = "link")
    @Override
    CraftingLink sftcore$getLink();
}
