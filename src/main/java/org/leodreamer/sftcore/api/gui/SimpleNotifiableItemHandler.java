package org.leodreamer.sftcore.api.gui;

import net.minecraft.world.item.ItemStack;

import com.lowdragmc.lowdraglib.side.item.IItemTransfer;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class SimpleNotifiableItemHandler implements IItemTransfer {

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
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate, boolean notifyChanges) {
        this.stack = stack;
        onChange.accept(stack);
        return stack;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate, boolean notifyChanges) {
        this.stack = ItemStack.EMPTY;
        onClear.run();
        return stack;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return stack.getItem() == this.stack.getItem();
    }

    @Override
    @SuppressWarnings("all")
    public @NotNull Object createSnapshot() {
        return stack;
    }

    @Override
    @SuppressWarnings("all")
    public void restoreFromSnapshot(Object snapshot) {
        if (snapshot instanceof ItemStack itemStack) {
            stack = itemStack;
        }
    }
}
