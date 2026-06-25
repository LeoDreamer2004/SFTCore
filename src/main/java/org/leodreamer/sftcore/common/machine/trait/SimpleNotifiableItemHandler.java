package org.leodreamer.sftcore.common.machine.trait;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class SimpleNotifiableItemHandler implements IItemHandlerModifiable {

    @NotNull
    private ItemStack stack = ItemStack.EMPTY;
    private final Consumer<ItemStack> onChange;
    private final Runnable onClear;

    public SimpleNotifiableItemHandler(Consumer<ItemStack> onChange, Runnable onClear) {
        this.onChange = onChange;
        this.onClear = onClear;
    }

    public SimpleNotifiableItemHandler(Consumer<ItemStack> onChange) {
        this(onChange, () -> onChange.accept(ItemStack.EMPTY));
    }

    @Override
    public int getSlots() {
        return 1;
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return stack;
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        this.stack = stack.copy();
        if (this.stack.isEmpty()) {
            onClear.run();
        } else {
            onChange.accept(this.stack);
        }
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (!simulate) {
            setStackInSlot(slot, stack);
        }
        return stack;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        var extracted = stack.copy();
        if (!simulate) {
            this.stack = ItemStack.EMPTY;
            onClear.run();
        }
        return extracted;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return true;
    }
}
