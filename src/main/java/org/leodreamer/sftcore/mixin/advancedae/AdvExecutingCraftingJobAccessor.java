package org.leodreamer.sftcore.mixin.advancedae;

import org.leodreamer.sftcore.integration.ae2.feature.IExecutingCraftingJob;

import appeng.crafting.CraftingLink;
import net.pedroksl.advanced_ae.common.logic.ExecutingCraftingJob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * AAE IS JUST A COPIER...
 * See {@link org.leodreamer.sftcore.mixin.ae2.crafting.ExecutingCraftingJobAccessor}
 */
@Mixin(ExecutingCraftingJob.class)
public interface AdvExecutingCraftingJobAccessor extends IExecutingCraftingJob {

    @Accessor(value = "link", remap = false)
    @Override
    CraftingLink sftcore$getLink();
}
