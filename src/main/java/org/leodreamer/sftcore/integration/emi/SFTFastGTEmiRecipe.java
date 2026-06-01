package org.leodreamer.sftcore.integration.emi;

import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.api.recipe.ingredient.FluidIngredient;
import com.gregtechceu.gtceu.api.recipe.ingredient.IntCircuitIngredient;
import com.gregtechceu.gtceu.api.recipe.ingredient.IntProviderIngredient;
import com.gregtechceu.gtceu.api.recipe.ingredient.SizedIngredient;
import com.gregtechceu.gtceu.integration.xei.widgets.GTRecipeWidget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.capability.templates.EmptyFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.EmptyHandler;

import com.lowdragmc.lowdraglib.emi.ModularEmiRecipe;
import com.lowdragmc.lowdraglib.emi.ModularForegroundRenderWidget;
import com.lowdragmc.lowdraglib.emi.ModularWrapperWidget;
import com.lowdragmc.lowdraglib.gui.ingredient.IRecipeIngredientSlot;
import com.lowdragmc.lowdraglib.gui.widget.DraggableScrollableWidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.jei.IngredientIO;
import com.lowdragmc.lowdraglib.jei.ModularWrapper;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.TankWidget;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.emi.emi.jemi.JemiStack;
import lombok.Getter;
import mekanism.api.chemical.gas.GasStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Faster rendering version for {@link com.gregtechceu.gtceu.integration.emi.recipe.GTEmiRecipe}.
 * Thus, DO NOT use GTEmiRecipe anywhere, as they are replaced by this. Use {@link IGTEmiRecipe} instead.
 */
public final class SFTFastGTEmiRecipe implements EmiRecipe, IGTEmiRecipe {

    @Getter
    private final GTRecipe recipe;
    @Getter
    private final EmiRecipeCategory category;
    @Getter
    private final ResourceLocation id;
    private final int width;
    private final int height;

    @Getter
    private final List<EmiIngredient> inputs = new ArrayList<>();
    @Getter
    private final List<EmiIngredient> catalysts = new ArrayList<>();
    @Getter
    private final List<EmiStack> outputs = new ArrayList<>();

    public SFTFastGTEmiRecipe(GTRecipe recipe, EmiRecipeCategory category) {
        this.recipe = recipe;
        this.category = category;
        this.id = recipe.getId();
        this.width = recipe.recipeType.getRecipeUI().getJEISize().width;
        this.height = recipe.recipeType.getRecipeUI().getJEISize().height;

        readContentMap(recipe.inputs, false);
        readContentMap(recipe.tickInputs, false);
        readContentMap(recipe.outputs, true);
        readContentMap(recipe.tickOutputs, true);
    }

    @Override
    public int getDisplayWidth() {
        return width;
    }

    @Override
    public int getDisplayHeight() {
        return height;
    }

    @Override
    public GTRecipe sftcore$recipe() {
        return recipe;
    }

    private void readContentMap(Map<RecipeCapability<?>, List<Content>> map, boolean output) {
        if (map == null || map.isEmpty()) {
            return;
        }

        for (var entry : map.entrySet()) {
            var cap = entry.getKey();

            // EU/CWU/BlockState/other non-search capabilities should not enter EMI recipe lookup.
            if (cap != ItemRecipeCapability.CAP && cap != FluidRecipeCapability.CAP) {
                continue;
            }

            for (var content : entry.getValue()) {
                var ingredient = toEmiIngredient(cap, content);
                if (ingredient == null || ingredient.isEmpty()) {
                    continue;
                }

                if (output) {
                    var first = ingredient.getEmiStacks().get(0).copy();
                    applyChance(first, content);
                    outputs.add(first);
                } else if (isInputCatalyst(cap, content)) {
                    catalysts.add(ingredient);
                } else {
                    applyChance(ingredient, content);
                    inputs.add(ingredient);
                }
            }
        }
    }

    private static @Nullable EmiIngredient toEmiIngredient(RecipeCapability<?> cap, Content content) {
        if (cap == ItemRecipeCapability.CAP) {
            var ingredient = ItemRecipeCapability.CAP.of(content.content);
            return EmiIngredient.of(ingredient);
        }

        if (cap == FluidRecipeCapability.CAP) {
            var ingredient = FluidRecipeCapability.CAP.of(content.content);
            return toFluidEmiIngredient(ingredient);
        }

        return null;
    }

    private static EmiIngredient toFluidEmiIngredient(FluidIngredient ingredient) {
        if (ingredient == null || ingredient.isEmpty()) {
            return EmiStack.EMPTY;
        }

        var stacks = new ArrayList<EmiIngredient>();
        for (var stack : ingredient.getStacks()) {
            if (stack == null || stack.isEmpty()) {
                continue;
            }

            long amount;
            amount = Math.max(1, stack.getAmount());

            stacks.add(EmiStack.of(stack.getFluid(), stack.getTag(), amount));
        }

        return stacks.isEmpty() ? EmiStack.EMPTY : EmiIngredient.of(stacks);
    }

    private static boolean isInputCatalyst(RecipeCapability<?> cap, Content content) {
        // GTM convention: chance == 0 input means not consumed, shown as catalyst.
        if (content.chance == 0) {
            return true;
        }

        // Programmed circuit should be searchable as catalyst, not as consumed input.
        if (cap == ItemRecipeCapability.CAP) {
            var ingredient = ItemRecipeCapability.CAP.of(content.content);
            return isCircuitIngredient(ingredient);
        }

        return false;
    }

    private static boolean isCircuitIngredient(Ingredient ingredient) {
        if (ingredient instanceof IntCircuitIngredient) {
            return true;
        }

        if (ingredient instanceof SizedIngredient sized) {
            return isCircuitIngredient(sized.getInner());
        }

        if (ingredient instanceof IntProviderIngredient provider) {
            return isCircuitIngredient(provider.getInner());
        }

        return false;
    }

    private static void applyChance(EmiIngredient ingredient, Content content) {
        if (content.chance > 0 && content.chance < content.maxChance) {
            ingredient.setChance((float) content.chance / (float) content.maxChance);
        }
    }

    /**
     * Lazy UI construction.
     * This method is only called when EMI actually renders the recipe page.
     * Do not call {@link GTRecipeWidget} in the constructor.
     */
    @Override
    public void addWidgets(WidgetHolder widgets) {
        var widget = new GTRecipeWidget(recipe);
        var modular = new ModularWrapper<WidgetGroup>(widget);
        modular.setRecipeWidget(0, 0);

        synchronized (ModularEmiRecipe.CACHE_OPENED) {
            ModularEmiRecipe.CACHE_OPENED.add(modular);
        }

        var slots = new ArrayList<Widget>();

        for (var w : getFlatWidgetCollection(widget)) {
            if (!(w instanceof IRecipeIngredientSlot slot)) {
                continue;
            }

            if (w.getParent() instanceof DraggableScrollableWidgetGroup draggable && draggable.isUseScissor()) {
                continue;
            }

            var io = slot.getIngredientIO();
            if (io == null || io == IngredientIO.RENDER_ONLY) {
                continue;
            }

            var wrapped = SFTJemiGasBridge.wrapGasIngredients(slot.getXEIIngredients());

            @SuppressWarnings({ "unchecked", "rawtypes" })
            var ingredients = EmiIngredient.of((List) wrapped);

            SlotWidget slotWidget = null;

            if (slot instanceof com.gregtechceu.gtceu.api.gui.widget.SlotWidget slotW) {
                slotW.setHandlerSlot((IItemHandlerModifiable) EmptyHandler.INSTANCE, 0);
                slotW.setDrawHoverOverlay(false).setDrawHoverTips(false);
            } else if (slot instanceof com.gregtechceu.gtceu.api.gui.widget.TankWidget tankW) {
                tankW.setFluidTank(EmptyFluidHandler.INSTANCE);
                tankW.setDrawHoverOverlay(false).setDrawHoverTips(false);

                long capacity = Math.max(1, ingredients.getAmount());
                slotWidget = new TankWidget(
                    ingredients,
                    w.getPosition().x,
                    w.getPosition().y,
                    w.getSize().width,
                    w.getSize().height,
                    capacity
                );
            }

            if (slotWidget == null) {
                slotWidget = createInvisibleGasSlot(
                    ingredients,
                    w.getPosition().x,
                    w.getPosition().y
                );
            }

            slotWidget
                .customBackground(null, w.getPosition().x, w.getPosition().y, w.getSize().width, w.getSize().height)
                .drawBack(false);

            if (io == IngredientIO.CATALYST) {
                slotWidget.catalyst(true);
            } else if (io == IngredientIO.OUTPUT) {
                slotWidget.recipeContext(this);
            }

            for (var component : w.getTooltipTexts()) {
                slotWidget.appendTooltip(component);
            }

            slots.add(slotWidget);
        }

        widgets.add(new ModularWrapperWidget(modular, slots));
        slots.forEach(widgets::add);
        widgets.add(new ModularForegroundRenderWidget(modular));
    }

    private static List<com.lowdragmc.lowdraglib.gui.widget.Widget> getFlatWidgetCollection(
        com.lowdragmc.lowdraglib.gui.widget.Widget widgetIn
    ) {
        var widgets = new ArrayList<com.lowdragmc.lowdraglib.gui.widget.Widget>();

        if (widgetIn instanceof WidgetGroup group) {
            for (var widget : group.widgets) {
                widgets.add(widget);
                if (widget instanceof WidgetGroup child) {
                    widgets.addAll(child.getContainedWidgets(true));
                }
            }
        } else {
            widgets.add(widgetIn);
        }

        return widgets;
    }

    private SlotWidget createInvisibleGasSlot(
        EmiIngredient stack,
        int x,
        int y
    ) {
        for (var emiStack : stack.getEmiStacks()) {
            if (emiStack instanceof JemiStack<?> jemiStack && jemiStack.ingredient instanceof GasStack) {
                return new SlotWidget(stack, x, y) {

                    @Override
                    public void drawStack(GuiGraphics draw, int mouseX, int mouseY, float delta) {
                        // Do not draw the foreground stack, just the background
                    }
                };
            }
        }

        return new SlotWidget(stack, x, y);
    }
}
