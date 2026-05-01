package org.leodreamer.sftcore.integration.ae2.slot;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.integration.ae2.slot.ExportOnlyAEItemList;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class IOAEItemList extends ExportOnlyAEItemList {

    private CustomItemStackHandler itemHandler;

    public IOAEItemList(MetaMachine holder, int slots) {
        super(holder, slots);
        // overwrite the inventory with `IOAEItemSlot`
        inventory = new IOAEItemSlot[slots];
        for (int i = 0; i < slots; i++) {
            var slot = new IOAEItemSlot();
            slot.setOnContentsChanged(this::onContentsChanged);
            inventory[i] = slot;
        }
    }

    @Override
    public CustomItemStackHandler getHandler() {
        if (itemHandler == null) {
            itemHandler = new ItemStackHandlerDelegate((IOAEItemSlot[]) inventory);
        }
        return itemHandler;
    }

    @Override
    public IO getHandlerIO() {
        return IO.BOTH;
    }

    @Override
    public List<Ingredient> handleRecipeInner(IO io, GTRecipe recipe, List<Ingredient> left, boolean simulate) {
        // use `io` instead of `handlerIO` here to cheat
        return NotifiableItemStackHandler.handleRecipe(io, recipe, left, simulate, io, getHandler());
    }

    private static class ItemStackHandlerDelegate extends CustomItemStackHandler {

        private final IOAEItemSlot[] inventory;

        public ItemStackHandlerDelegate(IOAEItemSlot[] inventory) {
            super();
            this.inventory = inventory;
        }

        @Override
        public int getSlots() {
            return inventory.length;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return inventory[slot].getStackInSlot(0);
        }

        @Override
        public void setStackInSlot(int slot, ItemStack stack) {
            // NO-OP
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            validateSlotIndex(slot);
            return inventory[slot].insertItem(0, stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (amount == 0) return ItemStack.EMPTY;
            validateSlotIndex(slot);
            return inventory[slot].extractItem(0, amount, simulate);
        }

        @Override
        protected void validateSlotIndex(int slot) {
            if (slot < 0 || slot >= getSlots())
                throw new RuntimeException(
                    "Slot " + slot + " not in valid range - [0," + getSlots() + ")");
        }

        @Override
        public int getSlotLimit(int slot) {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return false;
        }
    }
}
