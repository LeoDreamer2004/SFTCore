package org.leodreamer.sftcore.integration.emi;

import dev.emi.emi.api.render.EmiTooltipComponents;
import dev.emi.emi.api.stack.EmiStack;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.client.gui.GuiUtils;
import mekanism.client.render.MekanismRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.leodreamer.sftcore.integration.IntegrateMods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SFTGasEmiStack extends EmiStack {
    private final Gas gas;

    public SFTGasEmiStack(GasStack stack) {
        this(stack.getType(), stack.getAmount());
    }

    public SFTGasEmiStack(Gas gas, long amount) {
        this.gas = gas;
        this.amount = amount;
    }

    public GasStack getGasStack() {
        if (isEmpty()) {
            return GasStack.EMPTY;
        }
        return new GasStack(gas, amount);
    }

    @Override
    public EmiStack copy() {
        var copy = new SFTGasEmiStack(gas, amount);
        copy.setChance(chance);
        copy.setRemainder(getRemainder().copy());
        copy.comparison = comparison;
        return copy;
    }

    @Override
    public boolean isEmpty() {
        return amount <= 0 || gas == null || gas.isEmptyType();
    }

    @Override
    public @Nullable CompoundTag getNbt() {
        return null;
    }

    @Override
    public Object getKey() {
        return gas;
    }

    @Override
    public ResourceLocation getId() {
        ResourceLocation id = MekanismAPI.gasRegistry().getKey(gas);
        return id == null ? ResourceLocation.fromNamespaceAndPath(IntegrateMods.MEK, "empty") : id;
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, float delta, int flags) {
        if ((flags & RENDER_ICON) == 0 || isEmpty()) {
            return;
        }

        GasStack stack = getGasStack();
        var sprite = MekanismRenderer.getChemicalTexture(stack.getType());

        MekanismRenderer.color(graphics, stack);
        GuiUtils.drawTiledSprite(
            graphics,
            x,
            y + 16,
            0,
            16,
            16,
            sprite,
            16,
            16,
            100,
            GuiUtils.TilingDirection.UP_RIGHT,
            true
        );
        MekanismRenderer.resetColor(graphics);
    }

    @Override
    public List<Component> getTooltipText() {
        if (isEmpty()) {
            return Collections.emptyList();
        }

        List<Component> tooltip = new ArrayList<>();
        GasStack stack = getGasStack();
        tooltip.add(stack.getTextComponent());
        return tooltip;
    }

    @Override
    public List<ClientTooltipComponent> getTooltip() {
        List<ClientTooltipComponent> tooltip = new ArrayList<>();

        for (Component component : getTooltipText()) {
            tooltip.add(EmiTooltipComponents.of(component));
        }

        tooltip.addAll(super.getTooltip());
        return tooltip;
    }

    @Override
    public Component getName() {
        return isEmpty() ? Component.empty() : getGasStack().getTextComponent();
    }
}
