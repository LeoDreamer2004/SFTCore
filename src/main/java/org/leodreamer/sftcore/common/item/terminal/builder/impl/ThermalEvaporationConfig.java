package org.leodreamer.sftcore.common.item.terminal.builder.impl;

import org.leodreamer.sftcore.common.item.terminal.MekTerminalTags;

import net.minecraft.nbt.CompoundTag;

public final class ThermalEvaporationConfig {

    public static final int WIDTH = 4;
    public static final int DEPTH = 4;

    public static final int MIN_HEIGHT = 3;
    public static final int MAX_HEIGHT = 18;
    public static final int DEFAULT_HEIGHT = 4;

    private final CompoundTag tag;

    private ThermalEvaporationConfig(CompoundTag tag) {
        this.tag = tag;
        ensureDefaults();
    }

    public static ThermalEvaporationConfig resolve(CompoundTag terminalTag) {
        var config = terminalTag.getCompound(MekTerminalTags.THERMAL_EVAPORATION);
        terminalTag.put(MekTerminalTags.THERMAL_EVAPORATION, config);
        return new ThermalEvaporationConfig(config);
    }

    public int getHeight() {
        return clampHeight(tag.getInt(MekTerminalTags.THERMAL_EVAPORATION_HEIGHT));
    }

    public void setHeight(int height) {
        tag.putInt(MekTerminalTags.THERMAL_EVAPORATION_HEIGHT, clampHeight(height));
    }

    private void ensureDefaults() {
        if (!tag.contains(MekTerminalTags.THERMAL_EVAPORATION_HEIGHT)) {
            tag.putInt(MekTerminalTags.THERMAL_EVAPORATION_HEIGHT, DEFAULT_HEIGHT);
        }

        setHeight(getHeight());
    }

    private int clampHeight(int value) {
        return Math.max(MIN_HEIGHT, Math.min(MAX_HEIGHT, value));
    }
}
