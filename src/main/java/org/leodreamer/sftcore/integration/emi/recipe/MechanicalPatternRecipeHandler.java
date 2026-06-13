package org.leodreamer.sftcore.integration.emi.recipe;

import org.leodreamer.sftcore.common.item.mechanical.MechanicalEncapsulationPatternUIProvider;
import org.leodreamer.sftcore.common.item.mechanical.MechanicalPatternEditorWidget;
import org.leodreamer.sftcore.common.item.mechanical.MechanicalEncapsulationPatternLogic;

import com.lowdragmc.lowdraglib.gui.modular.ModularUIContainer;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import dev.emi.emi.api.recipe.EmiPlayerInventory;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.EmiRecipeHandler;

import java.util.List;
import java.util.Optional;

public class MechanicalPatternRecipeHandler implements EmiRecipeHandler<ModularUIContainer> {

    @Override
    public EmiPlayerInventory getInventory(net.minecraft.client.gui.screens.inventory.AbstractContainerScreen<ModularUIContainer> screen) {
        return new EmiPlayerInventory(List.of());
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return getMechanicalRecipeId(recipe).isPresent();
    }

    @Override
    public boolean alwaysDisplaySupport(EmiRecipe recipe) {
        return false;
    }

    @Override
    public boolean canCraft(EmiRecipe recipe, EmiCraftContext<ModularUIContainer> context) {
        return getMechanicalRecipeId(recipe).isPresent() && getEditor(context) != null;
    }

    @Override
    public boolean craft(EmiRecipe recipe, EmiCraftContext<ModularUIContainer> context) {
        var editor = getEditor(context);
        var id = getMechanicalRecipeId(recipe);
        if (editor == null || id.isEmpty()) {
            return false;
        }
        editor.requestAddRecipe(id.get());
        return true;
    }

    private MechanicalPatternEditorWidget getEditor(EmiCraftContext<ModularUIContainer> context) {
        var ui = context.getScreenHandler().getModularUI();
        if (ui == null) {
            return null;
        }
        var widget = ui.getFirstWidgetById(MechanicalEncapsulationPatternUIProvider.EDITOR_WIDGET_ID);
        return widget instanceof MechanicalPatternEditorWidget editor ? editor : null;
    }

    private Optional<ResourceLocation> getMechanicalRecipeId(EmiRecipe recipe) {
        var backingRecipe = recipe.getBackingRecipe();
        if (backingRecipe != null && MechanicalEncapsulationPatternLogic.canEncode(backingRecipe)) {
            return Optional.of(backingRecipe.getId());
        }

        var level = Minecraft.getInstance().level;
        var id = recipe.getId();
        if (id != null && level != null && MechanicalEncapsulationPatternLogic.canEncode(level, id)) {
            return Optional.of(id);
        }
        return Optional.empty();
    }
}
