package org.leodreamer.sftcore.integration.emi.recipe;

import org.leodreamer.sftcore.common.data.SFTItems;
import org.leodreamer.sftcore.common.item.cepattern.CEPatternUIProvider;
import org.leodreamer.sftcore.common.item.cepattern.CERecipeStep;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;

import brachy.modularui.factory.PlayerInventoryGuiData;
import brachy.modularui.screen.ModularContainerMenu;
import dev.emi.emi.api.recipe.EmiPlayerInventory;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.EmiRecipeHandler;
import dev.emi.emi.screen.RecipeScreen;

import java.util.List;
import java.util.Optional;

public class CEPatternRecipeHandler implements EmiRecipeHandler<ModularContainerMenu> {

    @Override
    public EmiPlayerInventory getInventory(AbstractContainerScreen<ModularContainerMenu> screen) {
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
    public boolean canCraft(EmiRecipe recipe, EmiCraftContext<ModularContainerMenu> context) {
        return getCERecipeId(recipe).isPresent() && isCEPatternUI(context.getScreenHandler());
    }

    @Override
    public boolean craft(EmiRecipe recipe, EmiCraftContext<ModularContainerMenu> context) {
        var id = getCERecipeId(recipe);
        if (id.isEmpty() || !isCEPatternUI(context.getScreenHandler())) {
            return false;
        }

        context.getScreenHandler().getSyncManager().getMainPSM().callSyncedAction(
            CEPatternUIProvider.ADD_RECIPE_ACTION,
            buf -> buf.writeResourceLocation(id.get())
        );
        if (Minecraft.getInstance().screen instanceof RecipeScreen recipeScreen) {
            recipeScreen.onClose();
        }
        return true;
    }

    private static boolean isCEPatternUI(ModularContainerMenu menu) {
        if (!menu.isInitialized() || !(menu.getGuiData() instanceof PlayerInventoryGuiData<?> data)) {
            return false;
        }
        if (!CEPatternUIProvider.PANEL_NAME.equals(menu.getSyncManager().getMainPSM().getPanelName())) {
            return false;
        }
        return data.getUsedItemStack().is(SFTItems.CREATE_ENCAPSULATION_PATTERN.asItem());
    }

    private static Optional<ResourceLocation> getCERecipeId(EmiRecipe recipe) {
        var backingRecipe = recipe.getBackingRecipe();
        if (backingRecipe != null && CERecipeStep.fromRecipe(backingRecipe).isPresent()) {
            return Optional.of(backingRecipe.getId());
        }
        return Optional.empty();
    }
}
