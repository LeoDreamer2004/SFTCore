package org.leodreamer.sftcore.integration.emi.recipe;

import org.leodreamer.sftcore.common.item.cepattern.CEPatternEditorWidget;
import org.leodreamer.sftcore.common.item.cepattern.CEPatternUIProvider;
import org.leodreamer.sftcore.common.item.cepattern.CERecipeStep;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;

import com.lowdragmc.lowdraglib.gui.modular.ModularUIContainer;
import dev.emi.emi.api.recipe.EmiPlayerInventory;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.EmiRecipeHandler;

import java.util.List;
import java.util.Optional;

public class CEPatternRecipeHandler implements EmiRecipeHandler<ModularUIContainer> {

    @Override
    public EmiPlayerInventory getInventory(AbstractContainerScreen<ModularUIContainer> screen) {
        return new EmiPlayerInventory(List.of());
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return getCERecipeId(recipe).isPresent();
    }

    @Override
    public boolean alwaysDisplaySupport(EmiRecipe recipe) {
        return false;
    }

    @Override
    public boolean canCraft(EmiRecipe recipe, EmiCraftContext<ModularUIContainer> context) {
        return getCERecipeId(recipe).isPresent() && getEditor(context) != null;
    }

    @Override
    public boolean craft(EmiRecipe recipe, EmiCraftContext<ModularUIContainer> context) {
        var editor = getEditor(context);
        var id = getCERecipeId(recipe);
        if (editor == null || id.isEmpty()) {
            return false;
        }
        editor.requestAddRecipe(id.get());
        return true;
    }

    private CEPatternEditorWidget getEditor(EmiCraftContext<ModularUIContainer> context) {
        var ui = context.getScreenHandler().getModularUI();
        if (ui == null) {
            return null;
        }
        var widget = ui.getFirstWidgetById(CEPatternUIProvider.EDITOR_WIDGET_ID);
        return widget instanceof CEPatternEditorWidget editor ? editor : null;
    }

    private Optional<ResourceLocation> getCERecipeId(EmiRecipe recipe) {
        var backingRecipe = recipe.getBackingRecipe();
        if (backingRecipe != null && CERecipeStep.fromRecipe(backingRecipe).isPresent()) {
            return Optional.of(backingRecipe.getId());
        }
        return Optional.empty();
    }
}
