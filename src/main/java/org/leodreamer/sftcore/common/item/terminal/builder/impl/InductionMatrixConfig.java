package org.leodreamer.sftcore.common.item.terminal.builder.impl;

import org.leodreamer.sftcore.common.item.terminal.MekTerminalTags;

import net.minecraft.nbt.CompoundTag;

public final class InductionMatrixConfig {

    public static final int MIN_SIZE = 3;
    public static final int MAX_SIZE = 18;
    public static final int DEFAULT_SIZE = 5;

    private final CompoundTag tag;

    private InductionMatrixConfig(CompoundTag tag) {
        this.tag = tag;
        ensureDefaults();
    }

    public static InductionMatrixConfig resolve(CompoundTag terminalTag) {
        var config = terminalTag.getCompound(MekTerminalTags.INDUCTION);
        var result = new InductionMatrixConfig(config);
        terminalTag.put(MekTerminalTags.INDUCTION, config);
        return result;
    }

    public int getWidth() {
        return clamp(tag.getInt(MekTerminalTags.INDUCTION_WIDTH));
    }

    public void setWidth(int value) {
        tag.putInt(MekTerminalTags.INDUCTION_WIDTH, clamp(value));
    }

    public int getHeight() {
        return clamp(tag.getInt(MekTerminalTags.INDUCTION_HEIGHT));
    }

    public void setHeight(int value) {
        tag.putInt(MekTerminalTags.INDUCTION_HEIGHT, clamp(value));
    }

    public int getDepth() {
        return clamp(tag.getInt(MekTerminalTags.INDUCTION_DEPTH));
    }

    public void setDepth(int value) {
        tag.putInt(MekTerminalTags.INDUCTION_DEPTH, clamp(value));
    }

    private void ensureDefaults() {
        if (!tag.contains(MekTerminalTags.INDUCTION_WIDTH)) {
            tag.putInt(MekTerminalTags.INDUCTION_WIDTH, DEFAULT_SIZE);
        }
        if (!tag.contains(MekTerminalTags.INDUCTION_HEIGHT)) {
            tag.putInt(MekTerminalTags.INDUCTION_HEIGHT, DEFAULT_SIZE);
        }
        if (!tag.contains(MekTerminalTags.INDUCTION_DEPTH)) {
            tag.putInt(MekTerminalTags.INDUCTION_DEPTH, DEFAULT_SIZE);
        }
    }

    private static int clamp(int value) {
        return Math.max(MIN_SIZE, Math.min(MAX_SIZE, value));
    }
}
