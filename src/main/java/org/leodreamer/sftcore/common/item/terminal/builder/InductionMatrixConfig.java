package org.leodreamer.sftcore.common.item.terminal.builder;

import org.leodreamer.sftcore.common.item.terminal.MekTerminalTags;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public final class InductionMatrixConfig {

    public static final int MIN_SIZE = 3;
    public static final int MAX_SIZE = 18;
    public static final int DEFAULT_SIZE = 5;

    private InductionMatrixConfig() {}

    public static CompoundTag getOrCreate(ItemStack stack) {
        return getOrCreate(stack.getOrCreateTag());
    }

    public static CompoundTag getOrCreate(CompoundTag stackTag) {
        var root = stackTag.getCompound(MekTerminalTags.ROOT);
        var config = root.getCompound(MekTerminalTags.INDUCTION);

        ensureDefaults(config);

        root.put(MekTerminalTags.INDUCTION, config);
        stackTag.put(MekTerminalTags.ROOT, root);

        return config;
    }

    public static int getWidth(CompoundTag config) {
        return clamp(config.getInt(MekTerminalTags.INDUCTION_WIDTH));
    }

    public static int getHeight(CompoundTag config) {
        return clamp(config.getInt(MekTerminalTags.INDUCTION_HEIGHT));
    }

    public static int getDepth(CompoundTag config) {
        return clamp(config.getInt(MekTerminalTags.INDUCTION_DEPTH));
    }

    public static void setDimension(ItemStack stack, String key, int value) {
        var tag = stack.getOrCreateTag();
        var root = tag.getCompound(MekTerminalTags.ROOT);
        var config = root.getCompound(MekTerminalTags.INDUCTION);

        ensureDefaults(config);
        config.putInt(key, clamp(value));

        root.put(MekTerminalTags.INDUCTION, config);
        tag.put(MekTerminalTags.ROOT, root);
    }

    public static int readDimension(ItemStack stack, String key) {
        var config = getOrCreate(stack);
        return clamp(config.getInt(key));
    }

    private static void ensureDefaults(CompoundTag config) {
        if (!config.contains(MekTerminalTags.INDUCTION_WIDTH)) {
            config.putInt(MekTerminalTags.INDUCTION_WIDTH, DEFAULT_SIZE);
        }
        if (!config.contains(MekTerminalTags.INDUCTION_HEIGHT)) {
            config.putInt(MekTerminalTags.INDUCTION_HEIGHT, DEFAULT_SIZE);
        }
        if (!config.contains(MekTerminalTags.INDUCTION_DEPTH)) {
            config.putInt(MekTerminalTags.INDUCTION_DEPTH, DEFAULT_SIZE);
        }
    }

    private static int clamp(int value) {
        return Math.max(MIN_SIZE, Math.min(MAX_SIZE, value));
    }
}
