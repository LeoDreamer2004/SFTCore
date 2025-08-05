package org.leodreamer.sftcore;

import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import net.minecraft.data.recipes.FinishedRecipe;
import org.leodreamer.sftcore.common.data.recipes.SFTRecipes;

import java.util.function.Consumer;

@GTAddon
public class SFTGTAddon implements IGTAddon {
    @Override
    public GTRegistrate getRegistrate() {
        return SFTCore.REGISTRATE;
    }

    @Override
    public void initializeAddon() {
    }

    @Override
    public String addonModId() {
        return SFTCore.MOD_ID;
    }

    @Override
    public void addRecipes(Consumer<FinishedRecipe> provider) {
        SFTRecipes.init(provider);
    }
}