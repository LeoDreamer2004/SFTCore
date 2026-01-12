package org.leodreamer.sftcore.integration.emi;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.util.GTMachineUtils;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.integration.emi.recipe.GTEmiRecipe;
import com.gregtechceu.gtceu.utils.GTMath;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
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

@Mod.EventBusSubscriber(modid = SFTCore.MOD_ID, value = Dist.CLIENT)
public class EmiRecipeAutocraft {

    private static final Minecraft client = Minecraft.getInstance();
    private static final int MAX_STACK = 64;
    private static final int CHECK_INTERVAL = 3;

    /**
     * global lock while crafting
     */
    private static boolean isCrafting = false;
    /**
     * record of crafting start time, used to check if the crafting is timeout.
     */
    private static long craftingStart;
    /**
     * interval counter for checking crafting finish
     */
    private static int currentInterval = CHECK_INTERVAL;
    /**
     * the (crafting) recipe currently being crafted
     */
    @Nullable
    private static EmiRecipe currentRecipe = null;
    /**
     * the amount of items to craft in total
     */
    private static long currentAmount = 0;
    /**
     * the current machine screen opened by client
     */
    @Nullable
    public static MetaMachine openedMachine = null;

    @SubscribeEvent
    public static void onGTMachineHit(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getLevel().isClientSide) return;

        var pos = event.getPos();
        var level = event.getLevel();
        if (level.getBlockEntity(pos) instanceof IMachineBlockEntity mbe) {
            openedMachine = GTMachineUtils.thisOrController(mbe.getMetaMachine());
        } else {
            openedMachine = null;
        }
    }

    @SubscribeEvent
    public static void onClosingScreen(ScreenEvent.Closing event) {
        var screen = event.getScreen();
        if (screen instanceof CraftingScreen || screen instanceof ModularUIGuiContainer) {
            resetCrafting();
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!isCrafting || client.player == null) return;

        if (currentInterval-- > 0) {
            return;
        }

        currentInterval = CHECK_INTERVAL;
        checkCraftingFinished();
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
        if (isCrafting) return;

        var syn = findFocus();
        if (syn == null) return;

        var recipe = syn.getRecipe();
        if (recipe instanceof EmiCraftingRecipe) {
            // only do auto crafting for crafting table recipes
            isCrafting = true;
            boolean fill = EmiRecipeFiller.performFill(
                recipe, EmiApi.getHandledScreen(), EmiCraftContext.Type.CRAFTABLE,
                EmiCraftContext.Destination.INVENTORY, GTMath.saturatedCast(syn.batches)
            );

            if (fill) {
                craftingStart = System.currentTimeMillis();
                currentRecipe = recipe;
                currentAmount = syn.amount;
            } else {
                isCrafting = false;
            }
        } else {
            int batch = calculateMaxBatches(recipe, GTMath.saturatedCast(syn.batches));
            EmiRecipeFiller.performFill(
                recipe, EmiApi.getHandledScreen(), EmiCraftContext.Type.CRAFTABLE,
                EmiCraftContext.Destination.INVENTORY, batch
            );
        }
    }

    private static void checkCraftingFinished() {
        if (currentRecipe == null) {
            resetCrafting();
            return;
        }

        if (System.currentTimeMillis() - craftingStart > 500) {
            SFTCore.LOGGER.warn(
                """
                    EMI auto crafting seems to be stuck, aborting...
                    If you see this message frequently, please check if you installed Fast WorkBench. It has capability issue with the autocrafting function.
                    """
            );
            resetCrafting();
            return;
        }

        if (curScreen() == AutocraftScreen.CRAFTING) {
            EmiFavorites.updateSynthetic(EmiPlayerInventory.of(client.player));

            for (var syn : EmiFavorites.syntheticFavorites) {
                var r = syn.getRecipe();
                if (r == null) continue;
                if (r.getId() == currentRecipe.getId() && syn.amount == currentAmount) {
                    // still not crafted, maybe the network delay
                    return;
                }
            }
            resetCrafting();
            // crafting finished, and try to fill other recipes
            client.tell(EmiRecipeAutocraft::autoFillAvailableRecipes);
        }

        // the player has suddenly closed
        resetCrafting();
    }

    private static int calculateMaxBatches(EmiRecipe recipe, int requested) {
        if (!(recipe instanceof GTEmiRecipe gtEmiRecipe)) {
            return requested;
        }

        var gtRecipe = ((IGTEmiRecipe) gtEmiRecipe).sftcore$recipe();
        int max = requested;
        for (var input : RecipeHelper.getInputItems(gtRecipe)) {
            int amount = input.getCount();
            if (amount > 0) {
                max = Math.min(max, MAX_STACK / amount);
            }
        }
        return Math.max(1, max);
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

    public static void resetCrafting() {
        openedMachine = null;
        isCrafting = false;
        currentInterval = CHECK_INTERVAL;
        currentRecipe = null;
        currentAmount = 0;
    }
}
