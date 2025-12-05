package org.leodreamer.sftcore.common.data.machine;

import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import org.leodreamer.sftcore.api.registry.SFTTooltipsBuilder;
import org.leodreamer.sftcore.common.machine.OreReplicatorMachine;

import static org.leodreamer.sftcore.SFTCore.REGISTRATE;

public class SFTSingleMachines {

    public static final MachineDefinition ORE_REPLICATOR = REGISTRATE.machine("ore_replicator", OreReplicatorMachine::new)
            .tooltips(builder -> SFTTooltipsBuilder.of().with(builder)
                    .tip("Place it under an ore block and it will generate ores."))
            .langValue("Ore Replicator")
            .rotationState(RotationState.NONE)
            .register();

    public static void init() {
    }
}
