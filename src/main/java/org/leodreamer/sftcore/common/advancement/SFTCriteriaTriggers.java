package org.leodreamer.sftcore.common.advancement;

import org.leodreamer.sftcore.common.advancement.trigger.*;

import net.minecraft.advancements.CriteriaTriggers;

public final class SFTCriteriaTriggers {

    public static final FormedGTMultiblockTrigger FORMED_GT_MULTIBLOCK = new FormedGTMultiblockTrigger();
    public static final SharedMultiblockPartTrigger SHARED_MULTIBLOCK_PART =
        new SharedMultiblockPartTrigger();
    public static final RecipeExecutedTrigger RECIPE_EXECUTED = new RecipeExecutedTrigger();
    public static final WireBurnedTrigger WIRE_BURNED = new WireBurnedTrigger();
    public static final MachineExplodedTrigger MACHINE_EXPLODED = new MachineExplodedTrigger();
    public static final DuctTapedMaintenanceTrigger DUCT_TAPED_MAINTENANCE = new DuctTapedMaintenanceTrigger();
    public static final MaxCleanroomCleanTrigger MAX_CLEANROOM_CLEAN = new MaxCleanroomCleanTrigger();
    public static final ActiveTransformerLaserTrigger ACTIVE_TRANSFORMER_LASER =
        new ActiveTransformerLaserTrigger();
    public static final UltimateBatteryFullTrigger ULTIMATE_BATTERY_FULL = new UltimateBatteryFullTrigger();


    private static boolean registered = false;

    private SFTCriteriaTriggers() {}

    public static void register() {
        if (registered) {
            return;
        }

        CriteriaTriggers.register(FORMED_GT_MULTIBLOCK);
        CriteriaTriggers.register(SHARED_MULTIBLOCK_PART);
        CriteriaTriggers.register(RECIPE_EXECUTED);
        CriteriaTriggers.register(WIRE_BURNED);
        CriteriaTriggers.register(MACHINE_EXPLODED);
        CriteriaTriggers.register(DUCT_TAPED_MAINTENANCE);
        CriteriaTriggers.register(MAX_CLEANROOM_CLEAN);
        CriteriaTriggers.register(ACTIVE_TRANSFORMER_LASER);
        CriteriaTriggers.register(ULTIMATE_BATTERY_FULL);

        registered = true;
    }
}
