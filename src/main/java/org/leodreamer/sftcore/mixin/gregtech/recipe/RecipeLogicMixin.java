package org.leodreamer.sftcore.mixin.gregtech.recipe;

import org.leodreamer.sftcore.api.feature.IMEPatternBufferCache;
import org.leodreamer.sftcore.api.feature.SlotRestrictedRecipeHolder;
import org.leodreamer.sftcore.common.advancement.SFTCriteriaTriggers;

import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.api.machine.trait.MachineTrait;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.ActionResult;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;

import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Mixin(value = RecipeLogic.class, remap = false)
public abstract class RecipeLogicMixin extends MachineTrait {

    @Shadow
    @Nullable
    public List<GTRecipe> lastFailedMatches;

    @Shadow
    @Nullable
    protected GTRecipe lastRecipe;

    @Shadow
    @Nullable
    protected GTRecipe lastOriginRecipe;

    @Shadow
    protected boolean recipeDirty;

    @Shadow
    @Final
    protected Map<GTRecipe, Component> failureReasonMap;

    @Shadow
    public abstract IRecipeLogicMachine getRLMachine();

    @Shadow
    protected abstract ActionResult checkRecipe(GTRecipe recipe);

    @Shadow
    protected abstract ActionResult matchRecipe(GTRecipe recipe);

    @Shadow
    public abstract void setupRecipe(GTRecipe recipe);

    @Shadow
    public abstract Iterator<GTRecipe> searchRecipe();

    @Shadow
    protected abstract void handleSearchingRecipes(Iterator<GTRecipe> matches);

    @Shadow
    private RecipeLogic.Status status;

    @Inject(
        method = "onRecipeFinish",
        at = @At(
            value = "INVOKE",
            target = "Lcom/gregtechceu/gtceu/api/machine/trait/RecipeLogic;handleRecipeIO(Lcom/gregtechceu/gtceu/api/recipe/GTRecipe;Lcom/gregtechceu/gtceu/api/capability/recipe/IO;)Lcom/gregtechceu/gtceu/api/recipe/ActionResult;",
            shift = At.Shift.AFTER
        )
    )
    private void sftcore$triggerAdvancementWhenRecipeExecuted(CallbackInfo ci) {
        var recipe = this.lastOriginRecipe != null ? this.lastOriginRecipe : this.lastRecipe;

        if (recipe == null || recipe.id == null) {
            return;
        }

        SFTCriteriaTriggers.RECIPE_EXECUTED.trigger(getMachine(), recipe.id);
    }

    /**
     * @author LeoDreamer
     * @reason cache optimization for pattern buffer
     */
    @Overwrite
    public void findAndHandleRecipe() {
        lastFailedMatches = null;

        // Keep the fast lastRecipe
        if (!recipeDirty && lastRecipe != null && checkRecipe(lastRecipe).isSuccess()) {
            var recipe = lastRecipe;
            lastRecipe = null;
            lastOriginRecipe = null;
            setupRecipe(recipe);
            recipeDirty = false;
            return;
        }

        failureReasonMap.clear();
        lastRecipe = null;
        lastOriginRecipe = null;

        var buffers = sftcore$getPatternBuffers();

        if (buffers.isEmpty()) {
            handleSearchingRecipes(searchRecipe());
            recipeDirty = false;
            return;
        }

        // Use our cached pattern
        if (sftcore$tryCachedPatternRecipes(buffers)) {
            recipeDirty = false;
            return;
        }

        sftcore$searchRecipesAndUpdatePatternCache(buffers);

        recipeDirty = false;
    }

    @Unique
    private List<IMEPatternBufferCache> sftcore$getPatternBuffers() {
        var machine = getMachine();

        if (!(machine instanceof MultiblockControllerMachine controller)) {
            return List.of();
        }

        if (!controller.isFormed()) {
            return List.of();
        }

        var result = new ArrayList<IMEPatternBufferCache>();

        for (var part : controller.getParts()) {
            if (part instanceof IMEPatternBufferCache cache) {
                result.add(cache);
            }
        }

        return result;
    }

    @Unique
    private boolean sftcore$tryCachedPatternRecipes(List<IMEPatternBufferCache> caches) {
        for (var cache : caches) {

            for (int slot = 0; slot < cache.sftcore$getSlotCount(); slot++) {
                var cached = cache.sftcore$getCachedRecipe(slot);

                if (cached == null) {
                    continue;
                }

                if (!cache.sftcore$hasInternalContent(slot)) {
                    continue;
                }

                if (sftcore$tryRecipeFromSlot(cache, slot, cached, false)) {
                    return true;
                }
                cache.sftcore$clearCachedRecipe(slot);
            }
        }

        return false;
    }

    @Unique
    private void sftcore$searchRecipesAndUpdatePatternCache(List<IMEPatternBufferCache> caches) {
        var matches = searchRecipe();

        while (matches.hasNext()) {
            var match = matches.next();

            for (var cache : caches) {

                for (int slot = 0; slot < cache.sftcore$getSlotCount(); slot++) {
                    if (!cache.sftcore$hasInternalContent(slot)) {
                        continue;
                    }

                    if (sftcore$tryRecipeFromSlot(cache, slot, match, true)) {
                        return;
                    }
                }
            }

            if (!matchRecipe(match).isSuccess()) {
                continue;
            }

            if (lastFailedMatches == null) {
                lastFailedMatches = new ArrayList<>();
            }

            lastFailedMatches.add(match);
        }
    }

    @Unique
    private boolean sftcore$tryRecipeFromSlot(
        IMEPatternBufferCache cache,
        int slot,
        GTRecipe originRecipe,
        boolean updateCacheOnSuccess
    ) {
        var slotHandler = cache.sftcore$getSlotHandler(slot);
        if (slotHandler == null) {
            return false;
        }

        var modified = getRLMachine().fullModifyRecipe(originRecipe.copy());
        if (modified == null) {
            return false;
        }

        var self = (RecipeLogic) (Object) this;
        var conditionResult = RecipeHelper.checkConditions(modified, self);
        if (!conditionResult.isSuccess()) {
            RecipeLogic.putFailureReason(self, originRecipe, conditionResult.reason());
            return false;
        }

        var restrictedHolder = new SlotRestrictedRecipeHolder(getRLMachine(), slotHandler);
        var matchResult = RecipeHelper.matchContents(restrictedHolder, modified);

        if (!matchResult.isSuccess()) {
            return false;
        }

        setupRecipe(modified);

        if (lastRecipe != null && status == RecipeLogic.Status.WORKING) {
            lastOriginRecipe = originRecipe;

            if (updateCacheOnSuccess) {
                cache.sftcore$setCachedRecipe(slot, originRecipe);
            }

            return true;
        }

        return false;
    }
}
