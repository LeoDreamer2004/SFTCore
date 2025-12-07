package org.leodreamer.sftcore.mixin.vanilla;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public final class ChangeVanillaTitle {
    @Shadow
    @Final
    private Window window;

    @Inject(method = "updateTitle()V", at = @At("HEAD"), cancellable = true)
    private void updateTitle(final CallbackInfo info) {
        info.cancel();
        this.window.setTitle("Starter For Technology");
    }
}
