package org.leodreamer.sftcore.api.recipe.content;

import com.gregtechceu.gtceu.api.recipe.lookup.ingredient.AbstractMapIngredient;

import net.minecraft.resources.ResourceLocation;

import mekanism.api.chemical.gas.Gas;

public final class MapGasStackIngredient extends AbstractMapIngredient {

    private final ResourceLocation gasId;

    public MapGasStackIngredient(Gas gas) {
        this.gasId = gas.getRegistryName();
    }

    @Override
    protected int hash() {
        return gasId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        return obj instanceof MapGasStackIngredient other && gasId.equals(other.gasId);
    }

    @Override
    public String toString() {
        return "MapGasStackIngredient{" + gasId + "}";
    }
}
