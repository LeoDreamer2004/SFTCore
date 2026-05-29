package org.leodreamer.sftcore.api.gui;

import net.minecraft.MethodsReturnNonnullByDefault;

import mekanism.api.Action;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class DisplayGasHandler implements IGasHandler {

    private final ArrayList<GasStack> gases;

    public DisplayGasHandler(List<GasStack> gases) {
        this.gases = new ArrayList<>(gases.size());

        for (var stack : gases) {
            this.gases.add(stack == null || stack.isEmpty() ? GasStack.EMPTY : stack.copy());
        }
    }

    @Override
    public int getTanks() {
        return gases.size();
    }

    @Override
    public GasStack getChemicalInTank(int tank) {
        if (tank < 0 || tank >= gases.size()) {
            return GasStack.EMPTY;
        }
        return gases.get(tank);
    }

    @Override
    public void setChemicalInTank(int i, GasStack stack) {
        if (i < 0 || i >= gases.size()) {
            return;
        }
        gases.set(i, stack.isEmpty() ? GasStack.EMPTY : stack.copy());
    }

    @Override
    public long getTankCapacity(int tank) {
        GasStack stack = getChemicalInTank(tank);
        return stack.isEmpty() ? 1 : Math.max(1, stack.getAmount());
    }

    @Override
    public boolean isValid(int tank, GasStack stack) {
        return false;
    }

    @Override
    public GasStack insertChemical(int tank, GasStack stack, Action action) {
        return stack;
    }

    @Override
    public GasStack extractChemical(int tank, long amount, Action action) {
        return GasStack.EMPTY;
    }
}
