package org.leodreamer.sftcore.mixin.vanilla;

import org.leodreamer.sftcore.common.data.SFTDimensions;
import org.leodreamer.sftcore.integration.vanilla.IChunkMap;

import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LocalMobCapCalculator;
import net.minecraft.world.level.NaturalSpawner;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ServerChunkCache.class)
public class ServerChunkCacheMixin {

    @Shadow
    @Final
    public ServerLevel level;

    @Shadow
    @Final
    public ChunkMap chunkMap;

    @Unique
    private boolean sftcore$isVoid() {
        return level.dimension() == SFTDimensions.VOID_DIMENSION;
    }

    @Redirect(
        method = "tickChunks",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/NaturalSpawner;createState(ILjava/lang/Iterable;Lnet/minecraft/world/level/NaturalSpawner$ChunkGetter;Lnet/minecraft/world/level/LocalMobCapCalculator;)Lnet/minecraft/world/level/NaturalSpawner$SpawnState;"
        )
    )
    private NaturalSpawner.SpawnState skipVoidSpawning(
        int mob, Iterable<Entity> blockPos, NaturalSpawner.ChunkGetter mobCategory, LocalMobCapCalculator entity
    ) {
        if (sftcore$isVoid()) return null;
        return NaturalSpawner.createState(mob, blockPos, mobCategory, entity);
    }

    @Redirect(
        method = "tickChunks",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"
        )
    )
    private boolean skipVoidGameRulesChecking(GameRules rules, GameRules.Key<GameRules.BooleanValue> key) {
        if (sftcore$isVoid()) return false;
        return rules.getBoolean(key);
    }

    @Inject(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ChunkMap;tick(Ljava/util/function/BooleanSupplier;)V"
        )
    )
    private void tickWithDelay(BooleanSupplier hasTimeLeft, boolean tickChunks, CallbackInfo ci) {
        if (tickChunks && level.getServer().getTickCount() % 40 != 0) {
            ((IChunkMap) chunkMap).sftcore$setSkipTick(true);
        }
    }
}
