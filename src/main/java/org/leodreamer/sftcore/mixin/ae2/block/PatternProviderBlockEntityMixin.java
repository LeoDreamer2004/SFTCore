package org.leodreamer.sftcore.mixin.ae2.block;

import org.leodreamer.sftcore.integration.ae2.feature.IPromptProvider;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import appeng.blockentity.crafting.PatternProviderBlockEntity;
import appeng.blockentity.grid.AENetworkBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PatternProviderBlockEntity.class)
public abstract class PatternProviderBlockEntityMixin extends AENetworkBlockEntity
    implements IPromptProvider {

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
}
