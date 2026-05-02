package org.leodreamer.sftcore.common.advancement;

import net.minecraft.advancements.CriteriaTriggers;
import org.leodreamer.sftcore.common.advancement.trigger.FormedGTMultiblockTrigger;

public final class SFTCriteriaTriggers {

    public static final FormedGTMultiblockTrigger FORMED_GT_MULTIBLOCK =
        new FormedGTMultiblockTrigger();

    private static boolean registered = false;

    private SFTCriteriaTriggers() {
    }

    public static void register() {
        if (registered) {
            return;
        }
        CriteriaTriggers.register(FORMED_GT_MULTIBLOCK);
        registered = true;
    }
}
