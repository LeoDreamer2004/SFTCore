package org.leodreamer.sftcore.api.kinetics;

import com.gregtechceu.gtceu.api.machine.MetaMachine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class KineticPartHelper {

    private KineticPartHelper() {}

    public static @Nullable IKineticConsumer getKineticPart(Level level, BlockPos pos) {
        if (level == null || !level.isLoaded(pos)) {
            return null;
        }

        var machine = MetaMachine.getMachine(level, pos);
        return machine instanceof IKineticConsumer consumer ? consumer : null;
    }

    public static boolean hasShaftConnection(Level level, BlockPos pos, Direction face) {
        var consumer = getKineticPart(level, pos);
        if (consumer != null) {
            return consumer.hasShaftTowards(face);
        }

        if (!level.isLoaded(pos)) {
            return false;
        }

        var state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof IRotate rotate)) {
            return false;
        }

        return rotate.hasShaftTowards(level, pos, state, face);
    }

    public static float getConveyedSpeedFromKinetic(KineticBlockEntity from, IKineticConsumer to) {
        var direction = directionBetween(from.getBlockPos(), to.getBlockPos());
        if (direction == null) {
            return 0.0F;
        }

        if (!hasShaftConnection(from.getLevel(), from.getBlockPos(), direction)) {
            return 0.0F;
        }

        if (!to.hasShaftTowards(direction.getOpposite())) {
            return 0.0F;
        }

        return from.getTheoreticalSpeed();
    }

    public static void addToNetwork(IKineticConsumer consumer, KineticNetwork network) {
        if (network == null || consumer == null) {
            return;
        }

        var level = consumer.getLevel();
        var pos = consumer.getBlockPos();
        float stressImpact = consumer.getStressImpact();
        float speed = consumer.getTheoreticalSpeed();

        var oldNetwork = consumer.getLinkedKineticNetwork();
        if (oldNetwork == network) {
            ((IKineticNetwork) network).sftcore$updateStressConsumer(
                level,
                pos,
                stressImpact,
                speed,
                consumer
            );
            return;
        }

        if (oldNetwork != null) {
            ((IKineticNetwork) oldNetwork).sftcore$removeStressConsumer(level, pos);
            consumer.setLinkedKineticNetwork(null);
        }

        consumer.setLinkedKineticNetwork(network);
        ((IKineticNetwork) network).sftcore$addStressConsumer(
            level,
            pos,
            stressImpact,
            speed,
            consumer
        );
    }

    public static void removeFromNetwork(IKineticConsumer consumer) {
        removeFromNetwork(consumer, true, true);
    }

    public static void removeFromNetworkSilently(IKineticConsumer consumer) {
        removeFromNetwork(consumer, false, false);
    }

    private static void removeFromNetwork(IKineticConsumer consumer, boolean updateNetwork, boolean notifyConsumer) {
        if (consumer == null) {
            return;
        }

        var oldNetwork = consumer.getLinkedKineticNetwork();
        if (oldNetwork != null) {
            ((IKineticNetwork) oldNetwork).sftcore$removeStressConsumer(
                consumer.getLevel(),
                consumer.getBlockPos(),
                updateNetwork
            );
        }

        consumer.setLinkedKineticNetwork(null);
        consumer.setDrivingSourcePos(null);
        if (notifyConsumer) {
            consumer.onKineticStatsChanged(0.0F, 0.0F, false);
        }
    }

    public static void updateKineticStatsFromNetwork(IKineticConsumer consumer) {
        var network = consumer.getLinkedKineticNetwork();
        if (network == null) {
            consumer.onKineticStatsChanged(0.0F, 0.0F, false);
            return;
        }

        boolean overstressed = ((IKineticNetwork) network).sftcore$isOverstressed();
        float theoreticalSpeed = consumer.getTheoreticalSpeed();
        consumer.onKineticStatsChanged(theoreticalSpeed, overstressed ? 0.0F : theoreticalSpeed, overstressed);
    }

    public static void refreshConsumersAround(Level level, BlockPos pos) {
        if (level == null || level.isClientSide || pos == null) {
            return;
        }

        for (var direction : Direction.values()) {
            var consumer = getKineticPart(level, pos.relative(direction));
            if (consumer != null) {
                consumer.refreshKinetics();
            }
        }
    }

    public static boolean isAttachedToNetwork(IKineticConsumer consumer, KineticNetwork network) {
        return consumer != null && network != null && Objects.equals(consumer.getLinkedKineticNetwork(), network);
    }

    public static boolean isValidConsumerConnection(IKineticConsumer consumer, KineticNetwork network) {
        if (!isAttachedToNetwork(consumer, network)) {
            return false;
        }

        var level = consumer.getLevel();
        var consumerPos = consumer.getBlockPos();
        var sourcePos = consumer.getDrivingSourcePos();
        if (level == null || level.isClientSide || sourcePos == null) {
            return false;
        }

        var sourceToConsumer = directionBetween(sourcePos, consumerPos);
        if (sourceToConsumer == null || !consumer.hasShaftTowards(sourceToConsumer.getOpposite())) {
            return false;
        }

        Long networkId = ((IKineticNetwork) network).sftcore$getId();
        var sourceKinetic = getKineticBlockEntity(level, sourcePos);
        return sourceKinetic != null &&
            sourceKinetic.hasNetwork() &&
            Objects.equals(sourceKinetic.network, networkId) &&
            getConveyedSpeedFromKinetic(sourceKinetic, consumer) != 0.0F;
    }

    public static void clearNetworkLink(IKineticConsumer consumer) {
        if (consumer == null) {
            return;
        }

        consumer.setLinkedKineticNetwork(null);
        consumer.setDrivingSourcePos(null);
        consumer.onKineticStatsChanged(0.0F, 0.0F, false);
    }

    public static @Nullable KineticBlockEntity getKineticBlockEntity(Level level, BlockPos pos) {
        if (level == null || !level.isLoaded(pos)) {
            return null;
        }

        var blockEntity = level.getBlockEntity(pos);
        return blockEntity instanceof KineticBlockEntity kinetic ? kinetic : null;
    }

    public static @Nullable Direction directionBetween(BlockPos from, BlockPos to) {
        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();
        int dz = to.getZ() - from.getZ();

        if (Math.abs(dx) + Math.abs(dy) + Math.abs(dz) != 1) {
            return null;
        }

        return Direction.getNearest(dx, dy, dz);
    }
}
