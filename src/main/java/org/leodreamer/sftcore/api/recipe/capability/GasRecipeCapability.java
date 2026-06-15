package org.leodreamer.sftcore.api.recipe.capability;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.api.gui.GasTankWidget;
import org.leodreamer.sftcore.api.recipe.content.SerializerGasStack;
import org.leodreamer.sftcore.common.machine.trait.gas.DisplayGasHandler;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.ui.GTRecipeTypeUI;

import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.jei.IngredientIO;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@DataGenScanned
public class GasRecipeCapability extends RecipeCapability<GasStack> {

    @RegisterLanguage("gas")
    private static final String NAME = "recipe.capability.gas.name";

    public static final GasRecipeCapability CAP = new GasRecipeCapability();

    protected GasRecipeCapability() {
        super("gas", 0xFF00D7C8, true, 20, SerializerGasStack.INSTANCE);
    }

    @Override
    public GasStack copyInner(GasStack content) {
        return content.copy();
    }

    @Override
    public GasStack copyWithModifier(GasStack content, ContentModifier modifier) {
        if (content.isEmpty()) {
            return GasStack.EMPTY;
        }

        long amount = modifier.apply(content.getAmount());
        return amount <= 0 ? GasStack.EMPTY : new GasStack(content, amount);
    }

    @Override
    public boolean isRecipeSearchFilter() {
        return true;
    }

    @Override
    public boolean shouldBypassDistinct() {
        return false;
    }

    @Override
    public @NotNull List<Object> createXEIContainerContents(List<Content> contents, GTRecipe recipe, IO io) {
        var gases = new ArrayList<>();

        for (var content : contents) {
            var stack = of(content.content);
            gases.add(stack == null || stack.isEmpty() ? GasStack.EMPTY : stack.copy());
        }

        int max = io == IO.IN ? recipe.recipeType.getMaxInputs(this) : recipe.recipeType.getMaxOutputs(this);

        while (gases.size() < max) {
            gases.add(GasStack.EMPTY);
        }

        return gases;
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable Object createXEIContainer(List contents) {
        return new DisplayGasHandler((List<GasStack>) contents);
    }

    @Override
    public @NotNull Widget createWidget() {
        return new GasTankWidget(null, 0, 0, 0, 18, 18)
            .setShowAmount(true)
            .setAllowClickFilled(false)
            .setAllowClickDrained(false);
    }

    @Override
    public @NotNull Class<? extends Widget> getWidgetClass() {
        return GasTankWidget.class;
    }

    @Override
    public void applyWidgetInfo(
        @NotNull Widget widget,
        int index,
        boolean isXEI,
        IO io,
        @Nullable GTRecipeTypeUI.RecipeHolder recipeHolder,
        @NotNull GTRecipeType recipeType,
        @Nullable GTRecipe recipe,
        @Nullable Content content,
        @Nullable Object storage,
        int recipeTier,
        int chanceTier
    ) {
        if (!(widget instanceof GasTankWidget gasWidget)) {
            return;
        }

        if (storage instanceof IGasHandler gasHandler) {
            gasWidget.setGasTank(gasHandler, index);
        }

        gasWidget.setIngredientIO(switch (io) {
            case IN -> IngredientIO.INPUT;
            case OUT -> IngredientIO.OUTPUT;
            default -> IngredientIO.RENDER_ONLY;
        });

        gasWidget.setAllowClickFilled(!isXEI);
        gasWidget.setAllowClickDrained(!isXEI && io.support(IO.IN));
        gasWidget.setShowAmount(true);
        gasWidget.setDrawHoverOverlay(!isXEI);
    }
}
