package org.leodreamer.sftcore.mixin.create;

import org.leodreamer.sftcore.api.kinetics.IKineticConsumer;
import org.leodreamer.sftcore.api.kinetics.IKineticNetwork;
import org.leodreamer.sftcore.api.kinetics.KineticPartHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.content.kinetics.base.IRotate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(value = KineticNetwork.class, remap = false)
public abstract class KineticNetworkMixin implements IKineticNetwork {

    @Shadow
    public Long id;

    @Shadow
    public abstract void updateNetwork();

    @Shadow
    public abstract float calculateCapacity();

    @Unique
    private final Map<BlockPos, SFTStressEntry> sftcore$stressConsumers = new HashMap<>();

    @Unique
    private Level sftcore$level;

    @Unique
    private float sftcore$currentCapacity;

    @Unique
    private float sftcore$currentStress;

    @Override
    public Long sftcore$getId() {
        return id;
    }

    @Override
    public void sftcore$addStressConsumer(
        Level level,
        BlockPos pos,
        float stressImpact,
        float speed,
        IKineticConsumer consumer
    ) {
        sftcore$updateStressConsumer(level, pos, stressImpact, speed, consumer);
    }

    @Override
    public void sftcore$updateStressConsumer(
        Level level,
        BlockPos pos,
        float stressImpact,
        float speed,
        IKineticConsumer consumer
    ) {
        if (level == null || level.isClientSide || pos == null || consumer == null) {
            return;
        }

        sftcore$level = level;
        sftcore$stressConsumers.put(pos.immutable(), new SFTStressEntry(stressImpact, speed, consumer));
        updateNetwork();
    }

    @Override
    public void sftcore$removeStressConsumer(Level level, BlockPos pos, boolean updateNetwork) {
        if ((level != null && level.isClientSide) || pos == null) {
            return;
        }

        if (sftcore$stressConsumers.remove(pos) != null && updateNetwork) {
            updateNetwork();
        }
    }

    @Override
    public boolean sftcore$isOverstressed() {
        return IRotate.StressImpact.isEnabled() && sftcore$currentCapacity < sftcore$currentStress;
    }

    @Inject(method = "calculateStress", at = @At("RETURN"), cancellable = true)
    private void sftcore$appendMetaMachineStress(CallbackInfoReturnable<Float> cir) {
        if (sftcore$level == null || sftcore$level.isClientSide || sftcore$stressConsumers.isEmpty()) {
            sftcore$currentStress = cir.getReturnValue();
            return;
        }

        float extraStress = 0.0F;
        var iterator = sftcore$stressConsumers.entrySet().iterator();

        while (iterator.hasNext()) {
            var entry = iterator.next();
            var pos = entry.getKey();
            var stressEntry = entry.getValue();

            if (!sftcore$level.isLoaded(pos)) {
                continue;
            }

            var consumer = KineticPartHelper.getKineticPart(sftcore$level, pos);
            if (consumer == null || consumer != stressEntry.consumer()) {
                iterator.remove();
                continue;
            }

            var self = (KineticNetwork) (Object) this;
            if (!KineticPartHelper.isValidConsumerConnection(consumer, self)) {
                iterator.remove();
                if (KineticPartHelper.isAttachedToNetwork(consumer, self)) {
                    KineticPartHelper.clearNetworkLink(consumer);
                }
                continue;
            }

            extraStress += stressEntry.impact() * Math.abs(stressEntry.speed());
        }

        sftcore$currentStress = cir.getReturnValue() + extraStress;
        cir.setReturnValue(sftcore$currentStress);
    }

    @Inject(method = "calculateCapacity", at = @At("RETURN"))
    private void sftcore$rememberCapacity(CallbackInfoReturnable<Float> cir) {
        sftcore$currentCapacity = cir.getReturnValue();
    }

    @Inject(method = "updateNetwork", at = @At("TAIL"))
    private void sftcore$syncConsumersOnNetworkUpdate(CallbackInfo ci) {
        sftcore$syncConsumers();
    }

    @Inject(method = "sync", at = @At("TAIL"))
    private void sftcore$syncConsumers(CallbackInfo ci) {
        sftcore$syncConsumers();
    }

    @Unique
    private void sftcore$syncConsumers() {
        sftcore$currentCapacity = calculateCapacity();

        if (sftcore$level == null || sftcore$stressConsumers.isEmpty()) {
            return;
        }

        boolean overstressed = sftcore$isOverstressed();
        for (var it = sftcore$stressConsumers.entrySet().iterator(); it.hasNext();) {
            var entry = it.next();
            var pos = entry.getKey();
            var stressEntry = entry.getValue();
            var consumer = KineticPartHelper.getKineticPart(sftcore$level, pos);
            if (consumer == null || consumer != stressEntry.consumer()) {
                it.remove();
                continue;
            }

            consumer.onKineticStatsChanged(
                stressEntry.speed(),
                overstressed ? 0.0F : stressEntry.speed(),
                overstressed
            );
        }
    }

    @Unique
    private record SFTStressEntry(float impact, float speed, IKineticConsumer consumer) {}
}
