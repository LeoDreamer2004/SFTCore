package org.leodreamer.sftcore.mixin.ae2.misc;

import org.leodreamer.sftcore.common.data.SFTItems;
import org.leodreamer.sftcore.util.ReflectUtils;

import net.minecraft.world.level.Level;

import appeng.api.networking.energy.IEnergyService;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.crafting.CraftingLink;
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

@Mixin(CraftingCpuLogic.class)
public abstract class CraftingCpuLogicMixin {

    @Shadow(remap = false)
    @Final
    CraftingCPUCluster cluster;

    @Shadow(remap = false)
    private ExecutingCraftingJob job;

    @Shadow(remap = false)
    private boolean cantStoreItems;

    @Shadow(remap = false)
    public abstract void storeItems();

    @Shadow(remap = false)
    @Final
    private ListCraftingInventory inventory;

    @Shadow(remap = false)
    public abstract void cancel();

    @Shadow(remap = false)
    @Final
    private int[] usedOps;

    @Shadow(remap = false)
    public abstract int executeCrafting(
        int maxPatterns,
        CraftingService craftingService,
        IEnergyService energyService,
        Level level
    );

    @Shadow(remap = false)
    @Nullable
    public abstract GenericStack getFinalJobOutput();

    @Shadow(remap = false)
    public abstract long getWaitingFor(AEKey template);

    @Shadow(remap = false)
    protected abstract void finishJob(boolean success);

    /**
     * @author leodreamer
     * @reason add order item judgment
     */
    @Overwrite(remap = false)
    public void tickCraftingLogic(IEnergyService eg, CraftingService cc) {
        // Don't tick if we're not active.
        if (!cluster.isActive()) return;
        cantStoreItems = false;
        // If we don't have a job, just try to dump our items.
        if (this.job == null) {
            this.storeItems();
            if (!this.inventory.list.isEmpty()) {
                cantStoreItems = true;
            }
            return;
        }

        CraftingLink link = ReflectUtils.getFieldValue(job, "link", CraftingLink.class);
        // Check if the job was canceled.
        if (link.isCanceled()) {
            cancel();
            return;
        }

        var remainingOperations = cluster.getCoProcessors() + 1 - (this.usedOps[0] + this.usedOps[1] + this.usedOps[2]);
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
        this.usedOps[2] = this.usedOps[1];
        this.usedOps[1] = this.usedOps[0];
        this.usedOps[0] = started - remainingOperations;
    }
}
