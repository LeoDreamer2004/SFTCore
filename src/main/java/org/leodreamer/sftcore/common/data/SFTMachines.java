package org.leodreamer.sftcore.common.data;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import net.minecraft.network.chat.Component;
import org.leodreamer.sftcore.common.data.machine.GTMultimachineTweaks;
import org.leodreamer.sftcore.common.data.machine.SFTMultiMachines;
import org.leodreamer.sftcore.common.machine.ConfigurableAutoHatchMaintenancePartMachine;
import org.leodreamer.sftcore.common.machine.OreReplicatorMachine;
import org.leodreamer.sftcore.common.machine.multiblock.SFTPartAbility;
import org.leodreamer.sftcore.common.machine.multiblock.part.MachineAdjustmentHatchPartMachine;

import static com.gregtechceu.gtceu.api.GTValues.IV;
import static com.gregtechceu.gtceu.api.GTValues.LV;
import static org.leodreamer.sftcore.SFTCore.REGISTRATE;

public class SFTMachines {

    public static final MachineDefinition ORE_REPLICATOR = REGISTRATE.machine("ore_replicator", OreReplicatorMachine::new)
            .langValue("Ore Replicator")
            .rotationState(RotationState.NONE)
            .register();

    public static final MachineDefinition AUTO_CONFIGURABLE_MAINTENANCE_HATCH = REGISTRATE.machine("configurable_auto_maintenance_hatch", ConfigurableAutoHatchMaintenancePartMachine::new)
            .rotationState(RotationState.ALL)
            .abilities(PartAbility.MAINTENANCE)
            .tooltips(Component.translatable("gtceu.part_sharing.disabled"))
            .overlayTieredHullModel(GTCEu.id("block/machine/part/auto_maintenance_hatch"))
            .tier(IV)
            .register();

    public static final MachineDefinition MACHINE_ADJUSTMENT = REGISTRATE.machine("machine_adjustment_hatch", MachineAdjustmentHatchPartMachine::new)
            .rotationState(RotationState.ALL)
            .abilities(SFTPartAbility.MACHINE_ADJUSTMENT)
            .colorOverlayTieredHullModel("overlay_machine_in_emissive", null, "overlay_machine_holder")
            .tier(LV)
            .allowCoverOnFront(true)
            .register();

    public static void init() {
        GTMultimachineTweaks.init();
        SFTMultiMachines.init();
    }
}
