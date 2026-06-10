package org.leodreamer.sftcore.api.kinetics;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

import com.simibubi.create.content.kinetics.KineticNetwork;
import org.jetbrains.annotations.Nullable;

public interface IKineticConsumer {

    @Nullable
    Level getLevel();

    BlockPos getBlockPos();

    /**
     * Create stress impact at 1 RPM. The KineticNetwork integration multiplies this by
     * abs(theoretical speed).
     */
    float getStressImpact();

    /**
     * Whether this GT machine exposes a Create shaft connection on this face.
     */
    boolean hasShaftTowards(Direction face);

    float getTheoreticalSpeed();

    @Nullable
    KineticNetwork getLinkedKineticNetwork();

    void setLinkedKineticNetwork(@Nullable KineticNetwork network);

    @Nullable
    BlockPos getDrivingSourcePos();

    void setDrivingSourcePos(@Nullable BlockPos pos);

    void onKineticStatsChanged(float theoreticalSpeed, float usableSpeed, boolean overstressed);

    void refreshKinetics();
}
