package org.leodreamer.sftcore.integration.ae2.gui;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import com.gregtechceu.gtceu.api.gui.widget.PhantomSlotWidget;
import net.minecraft.world.item.BucketItem;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

/**
 * A proxy generic widget with bucket to mark fluids.
 * Amounts are always set to be 1
 */
public class PhantomGenericSlotWidget extends PhantomSlotWidget {

    public PhantomGenericSlotWidget(IItemHandlerModifiable itemHandler, int slotIndex, int xPosition, int yPosition) {
        super(itemHandler, slotIndex, xPosition, yPosition);
    }

    @Nullable
    public GenericStack getStack() {
        var item = getItem().getItem();
        if (item instanceof BucketItem bucketItem) {
            return GenericStack.fromFluidStack(new FluidStack(bucketItem.getFluid(), 1));
        }
        return GenericStack.fromItemStack(item.getDefaultInstance());
    }

    public void setStack(GenericStack stack) {
        if (stack.what() instanceof AEFluidKey fluidKey) {
            setItem(fluidKey.getFluid().getBucket().getDefaultInstance());
        } else if (stack.what() instanceof AEItemKey itemKey) {
            setItem(itemKey.toStack());
        }
    }
}
