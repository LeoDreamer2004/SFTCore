package org.leodreamer.sftcore.mixin.advancedae;

import org.leodreamer.sftcore.common.data.SFTItems;
import org.leodreamer.sftcore.integration.ae2.feature.IExecutingCraftingJob;
import org.leodreamer.sftcore.integration.ae2.feature.IScaleUpCraftingProvider;
import org.leodreamer.sftcore.integration.ae2.logic.ScaledProcessingPattern;

import com.gregtechceu.gtceu.utils.GTMath;

import net.minecraft.world.level.Level;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import appeng.crafting.execution.CraftingCpuHelper;
import appeng.crafting.inv.ListCraftingInventory;
import appeng.crafting.pattern.AEProcessingPattern;
import appeng.me.service.CraftingService;
import net.pedroksl.advanced_ae.common.cluster.AdvCraftingCPU;
import net.pedroksl.advanced_ae.common.logic.AdvCraftingCPULogic;
import net.pedroksl.advanced_ae.common.logic.ExecutingCraftingJob;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

/**
 * AAE IS JUST A COPIER...
 * See {@link org.leodreamer.sftcore.mixin.ae2.crafting.CraftingCpuLogicMixin}
 */
@Mixin(AdvCraftingCPULogic.class)
public abstract class AdvCraftingCpuLogicMixin {

    @Shadow(remap = false)
    @Final
    AdvCraftingCPU cpu;

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
    @Nullable
    public abstract GenericStack getFinalJobOutput();

    @Shadow(remap = false)
    public abstract long getWaitingFor(AEKey template);

    @Shadow(remap = false)
    protected abstract void finishJob(boolean success);

    @Shadow(remap = false)
    private boolean markedForDeletion;

    /**
     * @author leodreamer
     * @reason add order item judgment
     */
    @Overwrite(remap = false)
    public void tickCraftingLogic(IEnergyService eg, CraftingService cc) {
        // Don't tick if we're not active.
        if (!cpu.isActive()) return;
        cantStoreItems = false;
        // If we don't have a job, just try to dump our items.
        if (job == null) {
            storeItems();
            if (!inventory.list.isEmpty()) {
                cantStoreItems = true;
            } else if (markedForDeletion) {
                cpu.deactivate();
            }
            return;
        }

        var link = ((IExecutingCraftingJob) job).sftcore$getLink();
        // Check if the job was canceled.
        if (link.isCanceled()) {
            cancel();
            return;
        }

        var remainingOperations = cpu.getCoProcessors() + 1 - (usedOps[0] + usedOps[1] + usedOps[2]);
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
                var pushedPatterns = executeCrafting(remainingOperations, cc, eg, cpu.getLevel());
                if (isOrder) {
                    long orderDone = getWaitingFor(orderKey);
                    if (finish.amount() <= orderDone) {
                        // skip order waiting
                        finishJob(true);
                        cpu.updateOutput(null);
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

    /**
     * @author LeoDreamer
     * @reason Optimize of automatically scaling up
     */
    @Overwrite(remap = false)
    public int executeCrafting(
        int maxPatterns, CraftingService craftingService, IEnergyService energyService,
        Level level
    ) {
        var job = (IExecutingCraftingJob) this.job;
        if (job == null)
            return 0;

        var pushedPatterns = 0;

        var tasks = job.sftcore$getTasks();
        var it = tasks.entrySet().iterator();
        taskLoop:
        while (it.hasNext()) {
            var task = it.next();
            if (IExecutingCraftingJob.getProgress(task.getValue()) <= 0) {
                it.remove();
                continue;
            }

            var details = task.getKey();
            var expectedOutputs = new KeyCounter();
            var expectedContainerItems = new KeyCounter();
            // Contains the inputs for the pattern.
            @Nullable
            var craftingContainer = CraftingCpuHelper.extractPatternInputs(
                details, inventory, level, expectedOutputs, expectedContainerItems
            );

            // Try to push to each provider.
            for (var provider : craftingService.getProviders(details)) {
                if (craftingContainer == null)
                    break;
                if (provider.isBusy())
                    continue;

                var patternPower = CraftingCpuHelper.calculatePatternPower(craftingContainer);

                if (
                    energyService.extractAEPower(
                        patternPower, Actionable.SIMULATE,
                        PowerMultiplier.CONFIG
                    ) < patternPower - 0.01
                )
                    break;

                long value = IExecutingCraftingJob.getProgress(task.getValue());
                int multiplier = 1;
                if (details instanceof AEProcessingPattern && provider instanceof IScaleUpCraftingProvider) {
                    int left = GTMath.saturatedCast(value);
                    multiplier = Math.min(maxPatterns, left);
                    if (multiplier <= 0) {
                        it.remove();
                        continue taskLoop;
                    }
                    details = new ScaledProcessingPattern(details, multiplier);
                    for (var counter : craftingContainer) {
                        if (counter != null) {
                            for (var entry : counter) {
                                entry.setValue(entry.getLongValue() * multiplier);
                            }
                        }
                    }
                }

                if (provider.pushPattern(details, craftingContainer)) {
                    energyService.extractAEPower(patternPower, Actionable.MODULATE, PowerMultiplier.CONFIG);
                    pushedPatterns += multiplier;

                    var waitingFor = job.sftcore$getWaitingFor();
                    for (var expectedOutput : expectedOutputs) {
                        waitingFor.insert(
                            expectedOutput.getKey(),
                            expectedOutput.getLongValue() * multiplier, Actionable.MODULATE
                        );
                    }
                    for (var expectedContainerItem : expectedContainerItems) {
                        waitingFor.insert(
                            expectedContainerItem.getKey(),
                            expectedContainerItem.getLongValue() * multiplier, Actionable.MODULATE
                        );
                    }
                    cpu.markDirty();

                    value -= multiplier;
                    if (value <= 0) {
                        it.remove();
                        continue taskLoop;
                    } else {
                        IExecutingCraftingJob.setProgress(task.getValue(), value);
                    }

                    if (pushedPatterns == maxPatterns) {
                        break taskLoop;
                    }

                    // Prepare next inputs.
                    expectedOutputs.reset();
                    expectedContainerItems.reset();
                    craftingContainer = CraftingCpuHelper.extractPatternInputs(
                        details, inventory,
                        level, expectedOutputs, expectedContainerItems
                    );
                }
            }

            // Failed to push this pattern, reinject the inputs.
            if (craftingContainer != null) {
                CraftingCpuHelper.reinjectPatternInputs(inventory, craftingContainer);
            }
        }

        return pushedPatterns;
    }
}
