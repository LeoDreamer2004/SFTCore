package org.leodreamer.sftcore.mixin.gregtech.machine;

import org.leodreamer.sftcore.common.data.SFTItems;
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
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.integration.ae2.machine.MEBusPartMachine;
import com.gregtechceu.gtceu.integration.ae2.machine.MEPatternBufferPartMachine;

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
    implements IPromptProvider, IMemoryCardInteraction, IScaleUpCraftingProvider {

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
        IPatternDetails details, KeyCounter[] inputHolder, CallbackInfoReturnable<Boolean> cir
    ) {
        assert !cir.getReturnValue(); // expected to be false
        MEPatternBufferPartMachine.InternalSlot slot;
        if (details instanceof ScaledProcessingPattern spp) {
            slot = sftcore$wildcardDetailsSlotMap.get(spp.original());
        } else {
            slot = sftcore$wildcardDetailsSlotMap.get(details);
        }
        if (slot != null) {
            slot.pushPattern(details, inputHolder);
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
}
