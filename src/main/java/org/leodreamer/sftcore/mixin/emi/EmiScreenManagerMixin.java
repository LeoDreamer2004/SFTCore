package org.leodreamer.sftcore.mixin.emi;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.integration.emi.SyntheticFavoritesState;

import com.gregtechceu.gtceu.utils.GTMath;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.recipe.EmiPlayerInventory;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.bom.BoM;
import dev.emi.emi.registry.EmiRecipeFiller;
import dev.emi.emi.runtime.EmiFavorites;
import dev.emi.emi.screen.EmiScreenBase;
import dev.emi.emi.screen.EmiScreenManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EmiScreenManager.class)
public class EmiScreenManagerMixin {

    @Shadow(remap = false)
    private static Minecraft client;

    @Unique
    private static boolean sftcore$craftingRecipe = false;

    @Unique
    private static long sftcore$start;

    @Unique
    private static long sftcore$lastCheck;

    @Inject(method = "addWidgets", at = @At("TAIL"), remap = false)
    private static void onWidgetInit(Screen screen, CallbackInfo ci) {
        client.tell(EmiScreenManagerMixin::sftcore$findAvailableAutoRecipes);
    }

    @Unique
    private static void sftcore$findAvailableAutoRecipes() {
        if (sftcore$craftingRecipe || EmiScreenBase.getCurrent().isEmpty()) {
            return;
        }
        if (BoM.craftingMode && BoM.tree != null) {

            for (var syn : EmiFavorites.syntheticFavorites) {
                var recipe = syn.getRecipe();
                if (recipe == null) {
                    continue;
                }
                if (
                    syn.state == SyntheticFavoritesState.CRAFTABLE ||
                        syn.state == SyntheticFavoritesState.PARTIALLY_CRAFTABLE
                ) {
                    sftcore$craftingRecipe = true;
                    boolean fill = EmiRecipeFiller.performFill(
                        recipe, EmiApi.getHandledScreen(), EmiCraftContext.Type.CRAFTABLE,
                        EmiCraftContext.Destination.INVENTORY, GTMath.saturatedCast(syn.batches)
                    );

                    // only do auto craft for crafting table
                    if (fill && (recipe instanceof EmiCraftingRecipe)) {
                        sftcore$start = System.currentTimeMillis();
                        sftcore$lastCheck = System.currentTimeMillis();
                        Thread thread = new Thread(() -> sftcore$checkFinished(recipe, syn.amount));
                        thread.start();
                        return;
                    } else {
                        sftcore$craftingRecipe = false;
                    }
                }
            }
        }
    }

    @Unique
    private static void sftcore$checkFinished(EmiRecipe recipe, long amount) {
        long current = System.currentTimeMillis();
        if (current - sftcore$start > 500) {
            SFTCore.LOGGER.warn("EMI auto crafting seems to be stuck, aborting...");
            sftcore$craftingRecipe = false;
            return;
        }
        if (current - sftcore$lastCheck < 30) {
            // skip
            client.tell(() -> sftcore$checkFinished(recipe, amount));
            return;
        }
        sftcore$lastCheck = current;
        EmiFavorites.updateSynthetic(EmiPlayerInventory.of(client.player));

        for (var syn : EmiFavorites.syntheticFavorites) {
            var r = syn.getRecipe();
            if (r == null) continue;
            if (r.getId() == recipe.getId() && syn.amount == amount) {
                client.tell(() -> sftcore$checkFinished(recipe, amount));
                return;
            }
        }
        sftcore$craftingRecipe = false;
        client.tell(EmiScreenManagerMixin::sftcore$findAvailableAutoRecipes);
    }
}
