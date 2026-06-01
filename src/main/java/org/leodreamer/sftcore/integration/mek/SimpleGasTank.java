package org.leodreamer.sftcore.integration.mek;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;

import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.NBTConstants;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasTank;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SimpleGasTank implements IGasTank {

    private final long capacity;
    private GasStack stored = GasStack.EMPTY;
    private Runnable onChanged = () -> {};

    public SimpleGasTank(long capacity) {
        this.capacity = capacity;
    }

    public void setOnContentsChanged(Runnable onChanged) {
        this.onChanged = onChanged;
    }

    public void onContentsChanged() {
        onChanged.run();
    }

    @Override
    public GasStack getStack() {
        return stored;
    }

    @Override
    public void setStack(GasStack stack) {
        setStackUnchecked(stack);
        onContentsChanged();
    }

    @Override
    public void setStackUnchecked(GasStack stack) {
        if (stack.isEmpty()) {
            stored = GasStack.EMPTY;
        } else {
            stored = new GasStack(stack, Math.min(stack.getAmount(), capacity));
        }
    }

    @Override
    public long getCapacity() {
        return capacity;
    }

    @Override
    public boolean isValid(GasStack stack) {
        return true;
    }

    @Override
    public GasStack insert(GasStack stack, Action action, AutomationType automationType) {
        if (stack.isEmpty() || !isValid(stack)) {
            return stack;
        }

        if (!stored.isEmpty() && !stored.isTypeEqual(stack)) {
            return stack;
        }

        long storedAmount = stored.getAmount();
        long accepted = Math.min(capacity - storedAmount, stack.getAmount());

        if (accepted <= 0) {
            return stack;
        }

        if (action.execute()) {
            if (stored.isEmpty()) {
                stored = new GasStack(stack, accepted);
            } else {
                stored.grow(accepted);
            }
            onContentsChanged();
        }

        long remaining = stack.getAmount() - accepted;
        return remaining <= 0 ? GasStack.EMPTY : new GasStack(stack, remaining);
    }

    @Override
    public GasStack extract(long amount, Action action, AutomationType automationType) {
        if (stored.isEmpty() || amount <= 0) {
            return GasStack.EMPTY;
        }

        long extracted = Math.min(amount, stored.getAmount());
        var result = new GasStack(stored, extracted);

        if (action.execute()) {
            stored.shrink(extracted);
            if (stored.isEmpty()) {
                stored = GasStack.EMPTY;
            }
            onContentsChanged();
        }

        return result;
    }

    @Override
    public CompoundTag serializeNBT() {
        var tag = new CompoundTag();
        if (!stored.isEmpty()) {
            tag.put(NBTConstants.STORED, stored.write(new CompoundTag()));
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        setStackUnchecked(GasStack.readFromNBT(nbt.getCompound(NBTConstants.STORED)));
    }
}
