package org.leodreamer.sftcore.integration.ae2.pattern;

import com.gregtechceu.gtceu.api.machine.trait.RecipeHandlerList;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;

import org.jetbrains.annotations.Nullable;

public interface IMEPatternBufferCache {

    int sftcore$getSlotCount();

    boolean sftcore$hasInternalContent(int slot);

    boolean sftcore$isSlotCached(int slot);

    @Nullable
    GTRecipe sftcore$getCachedRecipe(int slot);

    void sftcore$setCachedRecipe(int slot, GTRecipe recipe);

    void sftcore$clearCachedRecipe(int slot);

    @Nullable
    RecipeHandlerList sftcore$getSlotHandler(int slot);
}
