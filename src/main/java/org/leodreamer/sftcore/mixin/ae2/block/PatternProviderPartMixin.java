package org.leodreamer.sftcore.mixin.ae2.block;

import appeng.parts.crafting.PatternProviderPart;
import net.minecraft.nbt.CompoundTag;
import org.leodreamer.sftcore.integration.ae2.feature.IPromptProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PatternProviderPart.class)
public abstract class PatternProviderPartMixin implements IPromptProvider {

    @Unique
    private static final String PROMPT_KEY = "prompt";

    @Unique
    private String sftcore$prompt = "";

    @Unique
    @Override
    public String sftcore$getPrompt() {
        return this.sftcore$prompt;
    }

    @Unique
    @Override
    public void sftcore$setPrompt(String prompt) {
        this.sftcore$prompt = prompt == null ? "" : prompt;
    }

    @Inject(method = "readFromNBT", at = @At("TAIL"), remap = false)
    public void savePrompt(CompoundTag data, CallbackInfo ci) {
        if (!this.sftcore$prompt.isEmpty()) {
            data.putString(PROMPT_KEY, this.sftcore$prompt);
        }
    }

    @Inject(method = "writeToNBT", at = @At("TAIL"), remap = false)
    public void loadPrompt(CompoundTag data, CallbackInfo ci) {
        this.sftcore$prompt = data.getString(PROMPT_KEY);
    }
}

