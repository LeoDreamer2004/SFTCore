package org.leodreamer.sftcore.common.data;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.common.data.recipe.misc.*;

import java.util.function.Consumer;

public class SFTRecipes {
    public static void init(Consumer<FinishedRecipe> provider) {
        VanillaRecipes.init(provider);
        CommonGTRecipes.init(provider);
        CustomGTRecipes.init(provider);
        BlockRecipes.init(provider);
        SemiconductorRecipes.init(provider);
        MekanismRecipes.init(provider);
        AE2Recipes.init(provider);
        ControllerRecipes.init(provider);
        GTMTRecipes.init(provider);
    }

    public static @Nullable Item getItemById(String modId, String path) {
        Item item = ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath(modId, path));
        if (item == null) {
            SFTCore.LOGGER.warn("Could not find item with path: {}:{}", modId, path);
        }
        return item;
    }
}
