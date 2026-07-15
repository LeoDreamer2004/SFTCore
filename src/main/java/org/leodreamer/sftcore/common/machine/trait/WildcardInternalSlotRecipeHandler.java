package org.leodreamer.sftcore.common.machine.trait;

import org.leodreamer.sftcore.common.machine.multiblock.part.WildcardMEPatternBufferPartMachine;

import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IFilteredHandler;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.machine.trait.MachineTraitType;
import com.gregtechceu.gtceu.api.machine.trait.notifiable.NotifiableRecipeHandlerTrait;
import com.gregtechceu.gtceu.api.machine.trait.recipe.RecipeHandlerGroupDistinctness;
import com.gregtechceu.gtceu.api.machine.trait.recipe.RecipeHandlerList;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.ingredient.FluidIngredient;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Dynamic recipe handlers for {@link WildcardMEPatternBufferPartMachine}, same as
 * {@link com.gregtechceu.gtceu.integration.ae2.machine.trait.InternalSlotRecipeHandler}.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class WildcardInternalSlotRecipeHandler {

    private final WildcardMEPatternBufferPartMachine buffer;
    private final List<SlotRHL> allSlotHandlers = new ArrayList<>();

    private int activeSize = 0;

    public WildcardInternalSlotRecipeHandler(WildcardMEPatternBufferPartMachine buffer) {
        this.buffer = buffer;
    }

    public void setActiveSize(int size) {
        if (size < 0) {
            size = 0;
        }

        ensureCapacity(size);
        this.activeSize = size;
    }

    public List<RecipeHandlerList> getSlotHandlers() {
        if (activeSize <= 0) {
            return List.of();
        }

        return new ArrayList<>(allSlotHandlers.subList(0, activeSize));
    }

    public @Nullable RecipeHandlerList getSlotHandler(int slot) {
        if (slot < 0 || slot >= activeSize) {
            return null;
        }

        return allSlotHandlers.get(slot);
    }

    public void notifySlotChanged(int slot) {
        if (slot < 0 || slot >= activeSize) {
            return;
        }

        allSlotHandlers.get(slot).notifySlotChanged();
    }

    private void ensureCapacity(int size) {
        while (allSlotHandlers.size() < size) {
            int index = allSlotHandlers.size();
            allSlotHandlers.add(new SlotRHL(buffer, index));
        }
    }

    @Getter
    protected static class SlotRHL extends RecipeHandlerList {

        private final SlotItemRecipeHandler itemRecipeHandler;
        private final SlotFluidRecipeHandler fluidRecipeHandler;

        public SlotRHL(WildcardMEPatternBufferPartMachine buffer, int index) {
            super(IO.IN);

            itemRecipeHandler = buffer.attachTrait(new SlotItemRecipeHandler(buffer, index));
            fluidRecipeHandler = buffer.attachTrait(new SlotFluidRecipeHandler(buffer, index));

            addHandlers(
                buffer.getCircuitSlot(),
                buffer.getShareInventory(),
                buffer.getShareTank(),
                itemRecipeHandler,
                fluidRecipeHandler
            );

            this.setGroup(RecipeHandlerGroupDistinctness.BUS_DISTINCT);
        }

        @Override
        public boolean isDistinct() {
            return true;
        }

        @Override
        public void setDistinct(boolean ignored, boolean notify) {}

        private void notifySlotChanged() {
            itemRecipeHandler.notifySlotChanged();
            fluidRecipeHandler.notifySlotChanged();
        }
    }

    @Getter
    private static class SlotItemRecipeHandler extends NotifiableRecipeHandlerTrait<Ingredient> {

        public static final MachineTraitType<SlotItemRecipeHandler> TYPE = new MachineTraitType<>(
            SlotItemRecipeHandler.class
        );

        private final WildcardMEPatternBufferPartMachine buffer;
        private final int index;
        private final int priority;
        private final int size = 81;
        @Getter
        private final RecipeCapability<Ingredient> capability = ItemRecipeCapability.CAP;
        private final IO handlerIO = IO.IN;
        private final boolean isDistinct = true;

        private SlotItemRecipeHandler(WildcardMEPatternBufferPartMachine buffer, int index) {
            super();
            this.buffer = buffer;
            this.index = index;
            this.priority = IFilteredHandler.HIGH + index + 1;
        }

        @Override
        public MachineTraitType<?> getTraitType() {
            return TYPE;
        }

        @Override
        public List<Ingredient> handleRecipeInner(
            IO io,
            GTRecipe recipe,
            List<Ingredient> left,
            boolean simulate
        ) {
            if (io != IO.IN) {
                return left;
            }
            var slot = buffer.getInternalSlot(index);
            if (slot == null || slot.isItemEmpty()) {
                return left;
            }
            return slot.handleItemInternal(left, simulate);
        }

        @Override
        public List<Object> getContents() {
            var slot = buffer.getInternalSlot(index);
            if (slot == null) {
                return List.of();
            }

            return new ArrayList<>(slot.getItems());
        }

        @Override
        public double getTotalContentAmount() {
            var slot = buffer.getInternalSlot(index);
            if (slot == null) {
                return 0;
            }

            return slot.getItems().stream()
                .mapToLong(ItemStack::getCount)
                .sum();
        }

        private void notifySlotChanged() {
            notifyListeners();
        }
    }

    @Getter
    private static class SlotFluidRecipeHandler extends NotifiableRecipeHandlerTrait<FluidIngredient> {

        public static final MachineTraitType<SlotFluidRecipeHandler> TYPE = new MachineTraitType<>(
            SlotFluidRecipeHandler.class
        );

        private final WildcardMEPatternBufferPartMachine buffer;
        private final int index;
        private final int priority;
        private final int size = 81;
        @Getter
        private final RecipeCapability<FluidIngredient> capability = FluidRecipeCapability.CAP;
        private final IO handlerIO = IO.IN;
        private final boolean isDistinct = true;

        private SlotFluidRecipeHandler(WildcardMEPatternBufferPartMachine buffer, int index) {
            super();
            this.buffer = buffer;
            this.index = index;
            this.priority = IFilteredHandler.HIGH + index + 1;
        }

        @Override
        public MachineTraitType<?> getTraitType() {
            return TYPE;
        }

        @Override
        public List<FluidIngredient> handleRecipeInner(
            IO io,
            GTRecipe recipe,
            List<FluidIngredient> left,
            boolean simulate
        ) {
            if (io != IO.IN) {
                return left;
            }
            var slot = buffer.getInternalSlot(index);
            if (slot == null || slot.isFluidEmpty()) {
                return left;
            }
            return slot.handleFluidInternal(left, simulate);
        }

        @Override
        public List<Object> getContents() {
            var slot = buffer.getInternalSlot(index);
            if (slot == null) {
                return List.of();
            }

            return new ArrayList<>(slot.getFluids());
        }

        @Override
        public double getTotalContentAmount() {
            var slot = buffer.getInternalSlot(index);
            if (slot == null) {
                return 0;
            }

            return slot.getFluids().stream()
                .mapToLong(FluidStack::getAmount)
                .sum();
        }

        private void notifySlotChanged() {
            notifyListeners();
        }
    }
}
