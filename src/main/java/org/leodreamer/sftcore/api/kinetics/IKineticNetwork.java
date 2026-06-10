package org.leodreamer.sftcore.api.kinetics;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface IKineticNetwork {

    Long sftcore$getId();

    void sftcore$addStressConsumer(
        Level level,
        BlockPos pos,
        float stressImpact,
        float speed,
        IKineticConsumer consumer
    );

    void sftcore$updateStressConsumer(
        Level level,
        BlockPos pos,
        float stressImpact,
        float speed,
        IKineticConsumer consumer
    );

    default void sftcore$removeStressConsumer(Level level, BlockPos pos) {
        sftcore$removeStressConsumer(level, pos, true);
    }

    void sftcore$removeStressConsumer(Level level, BlockPos pos, boolean updateNetwork);

    boolean sftcore$isOverstressed();
}
