package org.leodreamer.sftcore.mixin.ae2.sync;

import appeng.core.sync.BasePacket;
import appeng.core.sync.network.NetworkHandler;
import net.minecraft.network.FriendlyByteBuf;
import org.leodreamer.sftcore.integration.ae2.sync.SFTPacketHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NetworkHandler.class)
public class NetworkHandlerMixin {
    @Inject(method = "deserializePacket", at = @At("HEAD"), cancellable = true, remap = false)
    private static void deserializePacketWithSFT(FriendlyByteBuf payload, CallbackInfoReturnable<BasePacket> cir) {
        int packetId = payload.getInt(payload.readerIndex());

        if (SFTPacketHandler.PacketTypes.isSFTType(packetId)) {
            payload.readInt();
            var packet = SFTPacketHandler.PacketTypes.getPacket(packetId).parsePacket(payload);
            cir.setReturnValue(packet);
        }
    }
}
