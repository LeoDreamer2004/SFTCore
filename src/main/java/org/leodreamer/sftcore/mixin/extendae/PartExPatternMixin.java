package org.leodreamer.sftcore.mixin.extendae;

import org.leodreamer.sftcore.integration.ae2.feature.IPromptProvider;

import net.minecraft.nbt.CompoundTag;

import com.glodblock.github.extendedae.common.parts.PartExPatternProvider;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PartExPatternProvider.class)
public class PartExPatternMixin implements IPromptProvider {

    @Unique
    private static final String PROMPT_KEY = "prompt";

    @Unique
    private String sftcore$prompt = "";

    @Unique
    @Override
    public @NotNull String sftcore$getPrompt() {
        return this.sftcore$prompt;
    }

    @Unique
    @Override
    public void sftcore$setPrompt(@NotNull String prompt) {
        this.sftcore$prompt = prompt;
    }

    @Inject(method = "readFromNBT", at = @At("TAIL"), remap = false)
    public void savePrompt(CompoundTag data, CallbackInfo ci) {
        this.sftcore$prompt = data.getString(PROMPT_KEY);
    }

    @Inject(method = "writeToNBT", at = @At("TAIL"), remap = false)
    public void loadPrompt(CompoundTag data, CallbackInfo ci) {
        if (!this.sftcore$prompt.isEmpty()) {
            data.putString(PROMPT_KEY, this.sftcore$prompt);
        }
    }
}
