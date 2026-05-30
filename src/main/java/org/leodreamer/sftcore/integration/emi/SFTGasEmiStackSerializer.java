package org.leodreamer.sftcore.integration.emi;

import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.serializer.EmiStackSerializer;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.gas.Gas;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class SFTGasEmiStackSerializer implements EmiStackSerializer<SFTGasEmiStack> {
    public static final SFTGasEmiStackSerializer INSTANCE = new SFTGasEmiStackSerializer();

    @Override
    public EmiStack create(ResourceLocation id, CompoundTag nbt, long amount) {
        Gas gas = MekanismAPI.gasRegistry().getValue(id);
        if (gas == null || gas.isEmptyType() || amount <= 0) {
            return EmiStack.EMPTY;
        }
        return new SFTGasEmiStack(gas, amount);
    }

    @Override
    public String getType() {
        return "sftcore_gas";
    }
}
