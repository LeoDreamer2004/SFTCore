package org.leodreamer.sftcore.mixin.ae2.block;

import org.leodreamer.sftcore.integration.ae2.feature.IPatternClear;
import org.leodreamer.sftcore.integration.ae2.feature.IPromptProvider;
import org.leodreamer.sftcore.integration.ae2.item.MemoryCardUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import appeng.blockentity.crafting.PatternProviderBlockEntity;
import appeng.blockentity.grid.AENetworkBlockEntity;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.util.SettingsFrom;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PatternProviderBlockEntity.class)
public abstract class PatternProviderBlockEntityMixin extends AENetworkBlockEntity
    implements IPromptProvider {

    @Shadow(remap = false)
    @Final
    protected PatternProviderLogic logic;

    @Unique
    private static final String PROMPT_KEY = "prompt";

    @Unique
    private String sftcore$prompt = "";

    protected PatternProviderBlockEntityMixin(
        BlockEntityType<?> blockEntityType,
        BlockPos pos,
        BlockState blockState
    ) {
        super(blockEntityType, pos, blockState);
    }

    @Unique
    @Override
    public @NotNull String sftcore$getPrompt() {
        return this.sftcore$prompt;
    }

    @Unique
    @Override
    public void sftcore$setPrompt(@NotNull String prompt) {
        this.sftcore$prompt = prompt;
        this.setChanged();
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    public void savePrompt(CompoundTag data, CallbackInfo ci) {
        if (!this.sftcore$prompt.isEmpty()) {
            data.putString(PROMPT_KEY, this.sftcore$prompt);
        }
    }

    @Inject(method = "loadTag", at = @At("TAIL"), remap = false)
    public void loadPrompt(CompoundTag data, CallbackInfo ci) {
        this.sftcore$prompt = data.getString(PROMPT_KEY);
    }

    @Inject(method = "exportSettings", at = @At("HEAD"), remap = false, cancellable = true)
    public void cancelCutIfCutting(SettingsFrom mode, CompoundTag output, Player player, CallbackInfo ci) {
        if (player == null || mode != SettingsFrom.MEMORY_CARD) return;

        if (MemoryCardUtils.isCutting(player) == MemoryCardUtils.CuttingResult.DANGER) {
            super.exportSettings(mode, output, player);
            MemoryCardUtils.sendDangerousWarning(player);
            ci.cancel();
        }
    }

    @Inject(method = "exportSettings", at = @At("TAIL"), remap = false)
    public void allowCutPattern(SettingsFrom mode, CompoundTag output, @Nullable Player player, CallbackInfo ci) {
        if (player == null || mode != SettingsFrom.MEMORY_CARD) return;

        if (MemoryCardUtils.isCutting(player) != MemoryCardUtils.CuttingResult.NOT) {
            ((IPatternClear) logic).sftcore$clearPatterns(player);
            MemoryCardUtils.sendCutInfo(player);
        }
    }
}
