package org.leodreamer.sftcore.common.machine.trait.gas;

import org.leodreamer.sftcore.api.gui.ExportOnlyAEGasSlot;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;

import net.minecraft.MethodsReturnNonnullByDefault;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.MEStorage;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import mekanism.api.chemical.gas.GasStack;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.Predicate;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MEGasStockingInputHandler extends MEGasInputHandler {

    private boolean online;
    private MEStorage network;
    private IActionSource actionSource;

    public MEGasStockingInputHandler(int slots) {
        super(slots);
    }

    public void bindNetwork(boolean online, @Nullable MEStorage network, IActionSource actionSource) {
        this.online = online;
        this.network = network;
        this.actionSource = actionSource;
    }

    @Override
    public void syncFromME(MEStorage network, IActionSource actionSource) {
        bindNetwork(true, network, actionSource);

        for (var slot : inventory) {
            var config = slot.getConfig();

            if (!ExportOnlyAEGasSlot.isGas(config)) {
                slot.setStock(null);
                continue;
            }

            long available = network.extract(
                config.what(),
                Long.MAX_VALUE,
                Actionable.SIMULATE,
                actionSource
            );

            slot.setStock(available > 0 ? new GenericStack(config.what(), available) : null);
        }
    }

    @Override
    public void flushToME(@Nullable MEStorage network, IActionSource actionSource) {
        // Stocking hatch has no real local buffer.
    }

    @Override
    public List<GasStack> handleRecipeInner(
        IO io,
        GTRecipe recipe,
        List<GasStack> left,
        boolean simulate
    ) {
        if (io != IO.IN || !online || network == null) {
            return left;
        }

        var action = simulate ? Actionable.SIMULATE : Actionable.MODULATE;

        for (var it = left.iterator(); it.hasNext();) {
            var required = it.next();

            if (required == null || required.isEmpty()) {
                it.remove();
                continue;
            }

            var key = MekanismKey.of(required);
            if (key == null || key.getForm() != MekanismKey.GAS) {
                continue;
            }

            long need = required.getAmount();
            long extracted = network.extract(key, need, action, actionSource);

            if (extracted >= need) {
                it.remove();
            } else if (extracted > 0) {
                required.setAmount(need - extracted);
            }
        }

        return left;
    }

    public void refreshList(
        MEStorage network, IActionSource actionSource, long minStackSize, Predicate<GenericStack> filter
    ) {
        var topGases = new PriorityQueue<>(Comparator.comparingLong(Object2LongMap.Entry<AEKey>::getLongValue));

        for (var entry : network.getAvailableStacks()) {
            var what = entry.getKey();
            long amount = entry.getLongValue();

            if (!(what instanceof MekanismKey key) || key.getForm() != MekanismKey.GAS) {
                continue;
            }

            if (amount < minStackSize) {
                continue;
            }

            long extractable = network.extract(what, amount, Actionable.SIMULATE, actionSource);
            if (extractable <= 0) {
                continue;
            }

            var stack = new GenericStack(what, extractable);
            if (!filter.test(stack)) {
                continue;
            }

            if (topGases.size() < inventory.length) {
                topGases.offer(entry);
            } else if (topGases.peek() != null && amount > topGases.peek().getLongValue()) {
                topGases.poll();
                topGases.offer(entry);
            }
        }

        int gasCount = topGases.size();
        int index = 0;

        while (!topGases.isEmpty()) {
            var entry = topGases.poll();
            var what = entry.getKey();

            long extractable = network.extract(
                what,
                entry.getLongValue(),
                Actionable.SIMULATE,
                actionSource
            );

            int slotIndex = gasCount - index - 1;
            inventory[slotIndex].setConfig(new GenericStack(what, 1));
            inventory[slotIndex].setStock(new GenericStack(what, extractable));
            index++;
        }

        clearInventory(index);
    }

    @Override
    public boolean isStocking() {
        return true;
    }
}
