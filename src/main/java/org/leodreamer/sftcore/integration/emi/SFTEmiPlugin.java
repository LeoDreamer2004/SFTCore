package org.leodreamer.sftcore.integration.emi;

import org.leodreamer.sftcore.integration.emi.recipe.CEPatternRecipeHandler;

import com.lowdragmc.lowdraglib.gui.modular.ModularUIContainer;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;

@EmiEntrypoint
public class SFTEmiPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        registry.addRecipeHandler(ModularUIContainer.MENUTYPE, new CEPatternRecipeHandler());
    }
}
