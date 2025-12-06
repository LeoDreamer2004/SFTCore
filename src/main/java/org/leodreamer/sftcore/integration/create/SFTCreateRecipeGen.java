package org.leodreamer.sftcore.integration.create;

import com.blakebr0.extendedcrafting.init.ModItems;
import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.DeployingRecipeGen;
import com.simibubi.create.api.data.recipe.PressingRecipeGen;
import com.simibubi.create.api.data.recipe.SequencedAssemblyRecipeGen;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.kinetics.saw.CuttingRecipe;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.Tags;
import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.common.data.SFTItems;
import org.leodreamer.sftcore.integration.IntegrateMods;

import java.util.List;

@SuppressWarnings("unused")
public class SFTCreateRecipeGen {

    static class Pressing extends PressingRecipeGen {
        public Pressing(PackOutput output) {
            super(output, SFTCore.MOD_ID);
        }

        List<GeneratedRecipe> GT_PLATES = GTCEuAPI.materialManager.getRegisteredMaterials()
                .stream()
                .filter(material -> material.shouldGenerateRecipesFor(TagPrefix.plate) && !ChemicalHelper.get(TagPrefix.ingot, material).isEmpty())
                .map(material -> create(material.getName() + "_plate", b -> b.require(ChemicalHelper.get(TagPrefix.ingot, material).getItem())
                        .output(ChemicalHelper.get(TagPrefix.plate, material)))
                )
                .toList();

        GeneratedRecipe BLACK_IRON = create("black_iron", b -> b
                .require(ModItems.BLACK_IRON_INGOT.get())
                .output(ModItems.BLACK_IRON_SLATE.get()));
    }

    static class Deploying extends DeployingRecipeGen {
        public Deploying(PackOutput output) {
            super(output, SFTCore.MOD_ID);
        }

        GeneratedRecipe FLUX_DUST = create("flux_dust", b -> b
                .require(Items.REDSTONE)
                .require(Items.OBSIDIAN)
                .toolNotConsumed()
                .output(ResourceLocation.fromNamespaceAndPath(IntegrateMods.FLUX, "flux_dust")));
    }

    static class SequencedAssembly extends SequencedAssemblyRecipeGen {
        public SequencedAssembly(PackOutput output) {
            super(output, SFTCore.MOD_ID);
        }

        GeneratedRecipe UU_MATTER = create("uu_matter", b -> b
                .require(Blocks.COBBLESTONE)
                .transitionTo(SFTItems.INCOMPLETE_UU_MATTER)
                .addOutput(SFTItems.UU_MATTER, 1)
                .loops(1)
                .addStep(DeployerApplicationRecipe::new, rb -> rb.require(Blocks.COBBLESTONE))
                .addStep(CuttingRecipe::new, rb -> rb)
                .addStep(PressingRecipe::new, rb -> rb)
                .addStep(FillingRecipe::new, rb -> rb.require(Fluids.LAVA, 100))
        );

        GeneratedRecipe ANOTHER_PRECISION_MECHANISM = create("another_precision_mechanism", b -> b.require(Tags.Items.STORAGE_BLOCKS_GOLD)
                .transitionTo(AllItems.INCOMPLETE_PRECISION_MECHANISM.get())
                .addOutput(new ItemStack(AllItems.PRECISION_MECHANISM.get(), 3), 1)
                .loops(5)
                .addStep(DeployerApplicationRecipe::new, rb -> rb.require(AllBlocks.COGWHEEL.get()))
                .addStep(DeployerApplicationRecipe::new, rb -> rb.require(AllBlocks.LARGE_COGWHEEL.get()))
                .addStep(DeployerApplicationRecipe::new, rb -> rb.require(Tags.Items.INGOTS_IRON)));
    }
}
