package org.leodreamer.sftcore.integration.emi;

import com.lowdragmc.lowdraglib.gui.modular.ModularUIContainer;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import org.leodreamer.sftcore.integration.emi.recipe.MechanicalPatternRecipeHandler;

@EmiEntrypoint
public class SFTEmiPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        registry.addRecipeHandler(ModularUIContainer.MENUTYPE, new MechanicalPatternRecipeHandler());
    }
}
