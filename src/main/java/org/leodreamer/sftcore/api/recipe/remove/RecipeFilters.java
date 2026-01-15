package org.leodreamer.sftcore.api.recipe.remove;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

public class RecipeFilters {

    public static final RecipeFilter EMPTY = (rl, id) -> false;

    public static IDFilter id(ResourceLocation id) {
        return new IDFilter(id);
    }

    public static ModFilter mod(String mod) {
        return new ModFilter(mod);
    }

    public static InputFilter input(ItemLike item) {
        return new InputFilter(item);
    }

    public static OutputFilter output(ItemLike item) {
        return new OutputFilter(item);
    }
}
