package org.leodreamer.sftcore.api.kinetics;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

import com.simibubi.create.content.kinetics.KineticNetwork;
import org.jetbrains.annotations.Nullable;

public interface IKineticStressConsumer {

    @Nullable
    Level sftcore$getKineticLevel();

    BlockPos sftcore$getKineticPos();

    /**
     * Create stress impact at 1 RPM.
     * KineticNetwork will multiply this by abs(theoretical speed).
     */
    float sftcore$getStressImpact();

    /**
     * Whether this GT machine exposes a Create shaft connection on this face.
     */
    boolean sftcore$hasShaftTowards(Direction face);

    @Nullable
    KineticNetwork sftcore$getLinkedKineticNetwork();

    void sftcore$setLinkedKineticNetwork(@Nullable KineticNetwork network);

    /**
     * speed here is usable speed, meaning KineticBlockEntity#getSpeed().
     * It is already 0 when the Create network is overstressed.
     */
    void sftcore$onKineticStatsChanged(float speed, boolean overstressed);
}
