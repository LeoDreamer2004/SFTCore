package org.leodreamer.sftcore.integration.gtmm;

import com.gregtechceu.gtceu.api.capability.compat.FeCompat;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.trait.ICapabilityTrait;
import com.gregtechceu.gtceu.api.machine.trait.MachineTrait;
import com.gregtechceu.gtceu.api.machine.trait.MachineTraitType;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableEnergyContainer;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraftforge.energy.IEnergyStorage;

import cn.qiuye.gtmoremachine.common.machine.electric.WirelessEnergyInterface;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WirelessEnergyInterfaceFEAdapterTrait extends MachineTrait
    implements ICapabilityTrait, IEnergyStorage {

    public static final MachineTraitType<WirelessEnergyInterfaceFEAdapterTrait> TYPE = new MachineTraitType<>(
        WirelessEnergyInterfaceFEAdapterTrait.class
    );

    private final NotifiableEnergyContainer container;

    public WirelessEnergyInterfaceFEAdapterTrait(NotifiableEnergyContainer container) {
        this.container = container;
        setCapabilityValidator(this::canExposeOnSide);
    }

    @Override
    public MachineTraitType<WirelessEnergyInterfaceFEAdapterTrait> getTraitType() {
        return TYPE;
    }

    @Override
    public IO getCapabilityIO() {
        return IO.IN;
    }

    private boolean canExposeOnSide(@Nullable Direction side) {
        var machine = getMachine();

        if (!(machine instanceof WirelessEnergyInterface wei)) {
            return false;
        }

        return side == null || side == wei.getFrontFacing();
    }

    private boolean canReceiveNow() {
        var machine = getMachine();

        if (!(machine instanceof WirelessEnergyInterface wei)) {
            return false;
        }

        return wei.isWorkingEnabled() && container.getInputVoltage() > 0 && container.getInputAmperage() > 0 &&
            container.getEnergyCanBeInserted() > 0;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (maxReceive <= 0 || !canReceiveNow()) {
            return 0;
        }

        int feToEuRatio = FeCompat.ratio(true);
        long requestEU = FeCompat.toEu(maxReceive, feToEuRatio);

        if (requestEU <= 0) {
            return 0;
        }

        long acceptedEU = Math.min(requestEU, container.getEnergyCanBeInserted());

        if (acceptedEU <= 0) {
            return 0;
        }

        if (!simulate) {
            container.addEnergy(acceptedEU);
        }

        return euToFeInt(acceptedEU, feToEuRatio, maxReceive);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return euToFeStoredInt(container.getEnergyStored());
    }

    @Override
    public int getMaxEnergyStored() {
        return euToFeStoredInt(container.getEnergyCapacity());
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return canReceiveNow();
    }

    private static int euToFeStoredInt(long eu) {
        return euToFeInt(eu, FeCompat.ratio(false), Integer.MAX_VALUE);
    }

    private static int euToFeInt(long eu, int ratio, int upperBound) {
        if (eu <= 0 || ratio <= 0) {
            return 0;
        }

        long maxSafeEU = upperBound / (long) ratio;
        if (eu >= maxSafeEU) {
            return upperBound;
        }

        return (int) Math.min(upperBound, eu * (long) ratio);
    }
}
