package org.leodreamer.sftcore.integration.ae2.pattern;

import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeHandler;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.machine.trait.RecipeHandlerList;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class SlotRestrictedRecipeHolder implements IRecipeCapabilityHolder {

    private final Map<IO, List<RecipeHandlerList>> capabilitiesProxy = new EnumMap<>(IO.class);

    private final Map<IO, Map<RecipeCapability<?>, List<IRecipeHandler<?>>>> capabilitiesFlat =
        new EnumMap<>(IO.class);

    public SlotRestrictedRecipeHolder(
        IRecipeCapabilityHolder original,
        RecipeHandlerList selectedInputSlot
    ) {
        for (var entry : original.getCapabilitiesProxy().entrySet()) {
            var io = entry.getKey();

            for (var handlerList : entry.getValue()) {
                if (io == IO.IN) {
                    if (handlerList == selectedInputSlot || handlerList.doesCapabilityBypassDistinct()) {
                        addHandlerList(handlerList);
                    }
                } else {
                    addHandlerList(handlerList);
                }
            }
        }

        if (!capabilitiesProxy.getOrDefault(IO.IN, List.of()).contains(selectedInputSlot)) {
            addHandlerList(selectedInputSlot);
        }
    }

    @Override
    public @NotNull Map<IO, List<RecipeHandlerList>> getCapabilitiesProxy() {
        return capabilitiesProxy;
    }

    @Override
    public @NotNull Map<IO, Map<RecipeCapability<?>, List<IRecipeHandler<?>>>> getCapabilitiesFlat() {
        return capabilitiesFlat;
    }

    @Override
    public void addHandlerList(RecipeHandlerList handlerList) {
        if (handlerList == RecipeHandlerList.NO_DATA) {
            return;
        }

        var io = handlerList.getHandlerIO();

        capabilitiesProxy
            .computeIfAbsent(io, ignored -> new ArrayList<>())
            .add(handlerList);

        var entrySet = handlerList.getHandlerMap().entrySet();
        var inner = capabilitiesFlat.computeIfAbsent(
            io,
            ignored -> new Reference2ObjectOpenHashMap<>(entrySet.size())
        );

        for (var entry : entrySet) {
            inner.computeIfAbsent(
                entry.getKey(),
                ignored -> new ArrayList<>(entry.getValue().size())
            ).addAll(entry.getValue());
        }
    }
}
