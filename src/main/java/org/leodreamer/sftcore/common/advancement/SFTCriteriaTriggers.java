package org.leodreamer.sftcore.common.advancement;

import net.minecraft.advancements.CriteriaTriggers;
import org.leodreamer.sftcore.common.advancement.trigger.DuctTapedMaintenanceTrigger;
import org.leodreamer.sftcore.common.advancement.trigger.FormedGTMultiblockTrigger;
import org.leodreamer.sftcore.common.advancement.trigger.MachineExplodedTrigger;
import org.leodreamer.sftcore.common.advancement.trigger.WireBurnedTrigger;

public final class SFTCriteriaTriggers {

    public static final FormedGTMultiblockTrigger FORMED_GT_MULTIBLOCK = new FormedGTMultiblockTrigger();
    public static final WireBurnedTrigger WIRE_BURNED = new WireBurnedTrigger();
    public static final MachineExplodedTrigger MACHINE_EXPLODED = new MachineExplodedTrigger();
    public static final DuctTapedMaintenanceTrigger DUCT_TAPED_MAINTENANCE = new DuctTapedMaintenanceTrigger();

    private static boolean registered = false;

    private SFTCriteriaTriggers() {}

    public static void register() {
        if (registered) {
            return;
        }

        CriteriaTriggers.register(FORMED_GT_MULTIBLOCK);
        CriteriaTriggers.register(WIRE_BURNED);
        CriteriaTriggers.register(MACHINE_EXPLODED);
        CriteriaTriggers.register(DUCT_TAPED_MAINTENANCE);

        registered = true;
    }
}
