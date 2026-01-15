package org.leodreamer.sftcore.mixin.ae2.crafting;

import org.leodreamer.sftcore.integration.ae2.feature.IExecutingCraftingJob;

import appeng.api.crafting.IPatternDetails;
import appeng.crafting.CraftingLink;
import appeng.crafting.execution.ExecutingCraftingJob;
import appeng.crafting.inv.ListCraftingInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

// I Hate you AE2! Why your code are all private??
@Mixin(ExecutingCraftingJob.class)
public interface ExecutingCraftingJobAccessor extends IExecutingCraftingJob {

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
