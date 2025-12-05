package org.leodreamer.sftcore.common.data.machine;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.CleanroomType;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.common.machine.multiblock.part.DualHatchPartMachine;
import net.minecraft.network.chat.Component;
import org.leodreamer.sftcore.api.registry.SFTTooltipsBuilder;
import org.leodreamer.sftcore.common.machine.multiblock.SFTPartAbility;
import org.leodreamer.sftcore.common.machine.multiblock.part.ConfigurableAutoMaintenanceHatchPartMachine;
import org.leodreamer.sftcore.common.machine.multiblock.part.ConfigurableCleaningMaintenanceHatchPartMachine;
import org.leodreamer.sftcore.common.machine.multiblock.part.MachineAdjustmentHatchPartMachine;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.capability.recipe.IO.IN;
import static com.gregtechceu.gtceu.api.capability.recipe.IO.OUT;
import static com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties.IS_FORMED;
import static com.gregtechceu.gtceu.common.data.machines.GTMachineUtils.*;
import static org.leodreamer.sftcore.SFTCore.REGISTRATE;

public class SFTPartMachines {

    public static final MachineDefinition CONFIGURABLE_AUTO_MAINTENANCE_HATCH = REGISTRATE.machine("configurable_auto_maintenance_hatch", ConfigurableAutoMaintenanceHatchPartMachine::new)
            .rotationState(RotationState.ALL)
            .abilities(PartAbility.MAINTENANCE)
            .modelProperty(IS_FORMED, false)
            .tooltips(builder -> SFTTooltipsBuilder.of().with(builder).disableSharing())
            .overlayTieredHullModel(GTCEu.id("block/machine/part/auto_maintenance_hatch"))
            .tier(IV)
            .register();

    public static final MachineDefinition CONFIGURABLE_CLEANING_MAINTENANCE_HATCH = REGISTRATE.machine("configurable_cleaning_maintenance_hatch",
                    (holder) -> new ConfigurableCleaningMaintenanceHatchPartMachine(holder, CleanroomType.CLEANROOM))
            .rotationState(RotationState.ALL)
            .abilities(PartAbility.MAINTENANCE)
            .modelProperty(IS_FORMED, false)
            .tooltips(builder -> SFTTooltipsBuilder.of().with(builder).disableSharing())
            .overlayTieredHullModel(GTCEu.id("block/machine/part/cleaning_maintenance_hatch"))
            .tier(LuV)
            .register();

    public static final MachineDefinition MACHINE_ADJUSTMENT = REGISTRATE.machine("machine_adjustment_hatch", MachineAdjustmentHatchPartMachine::new)
            .rotationState(RotationState.ALL)
            .abilities(SFTPartAbility.MACHINE_ADJUSTMENT)
            .colorOverlayTieredHullModel("overlay_machine_in_emissive", null, "overlay_machine_holder")
            .tier(LV)
            .allowCoverOnFront(true)
            .register();

    // add the dual hatch omitted in GTM
    public static final MachineDefinition[] DUAL_IMPORT_HATCH = registerTieredMachines(
            REGISTRATE,
            "dual_input_hatch",
            (holder, tier) -> new DualHatchPartMachine(holder, tier, IN),
            (tier, builder) -> builder
                    .langValue("%s Dual Input Hatch".formatted(VNF[tier]))
                    .rotationState(RotationState.ALL)
                    .abilities(DUAL_INPUT_HATCH_ABILITIES)
                    .overlayTieredHullModel(GTCEu.id("block/machine/part/dual_input_hatch"))
                    .tooltips(
                            Component.translatable("gtceu.machine.dual_hatch.import.tooltip"),
                            Component.translatable(
                                    "gtceu.universal.tooltip.item_storage_capacity",
                                    (int) Math.pow(tier, 2)),
                            Component.translatable(
                                    "gtceu.universal.tooltip.fluid_storage_capacity_mult",
                                    tier,
                                    DualHatchPartMachine.getTankCapacity(DualHatchPartMachine.INITIAL_TANK_CAPACITY,
                                            tier)),
                            Component.translatable("gtceu.part_sharing.enabled"))
                    .tooltips(SFTTooltipsBuilder.of().modifiedBySFT().list())
                    .register(),
            GTValues.tiersBetween(LV, IV));

    public static final MachineDefinition[] DUAL_EXPORT_HATCH = registerTieredMachines(
            REGISTRATE,
            "dual_output_hatch",
            (holder, tier) -> new DualHatchPartMachine(holder, tier, OUT),
            (tier, builder) -> builder
                    .langValue("%s Dual Output Hatch".formatted(VNF[tier]))
                    .rotationState(RotationState.ALL)
                    .abilities(DUAL_OUTPUT_HATCH_ABILITIES)
                    .overlayTieredHullModel(GTCEu.id("block/machine/part/dual_output_hatch"))
                    .tooltips(
                            Component.translatable("gtceu.machine.dual_hatch.export.tooltip"),
                            Component.translatable(
                                    "gtceu.universal.tooltip.item_storage_capacity",
                                    (int) Math.pow(tier, 2)),
                            Component.translatable(
                                    "gtceu.universal.tooltip.fluid_storage_capacity_mult",
                                    tier,
                                    DualHatchPartMachine.getTankCapacity(
                                            DualHatchPartMachine.INITIAL_TANK_CAPACITY, tier)))
                    .tooltips(SFTTooltipsBuilder.of().enableSharing().modifiedBySFT().list())
                    .register(),
            GTValues.tiersBetween(LV, IV));

    public static void init() {
    }
}
