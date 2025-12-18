package org.leodreamer.sftcore.integration.ponder.misc;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.AEParts;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import gripe._90.megacells.definition.MEGABlocks;
import lombok.Getter;
import mekanism.common.content.blocktype.FactoryType;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.tier.FactoryTier;
import mekanism.generators.common.registries.GeneratorsBlocks;
import mekanism.generators.common.registries.GeneratorsItems;
import net.minecraft.world.level.ItemLike;
import net.pedroksl.advanced_ae.common.definitions.AAEBlocks;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;

import java.util.Arrays;

@Getter
public enum SFTPonderGroup {

    CONTROLLER(
            AEBlocks.CONTROLLER
    ),

    QUANTUM_COMPUTER(
            AAEBlocks.QUANTUM_ACCELERATOR,
            AAEBlocks.QUANTUM_CORE,
            AAEBlocks.QUANTUM_STRUCTURE,
            AAEBlocks.QUANTUM_MULTI_THREADER,
            AAEBlocks.DATA_ENTANGLER,
            AAEBlocks.QUANTUM_STORAGE_128M,
            AAEBlocks.QUANTUM_STORAGE_256M
    ),

    CPU(QUANTUM_COMPUTER.merge(
            AEBlocks.CRAFTING_UNIT,
            AEBlocks.CRAFTING_MONITOR,
            AEBlocks.CRAFTING_ACCELERATOR,
            AEBlocks.CRAFTING_STORAGE_1K,
            AEBlocks.CRAFTING_STORAGE_4K,
            AEBlocks.CRAFTING_STORAGE_16K,
            AEBlocks.CRAFTING_STORAGE_64K,
            AEBlocks.CRAFTING_STORAGE_256K,
            MEGABlocks.MEGA_CRAFTING_UNIT,
            MEGABlocks.CRAFTING_MONITOR,
            MEGABlocks.CRAFTING_ACCELERATOR,
            MEGABlocks.CRAFTING_STORAGE_1M,
            MEGABlocks.CRAFTING_STORAGE_4M,
            MEGABlocks.CRAFTING_STORAGE_16M,
            MEGABlocks.CRAFTING_STORAGE_64M,
            MEGABlocks.CRAFTING_STORAGE_256M
    )),

    PATTERN(
            AEItems.BLANK_PATTERN,
            AEItems.CRAFTING_PATTERN,
            AEItems.PROCESSING_PATTERN,
            AEItems.STONECUTTING_PATTERN,
            AEItems.SMITHING_TABLE_PATTERN,
            AAEItems.ADV_PATTERN_ENCODER
    ),

    PATTERN_PROVIDER(
            AEBlocks.PATTERN_PROVIDER,
            AEParts.PATTERN_PROVIDER,
            EPPItemAndBlock.EX_PATTERN_PROVIDER,
            EPPItemAndBlock.EX_PATTERN_PROVIDER_PART,
            MEGABlocks.MEGA_PATTERN_PROVIDER,
            AAEBlocks.SMALL_ADV_PATTERN_PROVIDER,
            AAEItems.SMALL_ADV_PATTERN_PROVIDER,
            AAEBlocks.ADV_PATTERN_PROVIDER,
            AAEItems.SMALL_ADV_PATTERN_PROVIDER
    ),

    ASSEMBLER_MATRIX(
            EPPItemAndBlock.ASSEMBLER_MATRIX_WALL,
            EPPItemAndBlock.ASSEMBLER_MATRIX_FRAME,
            EPPItemAndBlock.ASSEMBLER_MATRIX_GLASS,
            EPPItemAndBlock.ASSEMBLER_MATRIX_PATTERN,
            EPPItemAndBlock.ASSEMBLER_MATRIX_SPEED,
            EPPItemAndBlock.ASSEMBLER_MATRIX_CRAFTER
    ),

    MOLECULAR_ASSEMBLER(ASSEMBLER_MATRIX.merge(
            AEBlocks.MOLECULAR_ASSEMBLER,
            EPPItemAndBlock.EX_ASSEMBLER
    )),

    INTERFACE(
            AEBlocks.INTERFACE,
            AEParts.INTERFACE,
            EPPItemAndBlock.EX_INTERFACE,
            EPPItemAndBlock.EX_INTERFACE_PART,
            EPPItemAndBlock.OVERSIZE_INTERFACE,
            EPPItemAndBlock.EX_PATTERN_PROVIDER_PART,
            MEGABlocks.MEGA_INTERFACE
    ),

    IMPORT_BUS(
            AEParts.IMPORT_BUS,
            EPPItemAndBlock.EX_IMPORT_BUS
    ),

    EXPORT_BUS(
            AEParts.EXPORT_BUS,
            EPPItemAndBlock.EX_EXPORT_BUS,
            EPPItemAndBlock.MOD_EXPORT_BUS,
            EPPItemAndBlock.TAG_EXPORT_BUS,
            EPPItemAndBlock.PRECISE_EXPORT_BUS,
            EPPItemAndBlock.THRESHOLD_EXPORT_BUS
    ),

    STORAGE_BUS(
            AEParts.STORAGE_BUS,
            EPPItemAndBlock.TAG_STORAGE_BUS,
            EPPItemAndBlock.MOD_STORAGE_BUS,
            EPPItemAndBlock.PRECISE_STORAGE_BUS
    ),

    ANNIHILATION_PLANE(
            AEParts.ANNIHILATION_PLANE
    ),

    FORMATION_PLANE(
            AEParts.FORMATION_PLANE,
            EPPItemAndBlock.ACTIVE_FORMATION_PLANE
    ),

    QUANTUM_BRIDGE(
            AEBlocks.QUANTUM_RING,
            AEBlocks.QUANTUM_LINK
    ),

    IO_PORT(
            AEBlocks.IO_PORT,
            EPPItemAndBlock.EX_IO_PORT
    ),

    INFUSING_FACTORY(
            MekanismBlocks.METALLURGIC_INFUSER,
            MekanismBlocks.getFactory(FactoryTier.BASIC, FactoryType.INFUSING),
            MekanismBlocks.getFactory(FactoryTier.ADVANCED, FactoryType.INFUSING),
            MekanismBlocks.getFactory(FactoryTier.ELITE, FactoryType.INFUSING),
            MekanismBlocks.getFactory(FactoryTier.ULTIMATE, FactoryType.INFUSING)
    ),

    TURBINE(
            GeneratorsBlocks.TURBINE_CASING,
            GeneratorsBlocks.TURBINE_ROTOR,
            GeneratorsBlocks.TURBINE_VENT,
            GeneratorsBlocks.TURBINE_VALVE,
            MekanismBlocks.PRESSURE_DISPERSER,
            GeneratorsItems.TURBINE_BLADE,
            GeneratorsBlocks.ROTATIONAL_COMPLEX,
            GeneratorsBlocks.ELECTROMAGNETIC_COIL,
            MekanismBlocks.STRUCTURAL_GLASS,
            GeneratorsBlocks.SATURATING_CONDENSER
    ),

    FISSION_FACTOR(
            GeneratorsBlocks.FISSION_REACTOR_CASING,
            GeneratorsBlocks.FISSION_REACTOR_PORT,
            GeneratorsBlocks.FISSION_FUEL_ASSEMBLY,
            GeneratorsBlocks.CONTROL_ROD_ASSEMBLY,
            GeneratorsBlocks.FISSION_REACTOR_LOGIC_ADAPTER,
            GeneratorsBlocks.REACTOR_GLASS
    ),

    SPS(
            MekanismBlocks.SPS_CASING,
            MekanismBlocks.SPS_PORT,
            MekanismBlocks.SUPERCHARGED_COIL,
            MekanismBlocks.STRUCTURAL_GLASS
    );

    private final ItemLike[] components;

    SFTPonderGroup(ItemLike... components) {
        this.components = components;
    }

    private ItemLike[] merge(ItemLike... others) {
        return concat(this.components, others);
    }

    private static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}
