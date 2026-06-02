package org.leodreamer.sftcore.mixin.gregtech.machine;

import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.leodreamer.sftcore.api.feature.IMEPatternBufferCache;
import org.leodreamer.sftcore.common.data.SFTItems;
import org.leodreamer.sftcore.common.data.lang.MixinTooltips;
import org.leodreamer.sftcore.common.item.wildcard.WildcardPatternLogic;
import org.leodreamer.sftcore.common.item.wildcard.impl.WildcardPatternDecoder;
import org.leodreamer.sftcore.integration.ae2.feature.HackyContainerGroupProxy;
import org.leodreamer.sftcore.integration.ae2.feature.IMemoryCardInteraction;
import org.leodreamer.sftcore.integration.ae2.feature.IPromptProvider;
import org.leodreamer.sftcore.integration.ae2.feature.IScaleUpCraftingProvider;
import org.leodreamer.sftcore.integration.ae2.item.MemoryCardUtils;
import org.leodreamer.sftcore.integration.ae2.logic.MemoryCardPatternInventoryProxy;
import org.leodreamer.sftcore.integration.ae2.logic.ScaledProcessingPattern;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.trait.RecipeHandlerList;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.sync_system.annotations.SyncToClient;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.integration.ae2.gui.widget.slot.AEPatternViewSlotWidget;
import com.gregtechceu.gtceu.integration.ae2.machine.MEBusPartMachine;
import com.gregtechceu.gtceu.integration.ae2.machine.MEPatternBufferPartMachine;
import com.gregtechceu.gtceu.integration.ae2.machine.trait.InternalSlotRecipeHandler;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import appeng.api.crafting.IPatternDetails;
import appeng.api.implementations.blockentities.PatternContainerGroup;
import appeng.api.inventories.InternalInventory;
import appeng.api.stacks.KeyCounter;
import appeng.core.definitions.AEBlocks;
import appeng.crafting.pattern.ProcessingPatternItem;
import com.google.common.collect.BiMap;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(value = MEPatternBufferPartMachine.class, remap = false)
public abstract class MEPatternBufferPartMachineMixin extends MEBusPartMachine
    implements IPromptProvider, IMemoryCardInteraction, IScaleUpCraftingProvider, IMEPatternBufferCache {

    // custom name now acts as the prompt, instead of the name shown in the pattern group!
    @Shadow
    private String customName;

    @Shadow
    @Final
    private CustomItemStackHandler patternInventory;

    @Shadow
    @Final
    private BiMap<IPatternDetails, MEPatternBufferPartMachine.InternalSlot> detailsSlotMap;

    @Shadow
    @Final
    protected MEPatternBufferPartMachine.InternalSlot[] internalInventory;

    // Cannot use BiMap here, as for wildcard patterns, multiple patterns may map to the same slot
    @Unique
    private final Map<IPatternDetails, MEPatternBufferPartMachine.InternalSlot> sftcore$wildcardDetailsSlotMap = new ConcurrentHashMap<>();

    @Shadow
    private boolean needPatternSync;

    @Shadow
    @Final
    private InternalInventory internalPatternInventory;

    @Unique
    private final GTRecipe[] sftcore$cachedRecipes = new GTRecipe[27];

    @Unique
    @SyncToClient
    // 32-bit 0-1 mask
    private int sftcore$cachedRecipeMask = 0;

    @Shadow
    public abstract InternalSlotRecipeHandler getInternalRecipeHandler();

    public MEPatternBufferPartMachineMixin(BlockEntityCreationInfo info, IO io) {
        super(info, io);
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
    private void recordThePosition(CallbackInfoReturnable<PatternContainerGroup> cir) {
        if (!isFormed()) return;
        var pos = getControllers().first().getBlockPos();
        var group = cir.getReturnValue();
        group = HackyContainerGroupProxy.of(group).setBlockPos(pos)
            .recordPartFrom(Component.translatable(getDefinition().getDescriptionId()))
            // the circuit has been set in the name in the original implementation
            // .setCircuit()
            .get();
        cir.setReturnValue(group);
    }

    /* --- Wildcard pattern integration --- */

    @Inject(method = "<init>", at = @At("TAIL"))
    private void allowWildcardPattern(BlockEntityCreationInfo info, CallbackInfo ci) {
        patternInventory.setFilter(
            (stack) -> stack.getItem() instanceof ProcessingPatternItem || stack.is(SFTItems.WILDCARD_PATTERN.asItem())
        );
    }

    @Inject(
        method = "onLoad()V",
        at = @At(
            value = "INVOKE", target = "Lcom/gregtechceu/gtceu/integration/ae2/machine/MEBusPartMachine;onLoad()V",
            shift = At.Shift.AFTER
        )
    )
    private void changeTickTaskForWildcard(CallbackInfo ci) {
        var level = getLevel();
        for (int i = 0; i < patternInventory.getSlots(); i++) {
            var stack = patternInventory.getStackInSlot(i);
            if (WildcardPatternDecoder.INSTANCE.isEncodedPattern(stack)) {
                int finalI = i;
                WildcardPatternLogic.decodePatterns(stack, level).forEach(
                    detail -> sftcore$wildcardDetailsSlotMap.put(detail, internalInventory[finalI])
                );
            }
        }
    }

    @Inject(method = "onPatternChange", at = @At("HEAD"), cancellable = true)
    private void detectWildcardPattern(int index, CallbackInfo ci) {
        if (isRemote()) return;

        if (index < 0 || index >= internalInventory.length) {
            ci.cancel();
            return;
        }

        var slot = internalInventory[index];
        var newPattern = patternInventory.getStackInSlot(index);

        // remove the original patterns
        boolean flag = detailsSlotMap.inverse().remove(slot) != null;
        var iterator = sftcore$wildcardDetailsSlotMap.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            if (entry.getValue() == slot) {
                iterator.remove();
                flag = true;
            }
        }
        if (flag) {
            // some patterns are here before, refund the slot
            slot.refund();
        }

        if (newPattern.isEmpty()) {
            needPatternSync = true;
            ci.cancel();
            return;
        }

        if (newPattern.is(SFTItems.WILDCARD_PATTERN.asItem())) {
            var level = getLevel();
            var parser = WildcardPatternLogic.on(newPattern);
            parser.generateAllPatterns(level).forEach(
                detail -> sftcore$wildcardDetailsSlotMap.put(detail, slot)
            );
            needPatternSync = true;
            ci.cancel();
        }
        // for general patterns, use the original logic
    }


    @Inject(method = "getAvailablePatterns", at = @At("RETURN"), cancellable = true)
    private void addWildcardPatterns(CallbackInfoReturnable<List<IPatternDetails>> cir) {
        var cur = new ArrayList<>(cir.getReturnValue()); // change to mutable list
        cur.addAll(sftcore$wildcardDetailsSlotMap.keySet());
        cir.setReturnValue(cur);
    }

    @Redirect(
        method = "pushPattern",
        at = @At(
            value = "INVOKE",
            target = "Lcom/google/common/collect/BiMap;containsKey(Ljava/lang/Object;)Z"
        )
    )
    private boolean skipSlotCheck(BiMap<?, ?> map, Object detail) {
        return true;
    }

    @Redirect(
        method = "pushPattern",
        at = @At(
            value = "INVOKE", target = "Lcom/google/common/collect/BiMap;get(Ljava/lang/Object;)Ljava/lang/Object;"
        )
    )
    private Object checkSlotForScaleUpPattern(BiMap<?, ?> map, Object details) {
        if (details instanceof ScaledProcessingPattern spp) {
            return map.get(spp.original());
        }
        return map.get(details);
    }

    @Inject(method = "pushPattern", at = @At(value = "RETURN", ordinal = 2), cancellable = true)
    private void pushWildcardPattern(
        IPatternDetails patternDetails, KeyCounter[] inputHolder, CallbackInfoReturnable<Boolean> cir
    ) {
        assert !cir.getReturnValue(); // expected to be false
        MEPatternBufferPartMachine.InternalSlot slot;
        if (patternDetails instanceof ScaledProcessingPattern spp) {
            slot = sftcore$wildcardDetailsSlotMap.get(spp.original());
        } else {
            slot = sftcore$wildcardDetailsSlotMap.get(patternDetails);
        }
        if (slot != null) {
            slot.pushPattern(patternDetails, inputHolder);
            cir.setReturnValue(true);
        }
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

        if (player == null) return;
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

    @Redirect(
        method = "createUIWidget",
        at = @At(
            value = "NEW",
            target = "(Lnet/minecraftforge/items/IItemHandlerModifiable;III)Lcom/gregtechceu/gtceu/integration/ae2/gui/widget/slot/AEPatternViewSlotWidget;"
        )
    )
    private AEPatternViewSlotWidget sftcore$clearCacheOnClicked(
        IItemHandlerModifiable itemHandler,
        final int slotIndex,
        int xPosition,
        int yPosition
    ) {
        return new AEPatternViewSlotWidget(itemHandler, slotIndex, xPosition, yPosition) {
            @Override
            public ItemStack slotClick(int dragType, ClickType clickTypeIn, Player player) {
                if (!player.level().isClientSide) {
                    sftcore$clearCachedRecipe(slotIndex);
                }
                return null;
            }
        };
    }


    @Inject(method = "createUIWidget", at = @At("RETURN"))
    private void sftcore$addCachedRecipeTooltip(CallbackInfoReturnable<Widget> cir) {
        if (!(cir.getReturnValue() instanceof WidgetGroup group)) {
            return;
        }

        int index = 0;

        for (var widget : group.widgets) {
            if (!(widget instanceof AEPatternViewSlotWidget slot)) {
                continue;
            }

            final int slotIndex = index++;

            slot.setOnAddedTooltips((ignored, tooltips) -> {
                if (sftcore$isSlotCached(slotIndex)) {
                    tooltips.add(
                        Component.translatable(MixinTooltips.PATTERN_CACHED)
                            .withStyle(ChatFormatting.GREEN)
                    );
                }
            });

            if (index >= sftcore$getSlotCount()) {
                break;
            }
        }
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
    public boolean sftcore$isSlotCached(int slot) {
        if (slot < 0 || slot >= sftcore$cachedRecipes.length) {
            return false;
        }

        return (sftcore$cachedRecipeMask & (1 << slot)) != 0;
    }

    @Override
    public @Nullable GTRecipe sftcore$getCachedRecipe(int slot) {
        if (slot < 0 || slot >= sftcore$cachedRecipes.length) {
            return null;
        }

        return sftcore$cachedRecipes[slot];
    }

    @Override
    public void sftcore$setCachedRecipe(int slot, GTRecipe recipe) {
        if (slot < 0 || slot >= sftcore$cachedRecipes.length || recipe == null) {
            return;
        }

        sftcore$cachedRecipes[slot] = recipe;

        int oldMask = sftcore$cachedRecipeMask;
        sftcore$cachedRecipeMask |= 1 << slot;

        if (oldMask != sftcore$cachedRecipeMask) {
            sftcore$syncCachedRecipeMask();
        }
    }

    @Override
    public void sftcore$clearCachedRecipe(int slot) {
        if (slot < 0 || slot >= sftcore$cachedRecipes.length) {
            return;
        }

        sftcore$cachedRecipes[slot] = null;

        int oldMask = sftcore$cachedRecipeMask;
        sftcore$cachedRecipeMask &= ~(1 << slot);

        if (oldMask != sftcore$cachedRecipeMask) {
            sftcore$syncCachedRecipeMask();
        }
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
}
