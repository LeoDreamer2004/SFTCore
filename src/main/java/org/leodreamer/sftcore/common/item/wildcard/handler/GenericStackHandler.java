package org.leodreamer.sftcore.common.item.wildcard.handler;

import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import org.jetbrains.annotations.Nullable;

public class GenericStackHandler extends CustomItemStackHandler {

    public GenericStackHandler() {
        super(1);
    }

    public GenericStackHandler(@Nullable GenericStack stack) {
        this();
        setGenericStack(stack);
    }

    public void setGenericStack(@Nullable GenericStack stack) {
        setStackInSlot(0, sampleFromGeneric(stack));
    }

    public @Nullable GenericStack getGenericStack(long amount) {
        var stack = getStackInSlot(0);
        if (stack.isEmpty()) {
            return null;
        }
        if (stack.getItem() instanceof BucketItem bucket) {
            return new GenericStack(AEFluidKey.of(bucket.getFluid()), amount);
        }
        var key = AEItemKey.of(stack);
        return key == null ? null : new GenericStack(key, amount);
    }

    public static ItemStack sampleFromGeneric(@Nullable GenericStack stack) {
        if (stack == null || stack.what() == null) {
            return ItemStack.EMPTY;
        }
        if (stack.what() instanceof AEItemKey itemKey) {
            return itemKey.toStack();
        }
        if (stack.what() instanceof AEFluidKey fluidKey) {
            return new ItemStack(fluidKey.getFluid().getBucket());
        }
        return GenericStack.wrapInItemStack(stack);
    }
}
