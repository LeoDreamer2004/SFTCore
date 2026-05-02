package org.leodreamer.sftcore.mixin.vanilla;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IExplosionMachine;
import com.gregtechceu.gtceu.api.machine.feature.IMachineFeature;
import com.gregtechceu.gtceu.config.ConfigHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.leodreamer.sftcore.common.advancement.SFTCriteriaTriggers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = IExplosionMachine.class, remap = false)
public interface IExplosionMachineMixin extends IMachineFeature {

    /**
     * @author LeoDreamer
     * @reason Mixin does not support injectors for interfaces, see <a href="https://github.com/SpongePowered/Mixin/issues/421">Mixin issue</a>
     */
    @Overwrite
    default void doExplosion(BlockPos pos, float explosionPower) {
        MetaMachine machine = self();
        Level level = machine.getLevel();
        if (level == null) return;
        SFTCriteriaTriggers.MACHINE_EXPLODED.trigger(level, pos);
        level.removeBlock(pos, false);
        level.explode(null, (double) pos.getX() + (double) 0.5F, (double) pos.getY() + (double) 0.5F, (double) pos.getZ() + (double) 0.5F, explosionPower, ConfigHolder.INSTANCE.machines.doesExplosionDamagesTerrain ? Level.ExplosionInteraction.BLOCK : Level.ExplosionInteraction.NONE);
    }
}
