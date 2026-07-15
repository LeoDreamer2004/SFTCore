package org.leodreamer.sftcore.api.feature;

import com.gregtechceu.gtceu.api.machine.trait.recipe.RecipeHandlerList;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IMEPatternBufferCache {

    int sftcore$getSlotCount();

    boolean sftcore$hasInternalContent(int slot);

    @Nullable
    GTRecipe sftcore$getCachedRecipe(int slot);

    void sftcore$setCachedRecipe(int slot, @NotNull GTRecipe recipe);

    void sftcore$clearCachedRecipe(int slot);

    @Nullable
    RecipeHandlerList sftcore$getSlotHandler(int slot);
}
