package org.leodreamer.sftcore.common.machine.trait.gas;

import org.leodreamer.sftcore.api.gui.gas.ExportOnlyAEGasSlot;
import org.leodreamer.sftcore.api.gui.gas.GasGuiHelper;
import org.leodreamer.sftcore.api.recipe.capability.GasRecipeCapability;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.integration.ae2.slot.IConfigurableSlot;
import com.gregtechceu.gtceu.integration.ae2.slot.IConfigurableSlotList;

import net.minecraft.MethodsReturnNonnullByDefault;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.MEStorage;
import lombok.Getter;
import mekanism.api.Action;
import mekanism.api.chemical.gas.GasStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Copy version for gas from {@link com.gregtechceu.gtceu.integration.ae2.slot.ExportOnlyAEFluidList}
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ExportOnlyAEGasList extends NotifiableGasTank implements IConfigurableSlotList {

    @Getter
    protected final ExportOnlyAEGasSlot[] inventory;

    public ExportOnlyAEGasList(int slots) {
        super(slots, 0, IO.IN, IO.NONE);
        this.inventory = new ExportOnlyAEGasSlot[slots];

        for (int i = 0; i < slots; i++) {
            this.inventory[i] = createSlot();
            this.inventory[i].setOnContentsChanged(this::onContentsChanged);
        }
    }

    protected ExportOnlyAEGasSlot createSlot() {
        return new ExportOnlyAEGasSlot();
    }

    public void syncFromME(MEStorage network, IActionSource actionSource) {
        for (var slot : inventory) {
            var exceed = slot.exceedStack();
            if (exceed != null) {
                long inserted = network.insert(
                    exceed.what(),
                    exceed.amount(),
                    Actionable.MODULATE,
                    actionSource
                );

                if (inserted > 0) {
                    slot.drainGeneric(inserted, Action.EXECUTE);
                    continue;
                }
            }

            var request = slot.requestStack();
            if (request != null) {
                long extracted = network.extract(
                    request.what(),
                    request.amount(),
                    Actionable.MODULATE,
                    actionSource
                );

                if (extracted > 0) {
                    slot.addStack(new GenericStack(request.what(), extracted));
                }
            }
        }
    }

    public void flushToME(@Nullable MEStorage network, IActionSource actionSource) {
        if (network == null) {
            return;
        }

        for (var slot : inventory) {
            var stock = slot.getStock();
            if (stock == null || stock.amount() <= 0) {
                continue;
            }

            long inserted = network.insert(
                stock.what(),
                stock.amount(),
                Actionable.MODULATE,
                actionSource
            );

            if (inserted > 0) {
                slot.drainGeneric(inserted, Action.EXECUTE);
            }
        }
    }

    @Override
    public void onContentsChanged() {
        super.onContentsChanged();
    }

    @Override
    public IO getHandlerIO() {
        return IO.IN;
    }

    @Override
    public RecipeCapability<GasStack> getCapability() {
        return GasRecipeCapability.CAP;
    }

    @Override
    public int getSize() {
        return inventory.length;
    }

    @Override
    public List<Object> getContents() {
        var result = new ArrayList<>();

        for (var slot : inventory) {
            var gas = GasGuiHelper.getGasStack(slot.getStock());
            if (!gas.isEmpty()) {
                result.add(gas);
            }
        }

        return result;
    }

    @Override
    public double getTotalContentAmount() {
        long amount = 0;

        for (var slot : inventory) {
            var stock = slot.getStock();
            if (stock != null) {
                amount += stock.amount();
            }
        }

        return amount;
    }

    @Override
    public boolean shouldSearchContent() {
        return true;
    }

    @Override
    public List<GasStack> handleRecipeInner(
        IO io,
        GTRecipe recipe,
        List<GasStack> left,
        boolean simulate
    ) {
        if (io != IO.IN) {
            return left;
        }

        var action = simulate ? Action.SIMULATE : Action.EXECUTE;

        for (var it = left.iterator(); it.hasNext();) {
            var required = it.next();

            if (required == null || required.isEmpty()) {
                it.remove();
                continue;
            }

            long need = required.getAmount();

            for (var slot : inventory) {
                if (need <= 0) {
                    break;
                }

                var drained = slot.drain(new GasStack(required, need), action);
                need -= drained.getAmount();
            }

            if (need <= 0) {
                it.remove();
            } else {
                required.setAmount(need);
            }
        }

        return left;
    }

    @Override
    public IConfigurableSlot getConfigurableSlot(int index) {
        return inventory[index];
    }

    @Override
    public int getConfigurableSlots() {
        return inventory.length;
    }

    public boolean isAutoPull() {
        return false;
    }

    public boolean isStocking() {
        return false;
    }
}
