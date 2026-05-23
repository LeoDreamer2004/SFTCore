package org.leodreamer.sftcore.api.kinetics;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface SFTKineticNetworkAccessor {

    void sftcore$addStressConsumer(Level level, BlockPos pos, float stressImpact);

    void sftcore$removeStressConsumer(Level level, BlockPos pos);
}
