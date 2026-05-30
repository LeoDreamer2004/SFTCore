package org.leodreamer.sftcore.integration.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiInitRegistry;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.gas.GasStack;
import net.minecraftforge.fluids.FluidType;

@EmiEntrypoint
public class SFTEmiPlugin implements EmiPlugin {
    @Override
    public void initialize(EmiInitRegistry registry) {
        registry.addIngredientSerializer(SFTGasEmiStack.class, SFTGasEmiStackSerializer.INSTANCE);
    }

    @Override
    public void register(EmiRegistry registry) {
        MekanismAPI.gasRegistry().getValues().forEach(gas -> {
            if (!gas.isEmptyType() && !gas.isHidden()) {
                registry.addEmiStack(new SFTGasEmiStack(new GasStack(gas, FluidType.BUCKET_VOLUME)));
            }
        });

        registry.addGenericStackProvider(new SFTGasStackProvider());
    }
}
