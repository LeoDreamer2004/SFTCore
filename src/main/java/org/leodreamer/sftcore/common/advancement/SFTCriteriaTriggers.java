package org.leodreamer.sftcore.common.advancement;

import org.leodreamer.sftcore.common.advancement.trigger.*;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.ActiveTransformerMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.CleanroomMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.generator.LargeCombustionEngineMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.part.MaintenanceHatchPartMachine;

import net.minecraft.advancements.CriteriaTriggers;

public final class SFTCriteriaTriggers {

    // spotless:off
    public static final FormedGTMultiblockTrigger FORMED_GT_MULTIBLOCK =
        new FormedGTMultiblockTrigger();
    public static final SimpleGTMachineTrigger<MultiblockControllerMachine> SHARED_MULTIBLOCK_PART =
        new SimpleGTMachineTrigger<>("shared_multiblock_part");
    public static final RecipeExecutedTrigger RECIPE_EXECUTED =
        new RecipeExecutedTrigger();
    public static final WireBurnedTrigger WIRE_BURNED =
        new WireBurnedTrigger();
    public static final SimpleGTMachineTrigger<MetaMachine> MACHINE_EXPLODED =
        new SimpleGTMachineTrigger<>("machine_exploded");
    public static final SimpleGTMachineTrigger<MaintenanceHatchPartMachine> DUCT_TAPED_MAINTENANCE =
        new SimpleGTMachineTrigger<>("duct_taped_maintenance");
    public static final SimpleGTMachineTrigger<CleanroomMachine> MAX_CLEANROOM_CLEAN =
        new SimpleGTMachineTrigger<>("max_cleanroom_clean");
    public static final SimpleGTMachineTrigger<ActiveTransformerMachine> ACTIVE_TRANSFORMER_LASER =
        new SimpleGTMachineTrigger<>("active_transformer_laser");
    public static final SimpleAnyTrigger ULTIMATE_BATTERY_FULL =
        new SimpleAnyTrigger("ultimate_battery_full");
    public static final SimpleGTMachineTrigger<LargeCombustionEngineMachine> OXYGEN_BOOSTED_COMBUSTION =
        new SimpleGTMachineTrigger<>("oxygen_combustion_engine");
    // spotless:on

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
        CriteriaTriggers.register(OXYGEN_BOOSTED_COMBUSTION);

        registered = true;
    }
}
