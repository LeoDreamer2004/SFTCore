package org.leodreamer.sftcore.common.data.machine;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.api.block.KineticMachineBlock;
import org.leodreamer.sftcore.api.blockentity.KineticMachineBlockEntity;
import org.leodreamer.sftcore.api.machine.KineticMachineDefinition;
import org.leodreamer.sftcore.api.machine.multiblock.part.KineticPartMachine;
import org.leodreamer.sftcore.api.registry.SFTTooltipsBuilder;
import org.leodreamer.sftcore.common.data.models.SFTMachineModels;
import org.leodreamer.sftcore.common.machine.multiblock.SFTPartAbility;
import org.leodreamer.sftcore.common.machine.multiblock.part.ConfigurableAutoMaintenanceHatchPartMachine;
import org.leodreamer.sftcore.common.machine.multiblock.part.ConfigurableCleaningMaintenanceHatchPartMachine;
import org.leodreamer.sftcore.common.machine.multiblock.part.MachineAdjustmentHatchPartMachine;
import org.leodreamer.sftcore.common.visual.SplitShaftVisual;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.item.MetaMachineItem;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.CleanroomType;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.registry.registrate.MachineBuilder;
import com.gregtechceu.gtceu.common.machine.multiblock.part.DualHatchPartMachine;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockBehaviour;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;

import java.util.Locale;
import java.util.function.BiFunction;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.capability.recipe.IO.IN;
import static com.gregtechceu.gtceu.api.capability.recipe.IO.OUT;
import static com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties.IS_FORMED;
import static com.gregtechceu.gtceu.common.data.machines.GTMachineUtils.*;
import static org.leodreamer.sftcore.SFTCore.REGISTRATE;

public final class SFTPartMachines {

    public static final MachineDefinition CONFIGURABLE_AUTO_MAINTENANCE_HATCH = REGISTRATE
            .machine(
                    "configurable_auto_maintenance_hatch",
                    ConfigurableAutoMaintenanceHatchPartMachine::new)
            .rotationState(RotationState.ALL)
            .abilities(PartAbility.MAINTENANCE)
            .modelProperty(IS_FORMED, false)
            .tooltips(builder -> SFTTooltipsBuilder.machine(builder.id).disableSharing())
            .overlayTieredHullModel(GTCEu.id("block/machine/part/auto_maintenance_hatch"))
            .tier(IV)
            .register();

    public static final MachineDefinition CONFIGURABLE_CLEANING_MAINTENANCE_HATCH = REGISTRATE
            .machine(
                    "configurable_cleaning_maintenance_hatch",
                    (holder) -> new ConfigurableCleaningMaintenanceHatchPartMachine(
                            holder, CleanroomType.CLEANROOM))
            .rotationState(RotationState.ALL)
            .abilities(PartAbility.MAINTENANCE)
            .modelProperty(IS_FORMED, false)
            .tooltips(builder -> SFTTooltipsBuilder.machine(builder.id).disableSharing())
            .overlayTieredHullModel(GTCEu.id("block/machine/part/cleaning_maintenance_hatch"))
            .tier(LuV)
            .register();

    public static final MachineDefinition MACHINE_ADJUSTMENT = REGISTRATE
            .machine("machine_adjustment_hatch", MachineAdjustmentHatchPartMachine::new)
            .rotationState(RotationState.ALL)
            .abilities(SFTPartAbility.MACHINE_ADJUSTMENT)
            .colorOverlayTieredHullModel(
                    "overlay_machine_in_emissive", null, "overlay_machine_holder")
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
                                    "gtceu.universal.tooltip.item_storage_capacity", (int) Math.pow(tier, 2)),
                            Component.translatable(
                                    "gtceu.universal.tooltip.fluid_storage_capacity_mult",
                                    tier,
                                    DualHatchPartMachine.getTankCapacity(
                                            DualHatchPartMachine.INITIAL_TANK_CAPACITY, tier)),
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
                                    "gtceu.universal.tooltip.item_storage_capacity", (int) Math.pow(tier, 2)),
                            Component.translatable(
                                    "gtceu.universal.tooltip.fluid_storage_capacity_mult",
                                    tier,
                                    DualHatchPartMachine.getTankCapacity(
                                            DualHatchPartMachine.INITIAL_TANK_CAPACITY, tier)))
                    .tooltips(SFTTooltipsBuilder.of().enableSharing().modifiedBySFT().list())
                    .register(),
            GTValues.tiersBetween(LV, IV));

    public static final KineticMachineDefinition[] KINETIC_INPUT_BOX = registerKineticTieredMachines(
            "kinetic_input_box",
            (tier, id) -> new KineticMachineDefinition(id, false, GTValues.V[tier]).setFrontRotation(true),
            (holder, tier) -> new KineticPartMachine(holder, tier, IO.IN),
            (tier, builder) -> builder
                    .langValue("%s Kinetic Input Box %s".formatted(VLVH[tier], VLVT[tier]))
                    .rotationState(RotationState.ALL)
                    .blockProp(BlockBehaviour.Properties::dynamicShape)
                    .blockProp(BlockBehaviour.Properties::noOcclusion)
                    .abilities(SFTPartAbility.INPUT_KINETIC)
                    .model(
                            SFTMachineModels.createTieredCustomModel(
                                    SFTCore.id("block/machine/part/kinetic_input_box")))
                    .tier(tier)
                    .register(),
            () -> (VisualizationContext var1, KineticMachineBlockEntity var2, float var3) -> new SplitShaftVisual(var1,
                    var2, var3),
            false,
            GTValues.tiersBetween(LV, EV));

    public static KineticMachineDefinition[] registerKineticTieredMachines(
                                                                           String name,
                                                                           BiFunction<Integer, ResourceLocation, KineticMachineDefinition> definitionFactory,
                                                                           BiFunction<IMachineBlockEntity, Integer, MetaMachine> factory,
                                                                           BiFunction<Integer, MachineBuilder<KineticMachineDefinition>, KineticMachineDefinition> builder,
                                                                           NonNullSupplier<SimpleBlockEntityVisualizer.Factory<? extends KineticBlockEntity>> visualFactory,
                                                                           boolean renderNormally,
                                                                           int[] tiers) {
        KineticMachineDefinition[] definitions = new KineticMachineDefinition[GTValues.TIER_COUNT];
        for (int tier : tiers) {
            var register = REGISTRATE
                    .machine(
                            GTValues.VN[tier].toLowerCase(Locale.ROOT) + "_" + name,
                            id -> definitionFactory.apply(tier, id),
                            holder -> factory.apply(holder, tier),
                            KineticMachineBlock::new,
                            MetaMachineItem::new,
                            KineticMachineBlockEntity::create)
                    .tier(tier)
                    .hasBER(visualFactory != null)
                    .onBlockEntityRegister(
                            type -> KineticMachineBlockEntity.onBlockEntityRegister(
                                    type, visualFactory, renderNormally));
            definitions[tier] = builder.apply(tier, register);
        }
        return definitions;
    }

    public static void init() {}
}
