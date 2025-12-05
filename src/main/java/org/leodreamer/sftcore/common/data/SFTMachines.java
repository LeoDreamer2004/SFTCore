package org.leodreamer.sftcore.common.data;

import org.leodreamer.sftcore.common.data.machine.GTMultimachineTweaks;
import org.leodreamer.sftcore.common.data.machine.SFTMultiMachines;
import org.leodreamer.sftcore.common.data.machine.SFTPartMachines;
import org.leodreamer.sftcore.common.data.machine.SFTSingleMachines;

public final class SFTMachines {

    public static void init() {
        GTMultimachineTweaks.init();
        SFTSingleMachines.init();
        SFTPartMachines.init();
        SFTMultiMachines.init();
    }
}
