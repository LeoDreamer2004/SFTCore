package org.leodreamer.sftcore.mixin.gregtech.machine;

import org.leodreamer.sftcore.api.feature.IMEPatternBufferCache;
import org.leodreamer.sftcore.integration.ae2.feature.HackyContainerGroupProxy;
import org.leodreamer.sftcore.integration.ae2.feature.IMemoryCardInteraction;
import org.leodreamer.sftcore.integration.ae2.feature.IPromptProvider;
import org.leodreamer.sftcore.integration.ae2.feature.IScaleUpCraftingProvider;
import org.leodreamer.sftcore.integration.ae2.item.MemoryCardUtils;
import org.leodreamer.sftcore.integration.ae2.logic.MemoryCardPatternInventoryProxy;
import org.leodreamer.sftcore.integration.ae2.logic.ScaledProcessingPattern;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.trait.notifiable.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.machine.trait.recipe.RecipeHandlerList;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.sync_system.annotations.SaveField;
import com.gregtechceu.gtceu.api.sync_system.annotations.SyncToClient;
import com.gregtechceu.gtceu.integration.ae2.machine.MEBusPartMachine;
import com.gregtechceu.gtceu.integration.ae2.machine.MEPatternBufferPartMachine;
import com.gregtechceu.gtceu.integration.ae2.machine.trait.InternalSlotRecipeHandler;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import appeng.api.implementations.blockentities.PatternContainerGroup;
import appeng.api.inventories.InternalInventory;
import appeng.core.definitions.AEBlocks;
import com.google.common.collect.BiMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

@Mixin(value = MEPatternBufferPartMachine.class, remap = false)
public abstract class MEPatternBufferPartMachineMixin extends MEBusPartMachine
    implements IPromptProvider, IMemoryCardInteraction, IScaleUpCraftingProvider, IMEPatternBufferCache {

    // custom name now acts as the prompt, instead of the name shown in the pattern group!
    @Shadow
    private String customName;

    @Shadow
    @Final
    protected MEPatternBufferPartMachine.InternalSlot[] internalInventory;

    @Shadow
    @Final
    private InternalInventory internalPatternInventory;

    @Unique
    private final GTRecipe[] sftcore$cachedRecipes = new GTRecipe[27];

    @Unique
    @SaveField
    private final String[] sftcore$cachedRecipeIds = sftcore$emptyCachedRecipeIds(); // SaveField does not support null

    @Unique
    @SyncToClient
    // 32-bit 0-1 mask
    private int sftcore$cachedRecipeMask = 0;

    @Shadow
    public abstract InternalSlotRecipeHandler getInternalRecipeHandler();

    public MEPatternBufferPartMachineMixin(BlockEntityCreationInfo info, IO io) {
        super(info, io, new NotifiableItemStackHandler(0, IO.NONE));
    }

    /* --- Prompt Support --- */

    @Override
    @Unique
    public @NotNull String sftcore$getPrompt() {
        return customName;
    }

    @Override
    @Unique
    public void sftcore$setPrompt(@NotNull String prompt) {
        customName = prompt;
    }

    @Redirect(
        method = "getTerminalGroup",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/String;isEmpty()Z"
        )
    )
    private boolean sftcore$passCustomName(String instance) {
        return true;
    }

    @Inject(
        method = "getTerminalGroup",
        at = @At("RETURN"),
        cancellable = true
    )
    private void sftcore$recordThePosition(CallbackInfoReturnable<PatternContainerGroup> cir) {
        if (!isFormed()) {
            return;
        }
        var pos = getControllers().first().getBlockPos();
        var group = cir.getReturnValue();
        group = HackyContainerGroupProxy.of(group).setBlockPos(pos)
            .recordPartFrom(Component.translatable(getDefinition().getDescriptionId()))
            .get();
        cir.setReturnValue(group);
    }

    @Redirect(
        method = "pushPattern",
        at = @At(
            value = "INVOKE",
            target = "Lcom/google/common/collect/BiMap;containsKey(Ljava/lang/Object;)Z"
        )
    )
    private boolean sftcore$containsKeyForScaleUpPattern(BiMap<?, ?> map, Object details) {
        if (details instanceof ScaledProcessingPattern spp) {
            return map.containsKey(spp.original());
        }
        return map.containsKey(details);
    }

    @Redirect(
        method = "pushPattern",
        at = @At(
            value = "INVOKE", target = "Lcom/google/common/collect/BiMap;get(Ljava/lang/Object;)Ljava/lang/Object;"
        )
    )
    private Object sftcore$checkSlotForScaleUpPattern(BiMap<?, ?> map, Object details) {
        if (details instanceof ScaledProcessingPattern spp) {
            return map.get(spp.original());
        }
        return map.get(details);
    }

    /* --- memory card integration --- */

    @Override
    public String sftcore$memoryId() {
        // cheat the memory card, as if this is a pattern provider block to export self settings to pattern providers
        return AEBlocks.PATTERN_PROVIDER.block().getDescriptionId();
    }

    @Unique
    @Override
    public void sftcore$exportSettings(CompoundTag output, @Nullable Player player) {
        new MemoryCardPatternInventoryProxy(internalPatternInventory, getLevel()).exportSettings(output);

        if (player == null) {

            return;

        }
        if (MemoryCardUtils.isCutting(player) != MemoryCardUtils.CuttingResult.NOT) {
            new MemoryCardPatternInventoryProxy(internalPatternInventory, getLevel()).clearPatterns(player);
            MemoryCardUtils.sendCutInfo(player);
        }
    }

    @Unique
    @Override
    public void sftcore$importSettings(CompoundTag input, @Nullable Player player) {
        new MemoryCardPatternInventoryProxy(internalPatternInventory, getLevel()).importSettings(input, player);
    }

    /* --- cache optimization --- */

    @Inject(method = "onLoad", at = @At("TAIL"))
    private void sftcore$loadCachedRecipes(CallbackInfo ci) {
        if (isRemote()) {
            return;
        }

        sftcore$cachedRecipeMask = 0;

        for (int slot = 0; slot < sftcore$cachedRecipeIds.length; slot++) {
            var recipeId = sftcore$cachedRecipeIds[slot];
            if (recipeId == null || recipeId.isEmpty()) {
                sftcore$cachedRecipes[slot] = null;
                sftcore$cachedRecipeIds[slot] = "";
                continue;
            }

            var id = ResourceLocation.tryParse(recipeId);
            var recipe = id == null ? null : getLevel().getRecipeManager().byKey(id).orElse(null);
            if (recipe instanceof GTRecipe gtRecipe) {
                sftcore$cachedRecipes[slot] = gtRecipe;
                sftcore$cachedRecipeMask |= 1 << slot;
            } else {
                sftcore$cachedRecipes[slot] = null;
                sftcore$cachedRecipeIds[slot] = "";
            }
        }

        sftcore$syncCachedRecipeMask();
    }

    @Inject(method = "onPatternChange", at = @At("HEAD"))
    private void sftcore$clearCacheOnPatternChange(int index, CallbackInfo ci) {
        sftcore$clearCachedRecipe(index);
    }

    @Override
    public int sftcore$getSlotCount() {
        return internalInventory.length;
    }

    @Override
    public boolean sftcore$hasInternalContent(int slot) {
        if (slot < 0 || slot >= internalInventory.length) {
            return false;
        }

        var internalSlot = internalInventory[slot];
        return !internalSlot.isItemEmpty() || !internalSlot.isFluidEmpty();
    }

    @Override
    public @Nullable GTRecipe sftcore$getCachedRecipe(int slot) {
        if (slot < 0 || slot >= sftcore$cachedRecipes.length) {
            return null;
        }

        return sftcore$cachedRecipes[slot];
    }

    @Override
    public void sftcore$setCachedRecipe(int slot, @NotNull GTRecipe recipe) {
        if (slot < 0 || slot >= sftcore$cachedRecipes.length) {
            return;
        }

        sftcore$cachedRecipes[slot] = recipe;
        sftcore$cachedRecipeIds[slot] = recipe.getId().toString();

        int oldMask = sftcore$cachedRecipeMask;
        sftcore$cachedRecipeMask |= 1 << slot;

        if (oldMask != sftcore$cachedRecipeMask) {
            sftcore$syncCachedRecipeMask();
        }
        setChanged();
    }

    @Override
    public void sftcore$clearCachedRecipe(int slot) {
        if (slot < 0 || slot >= sftcore$cachedRecipes.length) {
            return;
        }

        sftcore$cachedRecipes[slot] = null;
        sftcore$cachedRecipeIds[slot] = "";

        int oldMask = sftcore$cachedRecipeMask;
        sftcore$cachedRecipeMask &= ~(1 << slot);

        if (oldMask != sftcore$cachedRecipeMask) {
            sftcore$syncCachedRecipeMask();
        }
        setChanged();
    }

    @Override
    public @Nullable RecipeHandlerList sftcore$getSlotHandler(int slot) {
        if (slot < 0 || slot >= sftcore$getSlotCount()) {
            return null;
        }

        var handlers = getInternalRecipeHandler().getSlotHandlers();
        if (slot >= handlers.size()) {
            return null;
        }

        return handlers.get(slot);
    }

    @Unique
    private void sftcore$syncCachedRecipeMask() {
        getSyncDataHolder().markClientSyncFieldDirty("sftcore$cachedRecipeMask");
    }

    @Unique
    private static String[] sftcore$emptyCachedRecipeIds() {
        var ids = new String[27];
        Arrays.fill(ids, "");
        return ids;
    }
}
