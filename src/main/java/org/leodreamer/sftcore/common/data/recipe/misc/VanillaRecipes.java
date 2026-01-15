package org.leodreamer.sftcore.common.data.recipe.misc;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.common.data.SFTBlocks;
import org.leodreamer.sftcore.common.data.SFTItems;
import org.leodreamer.sftcore.common.data.recipe.utils.SFTVanillaRecipeHelper;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import appeng.core.definitions.AEItems;
import com.simibubi.create.AllItems;
import mekanism.common.registries.MekanismItems;
import mekanism.common.resource.PrimaryResource;
import mekanism.common.resource.ResourceType;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.GTValues.LV;
import static com.gregtechceu.gtceu.api.GTValues.VA;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Lava;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.CHEMICAL_BATH_RECIPES;

public final class VanillaRecipes {

    public static void init(Consumer<FinishedRecipe> provider) {
        vanillaRecipes(provider);
        SFTRecipes(provider);
    }

    private static void vanillaRecipes(Consumer<FinishedRecipe> provider) {
        SFTVanillaRecipeHelper.addShapedRecipe("nether_star")
            .pattern("HHH", "SSS", " S ")
            .arg('H', Items.WITHER_SKELETON_SKULL)
            .arg('S', Items.SOUL_SAND)
            .output(Items.NETHER_STAR)
            .save(provider);

        CHEMICAL_BATH_RECIPES.recipeBuilder(SFTCore.id("soul_sand_gt"))
            .outputItems(Items.SOUL_SAND)
            .inputItems(Items.SAND)
            .inputFluids(Lava.getFluid(50))
            .duration(20)
            .EUt(VA[LV])
            .save(provider);
    }

    private static void SFTRecipes(Consumer<FinishedRecipe> provider) {
        uuMatterRecipes(provider);
        SFTVanillaRecipeHelper.addShapedRecipe("void_portal")
            .pattern("GGG", "GDG", "GGG")
            .arg('G', Items.GOLD_BLOCK)
            .arg('D', Items.DIAMOND_BLOCK)
            .output(SFTBlocks.VOID_PORTAL)
            .save(provider);
    }

    @SuppressWarnings("ConstantConditions")
    private static void uuMatterRecipes(Consumer<FinishedRecipe> provider) {
        uu(provider, Items.COAL, 2, "   ", " U ", "   ");
        uu(provider, Items.IRON_INGOT, 3, " U ", " U ", " U ");
        uu(provider, Items.GOLD_INGOT, 3, "   ", "   ", "UUU");
        uu(provider, Items.COPPER_INGOT, 6, "  U", "   ", "U  ");
        uu(provider, Items.REDSTONE, 12, "   ", "U U", "   ");
        uu(provider, Items.GLOWSTONE_DUST, 4, "U  ", "  U", "   ");
        uu(provider, Items.LAPIS_LAZULI, 16, " U ", "U U", " U ");
        uu(provider, Items.QUARTZ, 8, "  U", "  U", "U  ");
        uu(provider, Items.EMERALD, 2, "UUU", "   ", "UUU");
        uu(provider, Items.DIAMOND, 1, "U U", " U ", "U U");
        uu(provider, Items.NETHERITE_SCRAP, 1, "UUU", "U U", "UUU");

        uu(provider, AllItems.ZINC_INGOT.asItem(), 3, "U  ", " U ", "  U");

        var mek = MekanismItems.PROCESSED_RESOURCES;
        uu(
            provider,
            mek.get(ResourceType.INGOT, PrimaryResource.OSMIUM).asItem(),
            3,
            " U ",
            "  U",
            "U  "
        );
        uu(
            provider,
            mek.get(ResourceType.INGOT, PrimaryResource.LEAD).asItem(),
            3,
            "   ",
            " U ",
            "U U"
        );
        uu(provider, mek.get(ResourceType.INGOT, PrimaryResource.TIN).asItem(), 4, "   ", "U U", " U ");
        uu(
            provider,
            mek.get(ResourceType.INGOT, PrimaryResource.URANIUM).asItem(),
            10,
            " U ",
            "UUU",
            "   "
        );
        uu(provider, MekanismItems.FLUORITE_GEM.asItem(), 8, "   ", "UUU", " U ");

        uu(provider, AEItems.SKY_DUST.asItem(), 6, "  U", " U ", "  U");
    }

    private static void uu(Consumer<FinishedRecipe> provider, Item output, int amount, String... pattern) {
        var name = output.getDescriptionId().replace('.', '_');
        SFTVanillaRecipeHelper.addShapedRecipe("uu/" + name)
            .pattern(pattern)
            .arg('U', SFTItems.UU_MATTER)
            .output(output, amount)
            .save(provider);
    }
}
