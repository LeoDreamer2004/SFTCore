package org.leodreamer.sftcore.common.machine.multiblock.part;

import org.leodreamer.sftcore.api.kinetics.IKineticStressConsumer;
import org.leodreamer.sftcore.api.kinetics.KineticStressIntegration;
import org.leodreamer.sftcore.common.machine.trait.NotifiableStressTrait;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import com.simibubi.create.content.kinetics.KineticNetwork;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class KineticPartMachine extends TieredIOPartMachine implements IKineticStressConsumer {

    private final NotifiableStressTrait stressTrait;

    private transient KineticNetwork linkedKineticNetwork;

    @Getter
    private float shaftSpeed;
    @Getter
    private boolean overstressed;

    public KineticPartMachine(BlockEntityCreationInfo info, int tier, IO io) {
        super(info, tier, io);
        this.stressTrait = attachPersistentTrait(
            "stress",
            new NotifiableStressTrait(this, io, io)
        );
    }

    @Override
    public void onLoad() {
        super.onLoad();

        if (!isRemote()) {
            refreshKineticNetwork();
            subscribeServerTick(() -> {
                if (getOffsetTimer() % 10 == 0) {
                    refreshKineticNetwork();
                }
            });
        }
    }

    @Override
    public void onUnload() {
        KineticStressIntegration.removeConsumer(this);
        super.onUnload();
    }

    @Override
    public void onMachineDestroyed() {
        KineticStressIntegration.removeConsumer(this);
        super.onMachineDestroyed();
    }

    @Override
    public void onNeighborChanged(Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
        super.onNeighborChanged(neighborBlock, neighborPos, isMoving);
        refreshKineticNetwork();
    }

    @Override
    public void onRotated(Direction oldFacing, Direction newFacing) {
        super.onRotated(oldFacing, newFacing);
        refreshKineticNetwork();
    }

    private void refreshKineticNetwork() {
        KineticStressIntegration.refreshConsumer(this);
        stressTrait.notifyStressChanged();
    }

    public float getAvailableStress() {
        if (overstressed) {
            return 0.0F;
        }

        return Math.abs(shaftSpeed) * getTorque();
    }

    public float getTorque() {
        return switch (tier) {
            case GTValues.MV -> 64.0F;
            case GTValues.HV -> 256.0F;
            case GTValues.EV -> 1024.0F;
            default -> 16.0F;
        };
    }

    @Override
    public @Nullable Level sftcore$getKineticLevel() {
        return getLevel();
    }

    @Override
    public BlockPos sftcore$getKineticPos() {
        return getBlockPos();
    }

    @Override
    public float sftcore$getStressImpact() {
        if (io != IO.IN || !isWorkingEnabled()) {
            return 0.0F;
        }

        return getTorque();
    }

    @Override
    public boolean sftcore$hasShaftTowards(Direction face) {
        return face.getAxis() == getFrontFacing().getAxis();
    }

    @Override
    public @Nullable KineticNetwork sftcore$getLinkedKineticNetwork() {
        return linkedKineticNetwork;
    }

    @Override
    public void sftcore$setLinkedKineticNetwork(@Nullable KineticNetwork network) {
        this.linkedKineticNetwork = network;
    }

    @Override
    public void sftcore$onKineticStatsChanged(float speed, boolean overstressed) {
        boolean changed = this.shaftSpeed != speed || this.overstressed != overstressed;

        this.shaftSpeed = speed;
        this.overstressed = overstressed;

        if (changed) {
            stressTrait.notifyStressChanged();
            notifyBlockUpdate();
            setDirty(true);
        }
    }
}
