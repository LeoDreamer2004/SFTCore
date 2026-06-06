package org.leodreamer.sftcore.common.item.terminal.builder.impl;

import org.leodreamer.sftcore.common.item.terminal.MekTerminalTags;

import net.minecraft.nbt.CompoundTag;

public final class ThermalBoilerConfig {

    public static final int MIN_WIDTH = 3;
    public static final int MAX_WIDTH = 18;

    public static final int MIN_DEPTH = 3;
    public static final int MAX_DEPTH = 18;

    public static final int MIN_HEIGHT = 5;
    public static final int MAX_HEIGHT = 18;

    public static final int MIN_LOWER_HEIGHT = 1;

    public static final int DEFAULT_WIDTH = 5;
    public static final int DEFAULT_HEIGHT = 6;
    public static final int DEFAULT_DEPTH = 5;
    public static final int DEFAULT_LOWER_HEIGHT = 2;

    private final CompoundTag tag;

    private ThermalBoilerConfig(CompoundTag tag) {
        this.tag = tag;
        ensureDefaults();
    }

    public static ThermalBoilerConfig resolve(CompoundTag terminalTag) {
        var config = terminalTag.getCompound(MekTerminalTags.THERMAL_BOILER);

        terminalTag.put(
            MekTerminalTags.THERMAL_BOILER,
            config
        );

        return new ThermalBoilerConfig(config);
    }

    public int getWidth() {
        return clamp(
            tag.getInt(MekTerminalTags.THERMAL_BOILER_WIDTH),
            MIN_WIDTH,
            MAX_WIDTH
        );
    }

    public void setWidth(int width) {
        tag.putInt(
            MekTerminalTags.THERMAL_BOILER_WIDTH,
            clamp(width, MIN_WIDTH, MAX_WIDTH)
        );
    }

    public int getHeight() {
        return clamp(
            tag.getInt(MekTerminalTags.THERMAL_BOILER_HEIGHT),
            MIN_HEIGHT,
            MAX_HEIGHT
        );
    }

    public void setHeight(int height) {
        tag.putInt(
            MekTerminalTags.THERMAL_BOILER_HEIGHT,
            clamp(height, MIN_HEIGHT, MAX_HEIGHT)
        );

        setLowerHeight(getLowerHeight());
    }

    public int getDepth() {
        return clamp(
            tag.getInt(MekTerminalTags.THERMAL_BOILER_DEPTH),
            MIN_DEPTH,
            MAX_DEPTH
        );
    }

    public void setDepth(int depth) {
        tag.putInt(
            MekTerminalTags.THERMAL_BOILER_DEPTH,
            clamp(depth, MIN_DEPTH, MAX_DEPTH)
        );
    }

    public int getLowerHeight() {
        return clamp(
            tag.getInt(MekTerminalTags.THERMAL_BOILER_LOWER_HEIGHT),
            MIN_LOWER_HEIGHT,
            maxLowerHeight()
        );
    }

    public void setLowerHeight(int lowerHeight) {
        tag.putInt(
            MekTerminalTags.THERMAL_BOILER_LOWER_HEIGHT,
            clamp(
                lowerHeight,
                MIN_LOWER_HEIGHT,
                maxLowerHeight()
            )
        );
    }

    public int getDisperserY() {
        return getLowerHeight() + 1;
    }

    private int maxLowerHeight() {
        return getHeight() - 4;
    }

    private void ensureDefaults() {
        if (!tag.contains(MekTerminalTags.THERMAL_BOILER_WIDTH)) {
            tag.putInt(
                MekTerminalTags.THERMAL_BOILER_WIDTH,
                DEFAULT_WIDTH
            );
        }

        if (!tag.contains(MekTerminalTags.THERMAL_BOILER_HEIGHT)) {
            tag.putInt(
                MekTerminalTags.THERMAL_BOILER_HEIGHT,
                DEFAULT_HEIGHT
            );
        }

        if (!tag.contains(MekTerminalTags.THERMAL_BOILER_DEPTH)) {
            tag.putInt(
                MekTerminalTags.THERMAL_BOILER_DEPTH,
                DEFAULT_DEPTH
            );
        }

        if (!tag.contains(MekTerminalTags.THERMAL_BOILER_LOWER_HEIGHT)) {
            tag.putInt(
                MekTerminalTags.THERMAL_BOILER_LOWER_HEIGHT,
                DEFAULT_LOWER_HEIGHT
            );
        }

        setWidth(getWidth());
        setHeight(getHeight());
        setDepth(getDepth());
        setLowerHeight(getLowerHeight());
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
