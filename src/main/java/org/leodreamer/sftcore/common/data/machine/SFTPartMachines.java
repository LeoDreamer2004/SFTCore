package org.leodreamer.sftcore.common.data.machine;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.client.renderer.KineticPartMachineRenderer;
import org.leodreamer.sftcore.common.block.KineticMachineBlock;
import org.leodreamer.sftcore.common.data.lang.SFTTooltipsBuilder;
import org.leodreamer.sftcore.common.machine.multiblock.SFTPartAbility;
import org.leodreamer.sftcore.common.machine.multiblock.part.*;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.item.MetaMachineItem;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.CleanroomType;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties;
import com.gregtechceu.gtceu.common.data.models.GTMachineModels;
import com.gregtechceu.gtceu.common.machine.multiblock.part.DualHatchPartMachine;
import com.gregtechceu.gtceu.utils.FormattingUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.ArrayList;
import java.util.Locale;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties.IS_FORMED;
import static com.gregtechceu.gtceu.common.data.machines.GTMachineUtils.*;
import static com.gregtechceu.gtceu.common.data.machines.GTMachineUtils.ALL_TIERS;
import static org.leodreamer.sftcore.SFTCore.REGISTRATE;
import static org.leodreamer.sftcore.common.machine.multiblock.part.GasHatchPartMachine.*;

public final class SFTPartMachines {

    public static final MachineDefinition WILDCARD_ME_PATTERN_BUFFER_HATCH = REGISTRATE
        .machine("wildcard_me_pattern_buffer_hatch", WildcardMEPatternBufferPartMachine::new)
        .langValue("Wildcard ME Pattern Buffer")
        .tier(LuV)
        .rotationState(RotationState.ALL)
        .abilities(PartAbility.IMPORT_ITEMS, PartAbility.IMPORT_FLUIDS)
        .colorOverlayTieredHullModel(
            SFTCore.id("block/overlay/machine/overlay_wildcard_me_buffer_hatch")
        )
        .tooltips(
            builder -> SFTTooltipsBuilder.machine(builder.id)
                .tip("Pattern buffer with wildcard pattern support")
                .intro(
                    component -> component.withStyle(ChatFormatting.GOLD),
                    "- Can put wildcard patterns to generate patterns",
                    "- Same as the pattern buffer, each recipe slot is independent",
                    "- Also optimized with recipe cache"
                )
                .enableSharing()
                .textureComeFrom("GregTech Odyssey")
        )
        .allowCoverOnFront(true)
        .register();

    public static final MachineDefinition CONFIGURABLE_AUTO_MAINTENANCE_HATCH = REGISTRATE
        .machine(
            "configurable_auto_maintenance_hatch",
            ConfigurableAutoMaintenanceHatchPartMachine::new
        )
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
            info -> new ConfigurableCleaningMaintenanceHatchPartMachine(
                info, CleanroomType.CLEANROOM
            )
        )
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
            "overlay_machine_in_emissive", null, "overlay_machine_holder"
        )
        .tier(LV)
        .allowCoverOnFront(true)
        .register();

    // add the dual hatch omitted in GTM
    public static final MachineDefinition[] DUAL_IMPORT_HATCH = registerTieredMachines(
        REGISTRATE,
        "dual_input_hatch",
        (info, tier) -> new DualHatchPartMachine(info, tier, IO.IN),
        (tier, builder) -> builder
            .langValue("%s Dual Input Hatch".formatted(VNF[tier]))
            .rotationState(RotationState.ALL)
            .abilities(DUAL_INPUT_HATCH_ABILITIES)
            .modelProperty(GTMachineModelProperties.IS_FORMED, false)
            .overlayTieredHullModel(GTCEu.id("block/machine/part/dual_input_hatch"))
            .tooltips(
                Component.translatable("gtceu.machine.dual_hatch.import.tooltip"),
                Component.translatable(
                    "gtceu.universal.tooltip.item_storage_capacity", (int) Math.pow(tier, 2)
                ),
                Component.translatable(
                    "gtceu.universal.tooltip.fluid_storage_capacity_mult",
                    tier,
                    DualHatchPartMachine.getTankCapacity(
                        DualHatchPartMachine.INITIAL_TANK_CAPACITY, tier
                    )
                ),
                Component.translatable("gtceu.part_sharing.enabled")
            )
            .tooltips(SFTTooltipsBuilder.of().modifiedBySFT().list())
            .register(),
        tiersBetween(LV, IV)
    );

    public static final MachineDefinition[] DUAL_EXPORT_HATCH = registerTieredMachines(
        REGISTRATE,
        "dual_output_hatch",
        (info, tier) -> new DualHatchPartMachine(info, tier, IO.OUT),
        (tier, builder) -> builder
            .langValue("%s Dual Output Hatch".formatted(VNF[tier]))
            .rotationState(RotationState.ALL)
            .abilities(DUAL_OUTPUT_HATCH_ABILITIES)
            .modelProperty(GTMachineModelProperties.IS_FORMED, false)
            .overlayTieredHullModel(GTCEu.id("block/machine/part/dual_output_hatch"))
            .tooltips(
                Component.translatable("gtceu.machine.dual_hatch.export.tooltip"),
                Component.translatable(
                    "gtceu.universal.tooltip.item_storage_capacity", (int) Math.pow(tier, 2)
                ),
                Component.translatable(
                    "gtceu.universal.tooltip.fluid_storage_capacity_mult",
                    tier,
                    DualHatchPartMachine.getTankCapacity(
                        DualHatchPartMachine.INITIAL_TANK_CAPACITY, tier
                    )
                )
            )
            .tooltips(SFTTooltipsBuilder.of().enableSharing().modifiedBySFT().list())
            .register(),
        tiersBetween(LV, IV)
    );

    public static final MachineDefinition ME_ADVANCED_INPUT_BUS = REGISTRATE
        .machine("me_advanced_input_bus", MEAdvancedInputBusPartMachine::new)
        .rotationState(RotationState.ALL)
        .abilities(PartAbility.IMPORT_ITEMS)
        .colorOverlayTieredHullModel(GTCEu.id("block/overlay/appeng/me_input_bus"))
        .tier(IV)
        .allowCoverOnFront(true)
        .register();

    public static final MachineDefinition[] KINETIC_INPUT_BOX = registerKineticInputBoxes();

    private static MachineDefinition[] registerKineticInputBoxes() {
        var definitions = new MachineDefinition[TIER_COUNT];

        for (int tier : tiersBetween(LV, EV)) {
            String tierName = VN[tier].toLowerCase(Locale.ROOT);
            String id = tierName + "_kinetic_input_box";

            definitions[tier] = REGISTRATE.machine(
                id,
                MachineDefinition::new,
                KineticMachineBlock::new,
                MetaMachineItem::new,
                info -> new KineticPartMachine(info, tier, IO.IN)
            )
                .langValue("%s Kinetic Input Box".formatted(VNF[tier]))
                .rotationState(RotationState.ALL)
                .abilities(SFTPartAbility.INPUT_KINETIC)
                .tier(tier)
                .blockProp(BlockBehaviour.Properties::dynamicShape)
                .blockProp(BlockBehaviour.Properties::noOcclusion)
                .model((ctx, prov, builder) -> {
                    var parentModel = prov.models()
                        .getExistingFile(SFTCore.id("block/machine/kinetic_electric_machine"));

                    var model = prov.models().nested().parent(parentModel);
                    GTMachineModels.casingTextures(model, GTCEu.id("block/casings/voltage/" + tierName));
                    builder.forAllStatesModels(state -> model);
                })
                .hasBER(true)
                .onBlockEntityRegister(type -> {
                    if (GTCEu.isClientSide()) {
                        BlockEntityRenderers.register(
                            type, KineticPartMachineRenderer::new
                        );
                    }
                })
                .register();
        }

        return definitions;
    }

    public static final MachineDefinition[] GAS_IMPORT_HATCH = registerGasHatches(
        "gas_import_hatch",
        "Gas Input Hatch",
        IO.IN,
        GasHatchPartMachine.INITIAL_TANK_CAPACITY_1X,
        1,
        SFTPartAbility.IMPORT_GASES,
        SFTPartAbility.IMPORT_GASES_1X,
        ALL_TIERS
    );

    public static final MachineDefinition[] GAS_EXPORT_HATCH = registerGasHatches(
        "gas_export_hatch",
        "Gas Output Hatch",
        IO.OUT,
        GasHatchPartMachine.INITIAL_TANK_CAPACITY_1X,
        1,
        SFTPartAbility.EXPORT_GASES,
        SFTPartAbility.EXPORT_GASES_1X,
        ALL_TIERS
    );

    public static final MachineDefinition[] GAS_IMPORT_HATCH_4X = registerGasHatches(
        "gas_import_hatch_4x",
        "Quadruple Gas Input Hatch",
        IO.IN,
        GasHatchPartMachine.INITIAL_TANK_CAPACITY_4X,
        4,
        SFTPartAbility.IMPORT_GASES,
        SFTPartAbility.IMPORT_GASES_4X,
        MULTI_HATCH_TIERS
    );

    public static final MachineDefinition[] GAS_EXPORT_HATCH_4X = registerGasHatches(
        "gas_export_hatch_4x",
        "Quadruple Gas Output Hatch",
        IO.OUT,
        GasHatchPartMachine.INITIAL_TANK_CAPACITY_4X,
        4,
        SFTPartAbility.EXPORT_GASES,
        SFTPartAbility.EXPORT_GASES_4X,
        MULTI_HATCH_TIERS
    );

    public static final MachineDefinition[] GAS_IMPORT_HATCH_9X = registerGasHatches(
        "gas_import_hatch_9x",
        "Nonuple Gas Input Hatch",
        IO.IN,
        GasHatchPartMachine.INITIAL_TANK_CAPACITY_9X,
        9,
        SFTPartAbility.IMPORT_GASES,
        SFTPartAbility.IMPORT_GASES_9X,
        MULTI_HATCH_TIERS
    );

    public static final MachineDefinition[] GAS_EXPORT_HATCH_9X = registerGasHatches(
        "gas_export_hatch_9x",
        "Nonuple Gas Output Hatch",
        IO.OUT,
        GasHatchPartMachine.INITIAL_TANK_CAPACITY_9X,
        9,
        SFTPartAbility.EXPORT_GASES,
        SFTPartAbility.EXPORT_GASES_9X,
        MULTI_HATCH_TIERS
    );

    public static final MachineDefinition ME_GAS_OUTPUT_HATCH = REGISTRATE
        .machine("me_gas_output_hatch", MEGasOutputHatchPartMachine::new)
        .tier(EV)
        .langValue("ME Gas Output Hatch")
        .rotationState(RotationState.ALL)
        .abilities(SFTPartAbility.EXPORT_GASES)
        .colorOverlayTieredHullModel(GTCEu.id("block/overlay/appeng/me_output_bus"))
        .register();

    public static final MachineDefinition ME_GAS_INPUT_HATCH = REGISTRATE
        .machine("me_gas_input_hatch", MEGasInputHatchPartMachine::new)
        .tier(EV)
        .langValue("ME Gas Input Hatch")
        .rotationState(RotationState.ALL)
        .abilities(SFTPartAbility.IMPORT_GASES)
        .colorOverlayTieredHullModel(GTCEu.id("block/overlay/appeng/me_input_bus"))
        .allowCoverOnFront(true)
        .register();

    public static final MachineDefinition ME_GAS_STOCKING_INPUT_HATCH = REGISTRATE
        .machine("me_gas_stocking_input_hatch", MEGasStockingInputHatchPartMachine::new)
        .tier(EV)
        .langValue("ME Gas Stocking Input Hatch")
        .rotationState(RotationState.ALL)
        .abilities(SFTPartAbility.IMPORT_GASES)
        .colorOverlayTieredHullModel(GTCEu.id("block/overlay/appeng/me_input_bus"))
        .allowCoverOnFront(true)
        .register();

    private static MachineDefinition[] registerGasHatches(
        String name,
        String lang,
        IO io,
        long initialCapacity,
        int slots,
        PartAbility commonAbility,
        PartAbility exactAbility,
        int... tiers
    ) {
        final ResourceLocation pipeOverlay;
        if (slots >= 9) {
            pipeOverlay = GTCEu.id("block/overlay/machine/overlay_pipe_9x");
        } else if (slots >= 4) {
            pipeOverlay = GTCEu.id("block/overlay/machine/overlay_pipe_4x");
        } else {
            pipeOverlay = null;
        }
        final var ioOverlay = SFTCore.id(
            "block/overlay/machine/" +
                (io == IO.OUT ? "overlay_gas_hatch_output" : "overlay_gas_hatch_input")
        );
        final var emissiveOverlay = GTCEu.id(
            "block/overlay/machine/" +
                (io == IO.OUT ? "overlay_pipe_out_emissive" : "overlay_pipe_in_emissive")
        );

        return registerTieredMachines(
            REGISTRATE,
            name,
            (holder, tier) -> new GasHatchPartMachine(holder, tier, io, initialCapacity, slots),
            (tier, builder) -> {
                var tooltips = new ArrayList<Component>();
                tooltips.add(Component.translatable(io == IO.IN ? IMPORT_TOOLTIP : EXPORT_TOOLTIP));
                long capacity = getTankCapacity(initialCapacity, tier);
                if (slots == 1) {
                    tooltips.add(
                        Component.translatable(
                            GAS_CAPACITY, FormattingUtil
                                .formatNumbers(capacity)
                        )
                    );
                } else {
                    tooltips.add(
                        Component.translatable(
                            GAS_CAPACITY_MULTI, slots, FormattingUtil
                                .formatNumbers(capacity)
                        )
                    );
                }

                return builder
                    .langValue("%s %s".formatted(VNF[tier], lang))
                    .rotationState(RotationState.ALL)
                    .abilities(commonAbility, exactAbility)
                    .colorOverlayTieredHullModel(ioOverlay, pipeOverlay, emissiveOverlay)
                    .modelProperty(IS_FORMED, false)
                    .allowCoverOnFront(true)
                    .tooltips(tooltips)
                    .register();
            },
            tiers
        );
    }

    public static void init() {}
}
