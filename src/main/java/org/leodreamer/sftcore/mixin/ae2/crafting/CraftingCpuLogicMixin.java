package org.leodreamer.sftcore.mixin.ae2.crafting;

import org.leodreamer.sftcore.common.data.SFTItems;
import org.leodreamer.sftcore.integration.ae2.feature.IExecutingCraftingJob;

import net.minecraft.world.level.Level;

import appeng.api.networking.energy.IEnergyService;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.crafting.execution.CraftingCpuLogic;
import appeng.crafting.execution.ExecutingCraftingJob;
import appeng.crafting.inv.ListCraftingInventory;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import appeng.me.service.CraftingService;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = CraftingCpuLogic.class, remap = false)
public abstract class CraftingCpuLogicMixin {

    @Shadow
    @Final
    CraftingCPUCluster cluster;

    @Shadow
    private ExecutingCraftingJob job;

    @Shadow
    private boolean cantStoreItems;

    @Shadow
    public abstract void storeItems();

    @Shadow
    @Final
    private ListCraftingInventory inventory;

    @Shadow
    public abstract void cancel();

    @Shadow
    @Final
    private int[] usedOps;

    @Shadow
    @Nullable
    public abstract GenericStack getFinalJobOutput();

    @Shadow
    public abstract long getWaitingFor(AEKey template);

    @Shadow
    protected abstract void finishJob(boolean success);

    @Shadow
    public abstract int executeCrafting(
        int maxPatterns, CraftingService craftingService, IEnergyService energyService, Level level
    );

    /**
     * @author leodreamer
     * @reason add order item judgment
     */
    @Overwrite
    public void tickCraftingLogic(IEnergyService eg, CraftingService cc) {
        // Don't tick if we're not active.
        if (!cluster.isActive()) {
            return;
        }
        cantStoreItems = false;
        // If we don't have a job, just try to dump our items.
        if (job == null) {
            storeItems();
            if (!inventory.list.isEmpty()) {
                cantStoreItems = true;
            }
            return;
        }

        var link = ((IExecutingCraftingJob) job).sftcore$getLink();
        // Check if the job was canceled.
        if (link.isCanceled()) {
            cancel();
            return;
        }

        var remainingOperations = cluster.getCoProcessors() + 1 - (usedOps[0] + usedOps[1] + usedOps[2]);
        final var started = remainingOperations;
        var finish = getFinalJobOutput();

        AEItemKey orderKey = null;
        boolean isOrder = false;

        if (
            finish != null && finish.what() instanceof AEItemKey finishKey &&
                finishKey.getItem() == SFTItems.ORDER.asItem()
        ) {
            isOrder = true;
            orderKey = finishKey;
        }

        if (remainingOperations > 0) {
            do {
                var pushedPatterns = executeCrafting(remainingOperations, cc, eg, cluster.getLevel());
                if (isOrder) {
                    long orderDone = getWaitingFor(orderKey);
                    if (finish.amount() <= orderDone) {
                        // skip order waiting
                        finishJob(true);
                        cluster.updateOutput(null);
                    }
                }

                if (pushedPatterns > 0) {
                    remainingOperations -= pushedPatterns;
                } else {
                    break;
                }
            } while (remainingOperations > 0);
        }
        usedOps[2] = usedOps[1];
        usedOps[1] = usedOps[0];
        usedOps[0] = started - remainingOperations;
    }
}
