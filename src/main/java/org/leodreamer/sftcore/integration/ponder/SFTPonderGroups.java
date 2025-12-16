package org.leodreamer.sftcore.integration.ponder;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.AEParts;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import gripe._90.megacells.definition.MEGABlocks;
import net.minecraft.world.level.ItemLike;
import net.pedroksl.advanced_ae.common.definitions.AAEBlocks;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;

import java.util.Arrays;

public class SFTPonderGroups {
    public static final ItemLike[] QUANTUM_COMPUTER = new ItemLike[]{
            AAEBlocks.QUANTUM_ACCELERATOR,
            AAEBlocks.QUANTUM_CORE,
            AAEBlocks.QUANTUM_STRUCTURE,
            AAEBlocks.QUANTUM_MULTI_THREADER,
            AAEBlocks.DATA_ENTANGLER,
            AAEBlocks.QUANTUM_STORAGE_128M,
            AAEBlocks.QUANTUM_STORAGE_256M
    };

    public static final ItemLike[] CPU = concat(new ItemLike[]{
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
            MEGABlocks.CRAFTING_STORAGE_256M,
    }, QUANTUM_COMPUTER);

    public static final ItemLike[] PATTERN = new ItemLike[]{
            AEItems.BLANK_PATTERN,
            AEItems.CRAFTING_PATTERN,
            AEItems.PROCESSING_PATTERN,
            AEItems.STONECUTTING_PATTERN,
            AEItems.SMITHING_TABLE_PATTERN,
            AAEItems.ADV_PATTERN_ENCODER
    };

    public static final ItemLike[] PATTERN_PROVIDER = new ItemLike[]{
            AEBlocks.PATTERN_PROVIDER,
            AEParts.PATTERN_PROVIDER,
            EPPItemAndBlock.EX_PATTERN_PROVIDER,
            EPPItemAndBlock.EX_PATTERN_PROVIDER_PART,
            MEGABlocks.MEGA_PATTERN_PROVIDER,
            AAEBlocks.SMALL_ADV_PATTERN_PROVIDER,
            AAEItems.SMALL_ADV_PATTERN_PROVIDER,
            AAEBlocks.ADV_PATTERN_PROVIDER,
            AAEItems.SMALL_ADV_PATTERN_PROVIDER
    };

    public static final ItemLike[] ASSEMBLER_MATRIX = new ItemLike[]{
            EPPItemAndBlock.ASSEMBLER_MATRIX_WALL,
            EPPItemAndBlock.ASSEMBLER_MATRIX_FRAME,
            EPPItemAndBlock.ASSEMBLER_MATRIX_GLASS,
            EPPItemAndBlock.ASSEMBLER_MATRIX_PATTERN,
            EPPItemAndBlock.ASSEMBLER_MATRIX_SPEED,
            EPPItemAndBlock.ASSEMBLER_MATRIX_CRAFTER
    };

    public static final ItemLike[] MOLECULAR_ASSEMBLER = concat(new ItemLike[]{
            AEBlocks.MOLECULAR_ASSEMBLER,
            EPPItemAndBlock.EX_ASSEMBLER,
    }, ASSEMBLER_MATRIX);

    public static final ItemLike[] INTERFACE = new ItemLike[]{
            AEBlocks.INTERFACE,
            AEParts.INTERFACE,
            EPPItemAndBlock.EX_INTERFACE,
            EPPItemAndBlock.EX_INTERFACE_PART,
            EPPItemAndBlock.OVERSIZE_INTERFACE,
            EPPItemAndBlock.EX_PATTERN_PROVIDER_PART,
            MEGABlocks.MEGA_INTERFACE
    };

    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}
