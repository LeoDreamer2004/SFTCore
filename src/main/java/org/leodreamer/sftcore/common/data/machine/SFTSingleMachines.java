package org.leodreamer.sftcore.common.data.machine;

import org.leodreamer.sftcore.api.registry.SFTTooltipsBuilder;
import org.leodreamer.sftcore.common.machine.GTWirelessControllerMachine;
import org.leodreamer.sftcore.common.machine.OreReplicatorMachine;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;

import static org.leodreamer.sftcore.SFTCore.REGISTRATE;

public final class SFTSingleMachines {

    public static final MachineDefinition ORE_REPLICATOR = REGISTRATE
        .machine("ore_replicator", OreReplicatorMachine::new)
        .tooltips(
            builder -> SFTTooltipsBuilder.machine(builder.id)
                .tip("Place it under an ore block and it will generate ores.")
        )
        .langValue("Ore Replicator")
        .rotationState(RotationState.NONE)
        .register();

    public static final MachineDefinition WIRELESS_CONTROLLER = REGISTRATE
        .machine("wireless_controller", GTWirelessControllerMachine::new)
        .tooltips(
            builder -> SFTTooltipsBuilder.machine(builder.id)
                .intro("Use Wireless Connector to connect to any GregTech ME Buses.")
        )
        .langValue("Wireless Controller")
        .rotationState(RotationState.ALL)
        .tier(GTValues.IV)
        .colorOverlayTieredHullModel(GTCEu.id("block/overlay/appeng/me_buffer_hatch_proxy"))
        .register();

    public static void init() {}
}
