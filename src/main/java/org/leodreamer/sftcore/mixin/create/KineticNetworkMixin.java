package org.leodreamer.sftcore.mixin.create;

import org.leodreamer.sftcore.api.kinetics.IKineticStressConsumer;
import org.leodreamer.sftcore.api.kinetics.KineticStressIntegration;
import org.leodreamer.sftcore.api.kinetics.SFTKineticNetworkAccessor;

import com.gregtechceu.gtceu.api.machine.MetaMachine;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import com.simibubi.create.content.kinetics.KineticNetwork;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Mixin(value = KineticNetwork.class, remap = false)
public abstract class KineticNetworkMixin implements SFTKineticNetworkAccessor {

    @Shadow
    public Long id;

    @Shadow
    public abstract void updateStress();

    @Unique
    private final Map<BlockPos, Float> sftcore$stressConsumers = new HashMap<>();

    @Unique
    private Level sftcore$level;

    @Override
    public void sftcore$addStressConsumer(Level level, BlockPos pos, float stressImpact) {
        if (level == null || level.isClientSide) {
            return;
        }

        this.sftcore$level = level;
        this.sftcore$stressConsumers.put(pos.immutable(), stressImpact);
        this.updateStress();
    }

    @Override
    public void sftcore$removeStressConsumer(Level level, BlockPos pos) {
        if (level != null && level.isClientSide) {
            return;
        }

        if (this.sftcore$stressConsumers.remove(pos) != null) {
            this.updateStress();
        }
    }

    @Inject(method = "calculateStress", at = @At("RETURN"), cancellable = true)
    private void sftcore$appendMetaMachineStress(CallbackInfoReturnable<Float> cir) {
        if (sftcore$level == null || sftcore$level.isClientSide || sftcore$stressConsumers.isEmpty()) {
            return;
        }

        float extraStress = 0.0F;
        var iterator = sftcore$stressConsumers.entrySet().iterator();

        while (iterator.hasNext()) {
            var entry = iterator.next();
            BlockPos pos = entry.getKey();

            if (!sftcore$level.isLoaded(pos)) {
                continue;
            }

            var machine = MetaMachine.getMachine(sftcore$level, pos);
            if (!(machine instanceof IKineticStressConsumer consumer)) {
                iterator.remove();
                continue;
            }

            var connected = KineticStressIntegration.findConnectedKineticBlockEntity(consumer);
            if (connected == null || !connected.hasNetwork() || !Objects.equals(connected.network, this.id)) {
                iterator.remove();
                consumer.sftcore$setLinkedKineticNetwork(null);
                consumer.sftcore$onKineticStatsChanged(0.0F, false);
                continue;
            }

            float impact = entry.getValue();
            extraStress += impact * Math.abs(connected.getTheoreticalSpeed());
        }

        cir.setReturnValue(cir.getReturnValue() + extraStress);
    }
}
