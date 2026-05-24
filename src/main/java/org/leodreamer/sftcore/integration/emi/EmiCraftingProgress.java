package org.leodreamer.sftcore.integration.emi;

import com.google.common.collect.Lists;
import dev.emi.emi.api.recipe.EmiPlayerInventory;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.bom.BoM;
import dev.emi.emi.runtime.EmiFavorite;
import dev.emi.emi.runtime.EmiFavorites;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

public final class EmiCraftingProgress {
    public static final int COLOR = 0xFF00D9C8;

    private static final List<Entry> ENTRIES = Lists.newArrayList();

    private static final Set<EmiPlayerInventory> PATCHED_INVENTORIES =
        Collections.newSetFromMap(new WeakHashMap<>());

    private static final Set<EmiFavorite.Synthetic> IN_PROGRESS_SYNTHETICS =
        Collections.newSetFromMap(new WeakHashMap<>());

    @Nullable
    private static PendingMark pendingMark;

    private EmiCraftingProgress() {
    }

    public static void prepareMark(EmiRecipe recipe, int requestedBatches) {
        if (!BoM.craftingMode || BoM.tree == null || recipe == null || recipe.getOutputs().isEmpty()) {
            pendingMark = null;
            return;
        }

        var output = canonical(recipe.getOutputs().get(0));
        long amount = estimateTargetAmount(recipe, requestedBatches);

        if (amount <= 0) {
            pendingMark = null;
            return;
        }

        long baseline = 0;
        var player = Minecraft.getInstance().player;
        if (player != null) {
            baseline = countInInventory(EmiPlayerInventory.of(player), output);
        }

        pendingMark = new PendingMark(output, amount, baseline);
    }

    public static void commitPreparedMark(boolean success) {
        if (!success || pendingMark == null) {
            pendingMark = null;
            return;
        }

        reconcileWithCurrentPlayerInventory();

        var existing = findEntry(pendingMark.stack);
        if (existing == null) {
            ENTRIES.add(new Entry(pendingMark.stack, pendingMark.amount, pendingMark.baseline));
        } else {
            existing.amount += pendingMark.amount;
            existing.baseline = pendingMark.baseline;
        }

        pendingMark = null;
    }

    public static void clear() {
        ENTRIES.clear();
        PATCHED_INVENTORIES.clear();
        IN_PROGRESS_SYNTHETICS.clear();
        pendingMark = null;
    }

    public static boolean hasAny() {
        reconcileWithCurrentPlayerInventory();
        return !ENTRIES.isEmpty();
    }

    public static void patchInventory(EmiPlayerInventory inventory) {
        if (inventory == null || PATCHED_INVENTORIES.contains(inventory)) {
            return;
        }

        PATCHED_INVENTORIES.add(inventory);
        reconcile(inventory);

        for (var entry : ENTRIES) {
            mergeStack(inventory, entry.stack, entry.amount);
        }
    }

    public static void appendSyntheticFavorites() {
        var player = Minecraft.getInstance().player;
        if (player != null) {
            reconcile(EmiPlayerInventory.of(player));
        }

        if (ENTRIES.isEmpty()) {
            return;
        }

        // EMI Will close the crafting mode when there's no recipe left.
        // Keep it if there is anything crafting
        if (BoM.tree != null) {
            BoM.craftingMode = true;
        }

        for (Entry entry : ENTRIES) {
            if (entry.amount <= 0) {
                continue;
            }

            var synthetic =
                new EmiFavorite.Synthetic(entry.stack.copy().setAmount(1), entry.amount, entry.amount);

            IN_PROGRESS_SYNTHETICS.add(synthetic);
            EmiFavorites.syntheticFavorites.add(synthetic);
        }
    }

    public static boolean isInProgressSynthetic(EmiFavorite.Synthetic synthetic) {
        return IN_PROGRESS_SYNTHETICS.contains(synthetic);
    }

    public static long getInProgressAmount(EmiIngredient ingredient) {
        var player = Minecraft.getInstance().player;
        if (player != null) {
            reconcile(EmiPlayerInventory.of(player));
        }

        long amount = 0;
        for (var entry : ENTRIES) {
            if (EmiIngredient.areEqual(entry.stack, ingredient)) {
                amount += entry.amount;
            }
        }
        return amount;
    }

    private static long estimateTargetAmount(EmiRecipe recipe, int requestedBatches) {
        for (var synthetic : EmiFavorites.syntheticFavorites) {
            if (sameRecipe(synthetic.getRecipe(), recipe)) {
                if (synthetic.batches == requestedBatches) {
                    return synthetic.amount;
                }
            }
        }

        if (recipe.getOutputs().isEmpty()) {
            return 0;
        }

        return Math.max(1L, requestedBatches) * recipe.getOutputs().get(0).getAmount();
    }

    private static boolean sameRecipe(@Nullable EmiRecipe a, EmiRecipe b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null || a.getId() == null || b.getId() == null) {
            return false;
        }
        return a.getId().equals(b.getId());
    }

    private static void reconcileWithCurrentPlayerInventory() {
        var player = Minecraft.getInstance().player;
        if (player != null) {
            reconcile(EmiPlayerInventory.of(player));
        }
    }

    private static void reconcile(EmiPlayerInventory inventory) {
        var iterator = ENTRIES.iterator();

        while (iterator.hasNext()) {
            var entry = iterator.next();
            long current = countInInventory(inventory, entry.stack);
            long produced = Math.max(0, current - entry.baseline);

            if (produced >= entry.amount) {
                iterator.remove();
            } else if (produced > 0) {
                entry.amount -= produced;
                entry.baseline = current;
            }
        }
    }

    @Nullable
    private static Entry findEntry(EmiStack stack) {
        for (var entry : ENTRIES) {
            if (EmiIngredient.areEqual(entry.stack, stack)) {
                return entry;
            }
        }
        return null;
    }

    private static long countInInventory(EmiPlayerInventory inventory, EmiStack target) {
        long amount = 0;

        for (var stack : inventory.inventory.values()) {
            if (stack.isEqual(target)) {
                amount += stack.getAmount();
            }
        }

        return amount;
    }

    private static void mergeStack(EmiPlayerInventory inventory, EmiStack stack, long amount) {
        if (amount <= 0 || stack.isEmpty()) {
            return;
        }

        var inserted = stack.copy()
            .setAmount(amount)
            .setChance(1)
            .comparison(comparison -> Comparison.DEFAULT_COMPARISON);

        inventory.inventory.merge(inserted, inserted, (a, b) -> a.setAmount(a.getAmount() + b.getAmount()));
    }

    private static EmiStack canonical(EmiStack stack) {
        return stack.copy()
            .setAmount(1)
            .setChance(1)
            .comparison(comparison -> Comparison.DEFAULT_COMPARISON);
    }

    private record PendingMark(EmiStack stack, long amount, long baseline) {
    }

    private static final class Entry {
        private final EmiStack stack;
        private long amount;
        private long baseline;

        private Entry(EmiStack stack, long amount, long baseline) {
            this.stack = stack;
            this.amount = amount;
            this.baseline = baseline;
        }
    }
}
