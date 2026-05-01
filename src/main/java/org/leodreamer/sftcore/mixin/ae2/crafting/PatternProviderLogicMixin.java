package org.leodreamer.sftcore.mixin.ae2.crafting;

import org.leodreamer.sftcore.common.item.wildcard.WildcardPatternLogic;
import org.leodreamer.sftcore.common.item.wildcard.impl.WildcardPatternDecoder;
import org.leodreamer.sftcore.integration.ae2.feature.IPatternClear;
import org.leodreamer.sftcore.integration.ae2.logic.MemoryCardPatternInventoryProxy;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

import appeng.api.crafting.IPatternDetails;
import appeng.api.networking.IManagedGridNode;
import appeng.api.stacks.AEKey;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.util.inv.AppEngInternalInventory;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Set;

@Mixin(value = PatternProviderLogic.class, remap = false)
public abstract class PatternProviderLogicMixin implements IPatternClear {

    @Shadow
    @Final
    private List<IPatternDetails> patterns;

    @Shadow
    @Final
    private Set<AEKey> patternInputs;

    @Shadow
    @Final
    private AppEngInternalInventory patternInventory;

    @Shadow
    @Final
    private PatternProviderLogicHost host;

    @Shadow
    @Final
    private IManagedGridNode mainNode;

    @Shadow
    protected abstract void clearPatternInventory(Player player);

    @Inject(
        method = "updatePatterns",
        at = @At(value = "INVOKE", target = "Ljava/util/Set;clear()V", shift = At.Shift.AFTER)
    )
    void decodeWildcardPattern(CallbackInfo ci) {
        var level = host.getBlockEntity().getLevel();
        for (var stack : patternInventory) {
            if (WildcardPatternDecoder.INSTANCE.isEncodedPattern(stack)) {
                WildcardPatternLogic.decodePatterns(stack, level)
                    .forEach(this::sftcore$updatePattern);
            }
        }
    }

    @Unique
    private void sftcore$updatePattern(IPatternDetails details) {
        if (details != null) {
            patterns.add(details);
            for (var input : details.getInputs()) {
                for (var inputCandidate : input.getPossibleInputs()) {
                    patternInputs.add(inputCandidate.what().dropSecondary());
                }
            }
        }
    }

    /**
     * @author LeoDreamer
     * @reason Use the safe proxy
     */
    @Overwrite
    public void exportSettings(CompoundTag output) {
        new MemoryCardPatternInventoryProxy(patternInventory, host.getBlockEntity().getLevel()).exportSettings(output);
    }

    @Unique
    @Override
    public void sftcore$clearPatterns(Player player) {
        if (player != null) {
            clearPatternInventory(player);
        }
    }

    // No need to overwrite importSettings here as it is totally same.
}
