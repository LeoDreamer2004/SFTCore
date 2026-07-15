package org.leodreamer.sftcore.api.gui.gas;

import com.gregtechceu.gtceu.integration.ae2.slot.ExportOnlyAESlot;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;

import appeng.api.stacks.GenericStack;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import mekanism.api.Action;
import mekanism.api.chemical.gas.GasStack;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ExportOnlyAEGasSlot extends ExportOnlyAESlot {

    public ExportOnlyAEGasSlot() {
        super();
    }

    public ExportOnlyAEGasSlot(@Nullable GenericStack config, @Nullable GenericStack stock) {
        super(copyGas(config), copyGas(stock));
    }

    @Override
    public void addStack(GenericStack stack) {
        if (!GasGuiHelper.isGas(stack)) {
            return;
        }

        if (this.stock == null) {
            this.stock = copy(stack);
        } else if (this.stock.what().equals(stack.what())) {
            this.stock = new GenericStack(this.stock.what(), this.stock.amount() + stack.amount());
        } else {
            this.stock = copy(stack);
        }

        onContentsChanged();
    }

    @Override
    public void setStock(@Nullable GenericStack stack) {
        if (stack != null && !GasGuiHelper.isGas(stack)) {
            return;
        }

        if (this.stock == null && stack == null) {
            return;
        }

        if (
            this.stock != null && stack != null && this.stock.what().equals(stack.what()) &&
                this.stock.amount() == stack.amount()
        ) {
            return;
        }

        this.stock = stack == null ? null : copy(stack);
        onContentsChanged();
    }

    @Override
    public void setConfig(@Nullable GenericStack stack) {
        if (stack != null && !GasGuiHelper.isGas(stack)) {
            return;
        }

        if (this.config == null && stack == null) {
            return;
        }

        if (
            this.config != null && stack != null && this.config.what().equals(stack.what()) &&
                this.config.amount() == stack.amount()
        ) {
            return;
        }

        this.config = stack == null ? null : copy(stack);
        onContentsChanged();
    }

    public GasStack drain(GasStack requested, Action action) {
        if (requested.isEmpty() || this.stock == null) {
            return GasStack.EMPTY;
        }

        var key = MekanismKey.of(requested);
        if (!this.stock.what().equals(key)) {
            return GasStack.EMPTY;
        }

        long drained = Math.min(this.stock.amount(), requested.getAmount());
        if (drained <= 0) {
            return GasStack.EMPTY;
        }

        var result = new GasStack(requested, drained);

        if (action.execute()) {
            long remain = this.stock.amount() - drained;
            this.stock = remain <= 0 ? null : new GenericStack(this.stock.what(), remain);
            onContentsChanged();
        }

        return result;
    }

    public void drainGeneric(long amount, Action action) {
        if (this.stock == null || amount <= 0) {
            return;
        }

        long drained = Math.min(this.stock.amount(), amount);

        if (action.execute()) {
            long remain = this.stock.amount() - drained;
            this.stock = remain <= 0 ? null : new GenericStack(this.stock.what(), remain);
            onContentsChanged();
        }
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        setConfig(tag.contains(CONFIG_TAG) ? GenericStack.readTag(tag.getCompound(CONFIG_TAG)) : null);
        setStock(tag.contains(STOCK_TAG) ? GenericStack.readTag(tag.getCompound(STOCK_TAG)) : null);
    }

    private static @Nullable GenericStack copyGas(@Nullable GenericStack stack) {
        return GasGuiHelper.isGas(stack) ? ExportOnlyAESlot.copy(stack) : null;
    }

    private void onContentsChanged() {
        onContentsChanged.run();
    }

    @Override
    public ExportOnlyAEGasSlot copy() {
        return new ExportOnlyAEGasSlot(
            this.config == null ? null : ExportOnlyAESlot.copy(this.config),
            this.stock == null ? null : ExportOnlyAESlot.copy(this.stock)
        );
    }
}
