package org.leodreamer.sftcore;

import org.leodreamer.sftcore.common.data.SFTBlocks;
import org.leodreamer.sftcore.common.data.SFTCreativeTabs;
import org.leodreamer.sftcore.common.data.SFTItems;
import org.leodreamer.sftcore.common.data.SFTOres;
import org.leodreamer.sftcore.common.data.SFTRecipes;

import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;

import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

@GTAddon
public class SFTGTAddon implements IGTAddon {

    @Override
    public GTRegistrate getRegistrate() {
        return SFTCore.REGISTRATE;
    }

    @Override
    public void initializeAddon() {
        SFTItems.init();
        SFTBlocks.init();
        SFTCreativeTabs.init();
    }

    @Override
    public String addonModId() {
        return SFTCore.MOD_ID;
    }

    @Override
    public void addRecipes(Consumer<FinishedRecipe> provider) {
        SFTRecipes.init(provider);
    }

    @Override
    public void registerOreVeins() {
        SFTOres.init();
    }
}
