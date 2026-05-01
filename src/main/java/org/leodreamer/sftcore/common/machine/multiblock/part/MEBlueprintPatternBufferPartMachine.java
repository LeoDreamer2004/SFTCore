package org.leodreamer.sftcore.common.machine.multiblock.part;

import appeng.api.crafting.IPatternDetails;
import appeng.api.implementations.blockentities.PatternContainerGroup;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGrid;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.stacks.KeyCounter;
import appeng.helpers.patternprovider.PatternContainer;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.integration.ae2.machine.MEBusPartMachine;
import com.gregtechceu.gtceu.integration.ae2.machine.MEPatternBufferPartMachine.InternalSlot;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;

public class MEBlueprintPatternBufferPartMachine extends MEBusPartMachine implements ICraftingProvider, PatternContainer {

    @Getter
    @Persisted
    @Nullable
    protected InternalSlot[] internalInventory;

    @Nullable
    private BiMap<IPatternDetails, InternalSlot> detailsSlotMap;

    public MEBlueprintPatternBufferPartMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, IO.IN, args);
    }

    @Override
    public void removedFromController(@NotNull IMultiController controller) {
        super.removedFromController(controller);
        internalInventory = null;
        detailsSlotMap = null;
    }

    @Override
    public void addedToController(@NotNull IMultiController controller) {
        super.addedToController(controller);
        if (controller instanceof IRecipeLogicMachine machine) {
            var recipes = new HashSet<GTRecipe>();
            for (var category : GTRegistries.RECIPE_CATEGORIES) {
                recipes.addAll(machine.getRecipeType().getRecipesInCategory(category));
            }
            internalInventory = new InternalSlot[recipes.size()];
            detailsSlotMap = HashBiMap.create(recipes.size());
        }
    }

    @Override
    public List<IPatternDetails> getAvailablePatterns() {
        return List.of();
    }

    @Override
    public boolean pushPattern(IPatternDetails patternDetails, KeyCounter[] inputHolder) {
        return false;
    }

    @Override
    public boolean isBusy() {
        return false;
    }

    @Override
    public @Nullable IGrid getGrid() {
        return null;
    }

    @Override
    public InternalInventory getTerminalPatternInventory() {
        return null;
    }

    @Override
    public PatternContainerGroup getTerminalGroup() {
        return null;
    }
}
