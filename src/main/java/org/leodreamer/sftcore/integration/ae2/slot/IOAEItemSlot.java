package org.leodreamer.sftcore.integration.ae2.slot;

import com.gregtechceu.gtceu.integration.ae2.slot.ExportOnlyAEItemSlot;
import com.gregtechceu.gtceu.utils.GTMath;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.ItemStack;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class IOAEItemSlot extends ExportOnlyAEItemSlot {

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (slot != 0 || config == null) return stack;

        var what = config.what();

        if (!what.matches(stock)) return stack;
        if (!(what instanceof AEItemKey itemKey)) return stack;
        if (!itemKey.matches(stack)) return stack;

        // check the left available amount
        long origin = stock != null ? stock.amount() : 0;
        long left = config.amount() - origin;
        if (left <= 0) return stack;
        int insert = GTMath.saturatedCast(Math.min(left, stack.getCount()));
        if (!simulate) {
            stock = new GenericStack(what, origin + insert);
        }
        onContentsChanged();
        return stack.copyWithCount(stack.getCount() - insert);
    }
}
