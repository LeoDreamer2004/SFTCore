package org.leodreamer.sftcore.api.recipe.capability;

import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.content.SerializerFloat;

import java.util.Collection;
import java.util.List;

public class StressRecipeCapability extends RecipeCapability<Float> {

    public static final StressRecipeCapability CAP = new StressRecipeCapability();

    protected StressRecipeCapability() {
        super("su", 0xFF77A400, false, 4, SerializerFloat.INSTANCE);
    }

    @Override
    public Float copyInner(Float content) {
        return content;
    }

    @Override
    public Float copyWithModifier(Float content, ContentModifier modifier) {
        return modifier.apply(content);
    }

    @Override
    public List<Object> compressIngredients(Collection<Object> ingredients) {
        return List.of(ingredients.stream().map(Float.class::cast).reduce(0f, Float::sum));
    }
}
