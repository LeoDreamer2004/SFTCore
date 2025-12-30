package org.leodreamer.sftcore.api.gui;

import net.minecraft.world.level.material.Fluids;

import com.lowdragmc.lowdraglib.side.fluid.FluidStack;
import com.lowdragmc.lowdraglib.side.fluid.IFluidTransfer;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class SimpleNotifiableFluidHandler implements IFluidTransfer {

    @NotNull
    // private FluidStack stack = FluidStack.empty();
    private FluidStack stack = FluidStack.create(Fluids.WATER, 1000);
    private final Consumer<FluidStack> onChange;
    private final Runnable onClear;

    public SimpleNotifiableFluidHandler(Consumer<FluidStack> onChange, Runnable onClear) {
        this.onChange = onChange;
        this.onClear = onClear;
    }

    public SimpleNotifiableFluidHandler(Consumer<FluidStack> onChange) {
        this(onChange, () -> onChange.accept(FluidStack.empty()));
    }

    public SimpleNotifiableFluidHandler() {
        this((stack) -> {});
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        return stack;
    }

    @SuppressWarnings("all")
    @Override
    public void setFluidInTank(int tank, @NotNull FluidStack fluid) {
        stack = fluid;
    }

    @Override
    public long getTankCapacity(int tank) {
        return 1000L;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return true;
    }

    @SuppressWarnings("all")
    @Override
    public long fill(int tank, FluidStack resource, boolean simulate, boolean notifyChanges) {
        stack = resource;
        onChange.accept(stack);
        return 1000L;
    }

    @Override
    public boolean supportsFill(int tank) {
        return true;
    }

    @SuppressWarnings("all")
    @Override
    public @NotNull FluidStack drain(int tank, FluidStack resource, boolean simulate, boolean notifyChanges) {
        stack = FluidStack.empty();
        onClear.run();
        return stack;
    }

    @Override
    public boolean supportsDrain(int tank) {
        return false;
    }

    @SuppressWarnings("all")
    @Override
    public @NotNull Object createSnapshot() {
        return stack;
    }

    @SuppressWarnings("all")
    @Override
    public void restoreFromSnapshot(Object snapshot) {
        if (snapshot instanceof FluidStack fluidStack) {
            stack = fluidStack;
        }
    }
}
