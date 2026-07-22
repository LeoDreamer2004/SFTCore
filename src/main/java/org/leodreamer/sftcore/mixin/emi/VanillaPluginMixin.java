package org.leodreamer.sftcore.mixin.emi;

import org.leodreamer.sftcore.integration.IntegrateMods;
import org.leodreamer.sftcore.util.RLUtils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.TallFlowerBlock;
import net.minecraftforge.registries.ForgeRegistries;

import dev.emi.emi.VanillaPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiWorldInteractionRecipe;
import dev.emi.emi.api.stack.EmiStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(value = VanillaPlugin.class, remap = false)
public abstract class VanillaPluginMixin {
    /**
     * EMI generates a huge amount of synthetic recipes:
     * - emi:/anvil/repairing/material/*
     * - emi:/anvil/repairing/tool/*
     * - emi:/anvil/enchanting/*
     * - emi:/grindstone/repairing/*
     * - emi:/grindstone/disenchanting/tool/*
     * - emi:/grindstone/disenchanting/book/*
     * Canceling here avoids both recipe construction and later recipe bake/search indexing.
     */
    @Inject(method = "addRepair", at = @At("HEAD"), cancellable = true)
    private static void sftcore$skipAnvilGrindstoneEnchantSyntheticRecipes(
        EmiRegistry registry,
        Set<Item> hiddenItems,
        CallbackInfo ci
    ) {
        // addRepair also contains tall flower duplication recipes.
        sftcore$addTallFlowerDuplicationRecipes(registry, hiddenItems);

        ci.cancel();
    }

    @Unique
    private static void sftcore$addTallFlowerDuplicationRecipes(EmiRegistry registry, Set<Item> hiddenItems) {
        for (var item : ForgeRegistries.ITEMS) {
            if (hiddenItems.contains(item)) {
                continue;
            }

            if (!(item instanceof BlockItem blockItem)) {
                continue;
            }

            if (!(blockItem.getBlock() instanceof TallFlowerBlock)) {
                continue;
            }

            var flowerInput = EmiStack.of(blockItem);
            flowerInput.setRemainder(EmiStack.of(blockItem));

            var id = RLUtils.getItemRL(item);
            String subId = id.getNamespace() + "/" + id.getPath();

            registry.addRecipe(
                EmiWorldInteractionRecipe.builder()
                    .id(ResourceLocation.fromNamespaceAndPath(IntegrateMods.EMI, "/world/flower_duping/" + subId))
                    .leftInput(flowerInput)
                    .rightInput(EmiStack.of(Items.BONE_MEAL), false)
                    .output(EmiStack.of(item))
                    .build()
            );
        }
    }
}
