package org.leodreamer.sftcore.common.data.machine;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifierList;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.gregtechceu.gtceu.common.machine.multiblock.part.DualHatchPartMachine;
import net.minecraft.network.chat.Component;
import org.leodreamer.sftcore.api.registry.SFTTooltipsBuilder;

import static com.gregtechceu.gtceu.common.data.GTMachines.DUAL_EXPORT_HATCH;
import static com.gregtechceu.gtceu.common.data.GTMachines.DUAL_IMPORT_HATCH;
import static com.gregtechceu.gtceu.common.data.machines.GCYMMachines.*;
import static com.gregtechceu.gtceu.common.data.machines.GTMultiMachines.*;
import static org.leodreamer.sftcore.common.data.recipe.SFTRecipeModifiers.*;

public class GTMultimachineTweaks {

    public static void init() {
        ParallelHatchTweaks();
        DualHatchTweaks();
        GTMultiTweaks();
        GTPerfectTweaks();
        GCYMTweaks();
        GTMegaTweaks();
    }

    public static void ParallelHatchTweaks() {
        for (var hatch : PARALLEL_HATCH) {
            // modified by mixin
            if (hatch == null) continue;
            hatch.setTooltipBuilder(
                    hatch.getTooltipBuilder().andThen((itemStack, components) -> {
                                components.removeLast();
                                components.addAll(SFTTooltipsBuilder.of().enableSharing().modifiedBySFT().list());
                            }
                    )
            );
        }
    }

    public static void DualHatchTweaks() {
        for (int tier : GTValues.ALL_TIERS) {
            // mixin
            for (var hatch : new MachineDefinition[]{DUAL_IMPORT_HATCH[tier], DUAL_EXPORT_HATCH[tier]}) {
                if (hatch == null) continue;
                hatch.setTooltipBuilder(
                        hatch.getTooltipBuilder().andThen((itemStack, components) -> {
                                    var shareEnabled = components.removeLast();
                                    components.removeLast(); // fluid storage
                                    components.removeLast(); // item storage
                                    components.add(Component.translatable(
                                            "gtceu.universal.tooltip.item_storage_capacity",
                                            (int) Math.pow(tier, 2)));
                                    components.add(Component.translatable(
                                            "gtceu.universal.tooltip.fluid_storage_capacity_mult",
                                            tier,
                                            DualHatchPartMachine.getTankCapacity(DualHatchPartMachine.INITIAL_TANK_CAPACITY,
                                                    tier)));
                                    components.add(shareEnabled);
                                    components.addAll(SFTTooltipsBuilder.of().modifiedBySFT().list());
                                }
                        )
                );
            }
        }
    }

    public static void GTMultiTweaks() {
        MachineDefinition[] machines = new MachineDefinition[]{
                ASSEMBLY_LINE,
                IMPLOSION_COMPRESSOR,
                PYROLYSE_OVEN,
                VACUUM_FREEZER
        };

        for (var machine : machines) {
            machine.setRecipeModifier(
                    new RecipeModifierList(
                            OC_HALF_PERFECT,
                            GTRecipeModifiers.BATCH_MODE
                    )
            );

            machine.setTooltipBuilder(
                    machine.getTooltipBuilder().andThen((stack, components) -> components.addAll(SFTTooltipsBuilder.of().halfPerfectOverlock().list()))
            );
        }

    }

    public static void GTPerfectTweaks() {
        LARGE_CHEMICAL_REACTOR.setTooltipBuilder(
                LARGE_CHEMICAL_REACTOR.getTooltipBuilder().andThen((stack, components) -> components.addAll(SFTTooltipsBuilder.of().perfectOverlock().list()))
        );
    }

    public static void GCYMTweaks() {
        MachineDefinition[] machines = new MachineDefinition[]{
                LARGE_ARC_SMELTER,
                LARGE_ASSEMBLER,
                LARGE_AUTOCLAVE,
                LARGE_BREWER,
                LARGE_CENTRIFUGE,
                LARGE_CHEMICAL_BATH,
                LARGE_CIRCUIT_ASSEMBLER,
                LARGE_CUTTER,
                LARGE_DISTILLERY,
                LARGE_ELECTROLYZER,
                LARGE_ELECTROMAGNET,
                LARGE_ENGRAVING_LASER,
                LARGE_EXTRACTOR,
                LARGE_EXTRUDER,
                LARGE_MACERATION_TOWER,
                LARGE_MATERIAL_PRESS,
                LARGE_MIXER,
                LARGE_PACKER,
                LARGE_SIFTING_FUNNEL,
                LARGE_SOLIDIFIER,
                LARGE_WIREMILL,
                MEGA_VACUUM_FREEZER,
        };

        for (var machine : machines) {
            machine.setRecipeModifier(
                    new RecipeModifierList(
                            GTRecipeModifiers.PARALLEL_HATCH,
                            OC_HALF_PERFECT_SUBTICK,
                            GCYM_MACHINE_REDUCE,
                            GTRecipeModifiers.BATCH_MODE
                    )
            );

            machine.setTooltipBuilder(
                    machine.getTooltipBuilder().andThen((stack, components) -> components.addAll(SFTTooltipsBuilder.of()
                            .energyMultiplier(GCYM_EUT_MULTIPLIER)
                            .timeMultiplier(GCYM_DURATION_MULTIPLIER)
                            .halfPerfectOverlock()
                            .modifiedBySFT().list()))
            );
        }
    }

    public static void GTMegaTweaks() {
        MEGA_BLAST_FURNACE.setRecipeModifier(
                new RecipeModifierList(
                        GTRecipeModifiers.PARALLEL_HATCH,
                        GTRecipeModifiers::ebfOverclock,
                        GCYM_MACHINE_REDUCE,
                        MEGA_COIL_MACHINE_REDUCE,
                        GTRecipeModifiers.BATCH_MODE
                )
        );

        MEGA_BLAST_FURNACE.setTooltipBuilder(
                MEGA_BLAST_FURNACE.getTooltipBuilder().andThen((stack, components) -> components.addAll(SFTTooltipsBuilder.of()
                        .energyMultiplier(GCYM_EUT_MULTIPLIER)
                        .timeMultiplier(GCYM_DURATION_MULTIPLIER)
                        .megaReduceWithCoil()
                        .modifiedBySFT().list()
                ))
        );
    }
}
