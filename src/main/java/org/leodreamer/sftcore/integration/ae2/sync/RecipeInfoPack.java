package org.leodreamer.sftcore.integration.ae2.sync;

import org.leodreamer.sftcore.integration.ae2.feature.ISendToGTMachine;

import com.gregtechceu.gtceu.api.recipe.GTRecipeType;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public record RecipeInfoPack(ResourceLocation type, int circuit) {

    public static RecipeInfoPack pack(ISendToGTMachine.RecipeInfo info) {
        return new RecipeInfoPack(info.type().registryName, info.circuit());
    }

    public ISendToGTMachine.RecipeInfo unpack() {
        var gtType = (GTRecipeType) ForgeRegistries.RECIPE_TYPES.getValue(type);
        return new ISendToGTMachine.RecipeInfo(gtType, circuit);
    }
}
