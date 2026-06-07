package org.leodreamer.sftcore.common.machine.trait.gas;

import org.leodreamer.sftcore.api.recipe.capability.GasRecipeCapability;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.machine.trait.MachineTraitType;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableRecipeHandlerTrait;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.integration.ae2.utils.KeyStorage;

import net.minecraft.MethodsReturnNonnullByDefault;

import me.ramidzkh.mekae2.ae2.MekanismKey;
import mekanism.api.chemical.gas.GasStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MEGasOutputHandler extends NotifiableRecipeHandlerTrait<GasStack> {

    private final KeyStorage internalBuffer;

    public MEGasOutputHandler(KeyStorage internalBuffer) {
        this.internalBuffer = internalBuffer;
    }

    public static final MachineTraitType<?> TYPE = new MachineTraitType<>(MEGasOutputHandler.class);

    @Override
    public IO getHandlerIO() {
        return IO.OUT;
    }

    @Override
    public MachineTraitType<?> getTraitType() {
        return TYPE;
    }

    @Override
    public RecipeCapability<GasStack> getCapability() {
        return GasRecipeCapability.CAP;
    }

    @Override
    public int getSize() {
        return 128;
    }

    @Override
    public List<Object> getContents() {
        return Collections.emptyList();
    }

    @Override
    public double getTotalContentAmount() {
        return 0;
    }

    @Override
    public boolean shouldSearchContent() {
        return false;
    }

    @Override
    public @Nullable List<GasStack> handleRecipeInner(
        IO io,
        GTRecipe recipe,
        List<GasStack> left,
        boolean simulate
    ) {
        if (io != IO.OUT) {
            return left;
        }

        for (var it = left.iterator(); it.hasNext();) {
            var gas = it.next();

            if (gas.isEmpty()) {
                it.remove();
                continue;
            }

            long accepted = insertGas(gas, simulate);

            if (accepted <= 0) {
                continue;
            }

            long remaining = gas.getAmount() - accepted;
            if (remaining <= 0) {
                it.remove();
            } else {
                gas.setAmount(remaining);
            }
        }

        return left.isEmpty() ? null : left;
    }

    public long insertGas(GasStack gas, boolean simulate) {
        var key = MekanismKey.of(gas);

        if (key == null || key.getForm() != MekanismKey.GAS) {
            return 0;
        }

        long amount = gas.getAmount();
        if (amount <= 0) {
            return 0;
        }

        long oldAmount = internalBuffer.storage.getOrDefault(key, 0L);
        long accepted = Math.min(Long.MAX_VALUE - oldAmount, amount);

        if (accepted <= 0) {
            return 0;
        }

        if (!simulate) {
            internalBuffer.storage.put(key, oldAmount + accepted);
            internalBuffer.onChanged();
        }

        return accepted;
    }
}
