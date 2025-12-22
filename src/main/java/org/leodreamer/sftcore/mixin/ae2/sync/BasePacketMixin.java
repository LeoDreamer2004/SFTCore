package org.leodreamer.sftcore.mixin.ae2.sync;

import appeng.core.sync.BasePacket;
import org.leodreamer.sftcore.integration.ae2.sync.SFTPacketHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BasePacket.class)
public class BasePacketMixin {
    @Inject(method = "getPacketID", at = @At("HEAD"), cancellable = true, remap = false)
    private void getPacketIdWithSFT(CallbackInfoReturnable<Integer> cir) {
        Class<?> cls = this.getClass();
        @SuppressWarnings("unchecked")
        var sft = SFTPacketHandler.PacketTypes.getID((Class<? extends BasePacket>) cls);
        if (sft != null) {
            cir.cancel();
            cir.setReturnValue(sft.getPacketId());
        }
    }
}
