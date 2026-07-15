package org.leodreamer.sftcore.api.gui.gas;

import org.leodreamer.sftcore.api.recipe.capability.GasRecipeCapability;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.recipe.gui.CapabilityContentBuilder;
import com.gregtechceu.gtceu.api.recipe.gui.GTRecipeTypeUILayout;
import com.gregtechceu.gtceu.api.recipe.gui.GTRecipeViewerWidget;
import com.gregtechceu.gtceu.api.recipe.gui.RecipeViewerCapabilityLayoutBuilder;

import brachy.modularui.drawable.GuiTextures;
import brachy.modularui.widgets.SlotGroupWidget;

public final class GasRecipeUI {

    private static final RecipeViewerCapabilityLayoutBuilder LAYOUT = (layout, widget, io) -> {
        int slots = layout.getRecipeType().getMaxSlots(GasRecipeCapability.CAP, io);
        if (slots == 0) {
            return;
        }

        if (slots == 1) {
            var slot = createSlot(layout, io, 0);
            if (io == IO.IN) {
                widget.inputColumn.child(slot);
            } else {
                widget.outputColumn.child(slot);
            }
            return;
        }

        var slotGroupWidget = SlotGroupWidget.builder()
            .matrix(layout.capabilityInfo(GasRecipeCapability.CAP).getRecipeViewerGrid(io))
            .key('s', i -> createSlot(layout, io, i))
            .build()
            .coverChildren(18, 18);

        if (io == IO.IN) {
            widget.inputColumn.child(slotGroupWidget);
        } else {
            widget.outputColumn.child(slotGroupWidget);
        }
    };

    private static final CapabilityContentBuilder CONTENT = (
        widget, content, io, perTick,
        recipeType, recipe, chanceTier, recipeTier
    ) -> {
        if (widget instanceof GasRecipeSlotWidget gasSlot) {
            gasSlot.value(content, io, perTick, recipe);
        }
    };

    private static GasRecipeSlotWidget createSlot(GTRecipeTypeUILayout layout, IO io, int index) {
        return new GasRecipeSlotWidget()
            .background(
                GuiTextures.SLOT_FLUID,
                layout.capabilityInfo(GasRecipeCapability.CAP).getOverlay(io, index)
            )
            .name(GTRecipeViewerWidget.capabilityWidgetName(GasRecipeCapability.CAP, io, index));
    }

    private GasRecipeUI() {}

    public static GTRecipeTypeUILayout.Builder apply(GTRecipeTypeUILayout.Builder builder) {
        return builder
            .setRecipeViewerLayoutCapabilityLayoutBuilder(GasRecipeCapability.CAP, LAYOUT)
            .setCapabilityContentBuilder(GasRecipeCapability.CAP, CONTENT);
    }
}
