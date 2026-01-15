package org.leodreamer.sftcore.mixin.advancedae;

import org.leodreamer.sftcore.integration.ae2.feature.IExecutingCraftingJob;

import appeng.api.crafting.IPatternDetails;
import appeng.crafting.CraftingLink;
import appeng.crafting.inv.ListCraftingInventory;
import net.pedroksl.advanced_ae.common.logic.ExecutingCraftingJob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

/**
 * AAE IS JUST A COPIER...
 * See {@link org.leodreamer.sftcore.mixin.ae2.crafting.ExecutingCraftingJobAccessor}
 */
@Mixin(ExecutingCraftingJob.class)
public interface AdvExecutingCraftingJobAccessor extends IExecutingCraftingJob {

    @Accessor(value = "link", remap = false)
    @Override
    CraftingLink sftcore$getLink();

    @Accessor(value = "waitingFor", remap = false)
    @Override
    ListCraftingInventory sftcore$getWaitingFor();

    @Accessor(value = "tasks", remap = false)
    @Override
    Map<IPatternDetails, ?> sftcore$getTasks();
}
