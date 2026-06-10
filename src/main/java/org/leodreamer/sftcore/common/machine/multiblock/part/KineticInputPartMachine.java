package org.leodreamer.sftcore.common.machine.multiblock.part;

import org.leodreamer.sftcore.api.kinetics.IKineticConsumer;
import org.leodreamer.sftcore.api.kinetics.KineticPartHelper;
import org.leodreamer.sftcore.common.machine.trait.NotifiableStressTrait;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;
import com.gregtechceu.gtceu.api.sync_system.annotations.RerenderOnChanged;
import com.gregtechceu.gtceu.api.sync_system.annotations.SyncToClient;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.foundation.utility.CreateLang;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Kinetic integration with Create
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class KineticInputPartMachine extends TieredIOPartMachine implements IKineticConsumer, IHaveGoggleInformation {

    private final NotifiableStressTrait stressTrait;

    @Getter
    @Setter
    @Nullable
    private transient KineticNetwork linkedKineticNetwork;

    @Getter
    @Setter
    @Nullable
    private transient BlockPos drivingSourcePos;

    @Getter
    @SyncToClient
    @RerenderOnChanged
    private float theoreticalSpeed;

    @Getter
    @SyncToClient
    @RerenderOnChanged
    private float shaftSpeed;

    @Getter
    @SyncToClient
    @RerenderOnChanged
    private boolean overstressed;

    private boolean refreshingKinetics;

    public KineticInputPartMachine(BlockEntityCreationInfo info, int tier) {
        super(info, tier, IO.IN);
        this.stressTrait = attachTrait(new NotifiableStressTrait(this, io, io));
    }

    public IO getIO() {
        return io;
    }

    @Override
    public void load(CompoundTag tag) {
        stripLegacyPersistentTraits(tag);
        super.load(tag);
    }

    @Override
    public void onLoad() {
        super.onLoad();

        if (!isRemote()) {
            scheduleForNextServerTick(this::refreshKinetics);
            subscribeServerTick(() -> {
                if (getOffsetTimer() % 20 == 0) {
                    refreshKinetics();
                }
            });
        }
    }

    @Override
    public void onUnload() {
        KineticPartHelper.removeFromNetworkSilently(this);
        super.onUnload();
    }

    @Override
    public void onMachineDestroyed() {
        KineticPartHelper.removeFromNetwork(this);
        super.onMachineDestroyed();
    }

    @Override
    public void onNeighborChanged(Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
        super.onNeighborChanged(neighborBlock, neighborPos, isMoving);
        refreshKinetics();
    }

    @Override
    public void onRotated(Direction oldFacing, Direction newFacing) {
        super.onRotated(oldFacing, newFacing);
        refreshKinetics();
    }

    @Override
    public void refreshKinetics() {
        if (refreshingKinetics) {
            return;
        }

        var level = getLevel();
        if (level == null || level.isClientSide) {
            return;
        }

        refreshingKinetics = true;
        try {
            if (!canConsumeStress()) {
                KineticPartHelper.removeFromNetwork(this);
                return;
            }

            var connection = findDrivingConnection();
            if (connection == null) {
                KineticPartHelper.removeFromNetwork(this);
                return;
            }

            theoreticalSpeed = connection.speed();
            drivingSourcePos = connection.sourcePos();
            KineticPartHelper.addToNetwork(this, connection.network());
            KineticPartHelper.updateKineticStatsFromNetwork(this);
            stressTrait.notifyStressChanged();
        } finally {
            refreshingKinetics = false;
        }
    }

    public float getAvailableStress() {
        if (!canConsumeStress() || overstressed) {
            return 0.0F;
        }

        return Math.abs(shaftSpeed) * getStressImpactAtBaseSpeed();
    }

    public float getStressImpactAtBaseSpeed() {
        return (float) (16L << (2 * Math.max(0, tier - GTValues.LV)));
    }

    public Direction.Axis getRotationAxis() {
        return getFrontFacing().getAxis();
    }

    @Override
    public float getStressImpact() {
        return canConsumeStress() ? getStressImpactAtBaseSpeed() : 0.0F;
    }

    @Override
    public boolean hasShaftTowards(Direction face) {
        // only face
        return face == getFrontFacing();
    }

    @Override
    public void onKineticStatsChanged(float theoreticalSpeed, float usableSpeed, boolean overstressed) {
        boolean changed = this.theoreticalSpeed != theoreticalSpeed || this.shaftSpeed != usableSpeed ||
            this.overstressed != overstressed;

        this.theoreticalSpeed = theoreticalSpeed;
        this.shaftSpeed = usableSpeed;
        this.overstressed = overstressed;

        if (theoreticalSpeed == 0.0F && usableSpeed == 0.0F && !overstressed) {
            drivingSourcePos = null;
        }

        if (changed) {
            syncDataHolder.markClientSyncFieldDirty("theoreticalSpeed");
            syncDataHolder.markClientSyncFieldDirty("shaftSpeed");
            syncDataHolder.markClientSyncFieldDirty("overstressed");
            stressTrait.notifyStressChanged();
            notifyBlockUpdate();
        }
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (io != IO.IN) {
            return false;
        }

        CreateLang.translate("gui.goggles.kinetic_stats")
            .forGoggles(tooltip);

        CreateLang.translate("tooltip.stressImpact")
            .style(ChatFormatting.GRAY)
            .forGoggles(tooltip);

        float stress = getStressImpactAtBaseSpeed() * Math.abs(theoreticalSpeed);
        CreateLang.number(stress)
            .translate("generic.unit.stress")
            .style(overstressed ? ChatFormatting.RED : ChatFormatting.AQUA)
            .space()
            .add(CreateLang.translate("gui.goggles.at_current_speed").style(ChatFormatting.DARK_GRAY))
            .forGoggles(tooltip, 1);

        if (overstressed) {
            CreateLang.translate("gui.stressometer.overstressed")
                .style(ChatFormatting.GOLD)
                .forGoggles(tooltip);
        }

        return true;
    }

    private boolean canConsumeStress() {
        return io == IO.IN && isWorkingEnabled();
    }

    private @Nullable DriveConnection findDrivingConnection() {
        var level = getLevel();
        if (level == null) {
            return null;
        }

        var direction = getFrontFacing();
        var neighbour = KineticPartHelper.getKineticBlockEntity(level, getBlockPos().relative(direction));
        if (neighbour == null || !neighbour.hasNetwork()) {
            return null;
        }

        float speed = KineticPartHelper.getConveyedSpeedFromKinetic(neighbour, this);
        if (speed == 0.0F) {
            return null;
        }

        var network = neighbour.getOrCreateNetwork();
        return network == null ? null : new DriveConnection(network, speed, neighbour.getBlockPos());
    }

    private static void stripLegacyPersistentTraits(CompoundTag tag) {
        tag.remove("traitHolder");
    }

    private record DriveConnection(KineticNetwork network, float speed, BlockPos sourcePos) {}
}
