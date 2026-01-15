package org.leodreamer.sftcore.mixin.vanilla;

import org.leodreamer.sftcore.integration.vanilla.IChunkMap;

import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(value = ChunkMap.class, priority = 0)
public abstract class ChunkMapMixin implements IChunkMap {

    @Shadow
    private volatile Long2ObjectLinkedOpenHashMap<ChunkHolder> visibleChunkMap;

    @Unique
    private boolean sftcore$skip;

    @Inject(
        method = "tick(Ljava/util/function/BooleanSupplier;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ChunkMap;processUnloads(Ljava/util/function/BooleanSupplier;)V"
        ), cancellable = true
    )
    private void tick(BooleanSupplier hasMoreTime, CallbackInfo ci) {
        if (sftcore$skip) {
            sftcore$skip = false;
            ci.cancel();
        }
    }

    public void sftcore$setSkipTick(boolean skip) {
        sftcore$skip = skip;
    }

    /**
     * @author LeoDreamer
     * @reason skip the slow wrapper of <code>Iterables.unmodifiableIterable</code>
     */
    @Overwrite
    public Iterable<ChunkHolder> getChunks() {
        return this.visibleChunkMap.values();
    }
}
