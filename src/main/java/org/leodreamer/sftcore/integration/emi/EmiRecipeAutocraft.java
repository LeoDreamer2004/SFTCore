package org.leodreamer.sftcore.integration.emi;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.util.GTMachineUtils;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.integration.emi.recipe.GTEmiRecipe;
import com.gregtechceu.gtceu.utils.GTMath;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.lowdragmc.lowdraglib.gui.modular.ModularUIGuiContainer;
import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.recipe.EmiPlayerInventory;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.bom.BoM;
import dev.emi.emi.registry.EmiRecipeFiller;
import dev.emi.emi.runtime.EmiFavorite;
import dev.emi.emi.runtime.EmiFavorites;
import dev.emi.emi.screen.EmiScreenBase;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid = SFTCore.MOD_ID)
public class EmiRecipeAutocraft {

    private static final Minecraft client = Minecraft.getInstance();

    private static boolean craftingRecipe = false; // global lock while crafting
    private static long craftingStart;

    /**
     * The current machine screen opened by client
     */
    @Nullable
    public static MetaMachine openedMachine = null;

    @SubscribeEvent
    public static void onGTMachineHit(PlayerInteractEvent.RightClickBlock event) {
        var pos = event.getPos();
        var level = event.getLevel();
        if (level.getBlockEntity(pos) instanceof IMachineBlockEntity mbe) {
            openedMachine = GTMachineUtils.thisOrController(mbe.getMetaMachine());
        } else {
            openedMachine = null;
        }
    }

    public enum AutocraftScreen {
        CRAFTING,
        GT,
    }

    @Nullable
    public static EmiFavorite.Synthetic findFocus() {
        if (EmiScreenBase.getCurrent().isEmpty() || !BoM.craftingMode || BoM.tree == null) {
            return null;
        }

        var screen = curScreen();
        for (var syn : EmiFavorites.syntheticFavorites) {
            var recipe = syn.getRecipe();
            if (recipe == null) {
                continue;
            }
            if (
                syn.state == SyntheticFavoritesState.CRAFTABLE ||
                    syn.state == SyntheticFavoritesState.PARTIALLY_CRAFTABLE
            ) {
                if (screen == AutocraftScreen.CRAFTING && recipe instanceof EmiCraftingRecipe) {
                    return syn;
                } else if (
                    screen == AutocraftScreen.GT && openedMachine != null && recipe instanceof GTEmiRecipe gtRecipe
                ) {
                    var recipeType = ((IGTEmiRecipe) gtRecipe).sftcore$recipe().recipeType;
                    if (GTMachineUtils.guessRecipe(openedMachine, recipeType).ok()) {
                        return syn;
                    }
                }
            }
        }
        return null;
    }

    public static void autoFillAvailableRecipes() {
        if (craftingRecipe) return;

        var syn = findFocus();
        if (syn == null) return;

        var recipe = syn.getRecipe();
        if (recipe instanceof EmiCraftingRecipe) {
            // only do auto crafting for crafting table recipes
            craftingRecipe = true;
            boolean fill = EmiRecipeFiller.performFill(
                recipe, EmiApi.getHandledScreen(), EmiCraftContext.Type.CRAFTABLE,
                EmiCraftContext.Destination.INVENTORY, GTMath.saturatedCast(syn.batches)
            );

            if (fill) {
                craftingStart = System.currentTimeMillis();
                Thread thread = new Thread(() -> checkCraftingFinished(recipe, syn.amount));
                thread.start();
            } else {
                craftingRecipe = false;
            }
        } else {
            EmiRecipeFiller.performFill(
                recipe, EmiApi.getHandledScreen(), EmiCraftContext.Type.CRAFTABLE,
                EmiCraftContext.Destination.INVENTORY, GTMath.saturatedCast(syn.batches)
            );
        }
    }

    private static void checkCraftingFinished(EmiRecipe recipe, long amount) {
        long current = System.currentTimeMillis();
        if (current - craftingStart > 500) {
            SFTCore.LOGGER.warn(
                """
                    EMI auto crafting seems to be stuck, aborting...
                    If you see this message frequently, please check if you installed Fast WorkBench. It has capability issue with the autocrafting function.
                    """
            );
            craftingRecipe = false;
            return;
        }

        if (curScreen() == AutocraftScreen.CRAFTING) {
            try {
                Thread.sleep(40); // in case of lagging
            } catch (InterruptedException e) {
                SFTCore.LOGGER.error("Interrupted while checking crafting finished", e);
                craftingRecipe = false;
                return;
            }

            EmiFavorites.updateSynthetic(EmiPlayerInventory.of(client.player));

            for (var syn : EmiFavorites.syntheticFavorites) {
                var r = syn.getRecipe();
                if (r == null) continue;
                if (r.getId() == recipe.getId() && syn.amount == amount) {
                    // still not crafted, maybe the network delay
                    checkCraftingFinished(recipe, amount);
                    return;
                }
            }
            craftingRecipe = false;
            // crafting finished, and try to fill other recipes
            client.tell(EmiRecipeAutocraft::autoFillAvailableRecipes);
        }

        // the player has suddenly closed
        craftingRecipe = false;
    }

    @Nullable
    public static EmiRecipeAutocraft.AutocraftScreen curScreen() {
        if (client.screen instanceof CraftingScreen) {
            return AutocraftScreen.CRAFTING;
        }
        if (client.screen instanceof ModularUIGuiContainer) {
            return AutocraftScreen.GT;
        }
        return null;
    }
}
