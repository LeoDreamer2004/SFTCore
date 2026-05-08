package org.leodreamer.sftcore.common.data;

import org.leodreamer.sftcore.common.data.lang.SFTTooltipsBuilder;
import org.leodreamer.sftcore.common.machine.GTWirelessControllerMachine;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;

import static org.leodreamer.sftcore.SFTCore.REGISTRATE;

public final class SFTMachines {

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
