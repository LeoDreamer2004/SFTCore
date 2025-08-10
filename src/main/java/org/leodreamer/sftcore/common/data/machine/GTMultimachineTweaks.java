package org.leodreamer.sftcore.common.data.machine;

import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifierList;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import net.minecraft.network.chat.Component;

import static com.gregtechceu.gtceu.common.data.machines.GCYMMachines.*;
import static com.gregtechceu.gtceu.common.data.machines.GTMultiMachines.*;
import static org.leodreamer.sftcore.common.data.recipe.SFTRecipeModifiers.*;

public class GTMultimachineTweaks {

    public static void init() {
        ParallelHatchTweaks();
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
                                components.remove(components.size() - 1); // sharing disabled
                                components.add(Component.translatable("gtceu.part_sharing.enabled"));
                                components.add(Component.translatable("sftcore.machine.modified_by_sft"));
                            }
                    )
            );
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
                    machine.getTooltipBuilder().andThen((stack, components) -> {
                        components.add(
                                Component.translatable("sftcore.multiblock.half_perfect_overclock.tooltip")
                        );
                        components.add(
                                Component.translatable("sftcore.multiblock.half_perfect_overclock.tooltip.1")
                        );
                        components.add(
                                Component.translatable("sftcore.machine.modified_by_sft")
                        );
                    })
            );
        }

    }

    public static void GTPerfectTweaks() {
        LARGE_CHEMICAL_REACTOR.setTooltipBuilder(
                LARGE_CHEMICAL_REACTOR.getTooltipBuilder().andThen((stack, components) -> {
                    components.add(
                            Component.translatable("sftcore.multiblock.perfect_overclock.tooltip")
                    );
                    components.add(
                            Component.translatable("sftcore.multiblock.perfect_overclock.tooltip.1")
                    );
                })
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
                            OC_HALF_PERFECT_SUBTICK,
                            GCYM_MACHINE_REDUCE,
                            GTRecipeModifiers.PARALLEL_HATCH,
                            GTRecipeModifiers.BATCH_MODE
                    )
            );

            machine.setTooltipBuilder(
                    machine.getTooltipBuilder().andThen((stack, components) -> {
                        components.add(
                                Component.translatable("sftcore.multiblock.energy_multiplier.tooltip", GCYM_EUT_MULTIPLIER)
                        );
                        components.add(
                                Component.translatable("sftcore.multiblock.time_multiplier.tooltip", GCYM_DURATION_MULTIPLIER)
                        );
                        components.add(
                                Component.translatable("sftcore.multiblock.half_perfect_overclock.tooltip")
                        );
                        components.add(
                                Component.translatable("sftcore.multiblock.half_perfect_overclock.tooltip.1")
                        );
                        components.add(
                                Component.translatable("sftcore.machine.modified_by_sft")
                        );
                    })
            );
        }
    }

    public static void GTMegaTweaks() {
        MEGA_BLAST_FURNACE.setRecipeModifier(
                new RecipeModifierList(
                        GTRecipeModifiers.PARALLEL_HATCH,
                        GTRecipeModifiers.BATCH_MODE,
                        GTRecipeModifiers::ebfOverclock,
                        GCYM_MACHINE_REDUCE,
                        MEGA_COIL_MACHINE_REDUCE
                )
        );

        MEGA_BLAST_FURNACE.setTooltipBuilder(
                MEGA_BLAST_FURNACE.getTooltipBuilder().andThen((stack, components) -> {
                    components.add(
                            Component.translatable("sftcore.multiblock.energy_multiplier.tooltip", GCYM_EUT_MULTIPLIER)
                    );
                    components.add(
                            Component.translatable("sftcore.multiblock.time_multiplier.tooltip", GCYM_DURATION_MULTIPLIER)
                    );
                    components.add(
                            Component.translatable("sftcore.multiblock.mega_reduce_with_coil")
                    );
                    components.add(
                            Component.translatable("sftcore.multiblock.mega_reduce_with_coil.1")
                    );
                    components.add(
                            Component.translatable("sftcore.machine.modified_by_sft")
                    );
                })
        );
    }
}
