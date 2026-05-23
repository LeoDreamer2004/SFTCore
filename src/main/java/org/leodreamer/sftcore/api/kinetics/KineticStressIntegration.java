package org.leodreamer.sftcore.api.kinetics;

import com.gregtechceu.gtceu.api.machine.MetaMachine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import org.jetbrains.annotations.Nullable;

public final class KineticStressIntegration {

    private KineticStressIntegration() {}

    public static void refreshConsumersAround(KineticBlockEntity kinetic) {
        var level = kinetic.getLevel();
        if (level == null || level.isClientSide) {
            return;
        }

        refreshConsumersAround(level, kinetic.getBlockPos());
    }

    public static void refreshConsumersAround(Level level, BlockPos center) {
        if (level.isClientSide) {
            return;
        }

        for (var direction : Direction.values()) {
            refreshConsumerAt(level, center.relative(direction));
        }
    }

    public static void refreshConsumerAt(Level level, BlockPos pos) {
        if (level.isClientSide || !level.isLoaded(pos)) {
            return;
        }

        var machine = MetaMachine.getMachine(level, pos);
        if (machine instanceof IKineticStressConsumer consumer) {
            refreshConsumer(consumer);
        }
    }

    public static void refreshConsumer(IKineticStressConsumer consumer) {
        var level = consumer.sftcore$getKineticLevel();
        if (level == null || level.isClientSide) {
            return;
        }

        var pos = consumer.sftcore$getKineticPos();
        var oldNetwork = consumer.sftcore$getLinkedKineticNetwork();

        var connected = findConnectedKineticBlockEntity(consumer);
        KineticNetwork newNetwork = null;

        if (connected != null && connected.hasNetwork()) {
            newNetwork = connected.getOrCreateNetwork();
        }

        if (oldNetwork != null && oldNetwork != newNetwork) {
            ((SFTKineticNetworkAccessor) oldNetwork).sftcore$removeStressConsumer(level, pos);
        }

        if (newNetwork != null) {
            ((SFTKineticNetworkAccessor) newNetwork).sftcore$addStressConsumer(
                level,
                pos,
                consumer.sftcore$getStressImpact()
            );
        }

        consumer.sftcore$setLinkedKineticNetwork(newNetwork);

        if (connected == null) {
            consumer.sftcore$onKineticStatsChanged(0.0F, false);
        } else {
            consumer.sftcore$onKineticStatsChanged(connected.getSpeed(), connected.isOverStressed());
        }
    }

    public static void removeConsumer(IKineticStressConsumer consumer) {
        var level = consumer.sftcore$getKineticLevel();
        var oldNetwork = consumer.sftcore$getLinkedKineticNetwork();

        if (level != null && oldNetwork != null) {
            ((SFTKineticNetworkAccessor) oldNetwork).sftcore$removeStressConsumer(
                level,
                consumer.sftcore$getKineticPos()
            );
        }

        consumer.sftcore$setLinkedKineticNetwork(null);
        consumer.sftcore$onKineticStatsChanged(0.0F, false);
    }

    @Nullable
    public static KineticBlockEntity findConnectedKineticBlockEntity(IKineticStressConsumer consumer) {
        var level = consumer.sftcore$getKineticLevel();
        if (level == null) {
            return null;
        }

        var pos = consumer.sftcore$getKineticPos();
        KineticBlockEntity best = null;

        for (var face : Direction.values()) {
            if (!consumer.sftcore$hasShaftTowards(face)) {
                continue;
            }

            var neighbourPos = pos.relative(face);

            if (!level.isLoaded(neighbourPos)) {
                continue;
            }

            var neighbourState = level.getBlockState(neighbourPos);
            if (!(neighbourState.getBlock() instanceof IRotate rotate)) {
                continue;
            }

            var blockEntity = level.getBlockEntity(neighbourPos);
            if (!(blockEntity instanceof KineticBlockEntity kinetic)) {
                continue;
            }

            if (!rotate.hasShaftTowards(level, neighbourPos, neighbourState, face.getOpposite())) {
                continue;
            }

            if (best == null || Math.abs(kinetic.getTheoreticalSpeed()) > Math.abs(best.getTheoreticalSpeed())) {
                best = kinetic;
            }
        }

        return best;
    }
}
