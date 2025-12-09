package org.leodreamer.sftcore.common.data.tag;

import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.tterrag.registrate.providers.RegistrateItemTagsProvider;
import mekanism.common.registries.MekanismItems;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;

public class SFTItemTagLoader {
    public static void init(RegistrateItemTagsProvider provider) {
        provider.addTag(Tags.Items.DYES_YELLOW)
                .add(Items.GOLD_INGOT);
        provider.addTag(Tags.Items.DYES_BLACK)
                .add(Items.COAL);
        provider.addTag(Tags.Items.DYES_BLUE)
                .add(Items.LAPIS_LAZULI);
        provider.addTag(Tags.Items.DYES_LIME)
                .add(Items.EMERALD);
        provider.addTag(Tags.Items.DYES_RED)
                .add(Items.REDSTONE);
        provider.addTag(Tags.Items.DYES_ORANGE)
                .add(Items.COPPER_INGOT);
        provider.addTag(Tags.Items.DYES_WHITE)
                .add(Items.QUARTZ);
        provider.addTag(Tags.Items.DYES_LIGHT_BLUE)
                .add(Items.DIAMOND);

        provider.addTag(CustomTags.ULV_CIRCUITS)
                .add(MekanismItems.ADVANCED_CONTROL_CIRCUIT.asItem());
        provider.addTag(CustomTags.LV_CIRCUITS)
                .add(MekanismItems.ELITE_CONTROL_CIRCUIT.asItem());
        provider.addTag(CustomTags.MV_CIRCUITS)
                .add(MekanismItems.ULTIMATE_CONTROL_CIRCUIT.asItem());
    }
}
