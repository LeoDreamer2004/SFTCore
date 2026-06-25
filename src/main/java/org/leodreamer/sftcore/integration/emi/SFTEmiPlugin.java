package org.leodreamer.sftcore.integration.emi;

import org.leodreamer.sftcore.integration.emi.recipe.CEPatternRecipeHandler;

import com.gregtechceu.gtceu.common.data.GTMenuTypes;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;

@EmiEntrypoint
public class SFTEmiPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        registry.addRecipeHandler(GTMenuTypes.MODULAR_CONTAINER.get(), new CEPatternRecipeHandler());
    }
}
