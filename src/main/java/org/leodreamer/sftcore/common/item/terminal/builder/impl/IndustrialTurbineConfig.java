package org.leodreamer.sftcore.common.item.terminal.builder.impl;

import org.leodreamer.sftcore.common.item.terminal.MekTerminalTags;

import net.minecraft.nbt.CompoundTag;

public final class IndustrialTurbineConfig {

    public static final int MIN_WIDTH = 5;
    public static final int MAX_WIDTH = 17;

    public static final int MIN_HEIGHT = 5;
    public static final int MAX_HEIGHT = 18;

    public static final int MIN_ROTORS = 1;
    public static final int MAX_ROTORS = 14;

    public static final int DEFAULT_WIDTH = 5;
    public static final int DEFAULT_HEIGHT = 8;
    public static final int DEFAULT_ROTORS = 4;

    private final CompoundTag tag;

    private IndustrialTurbineConfig(CompoundTag tag) {
        this.tag = tag;
        ensureDefaults();
    }

    public static IndustrialTurbineConfig resolve(
        CompoundTag terminalTag
    ) {
        var config = terminalTag.getCompound(
            MekTerminalTags.INDUSTRIAL_TURBINE
        );

        terminalTag.put(
            MekTerminalTags.INDUSTRIAL_TURBINE,
            config
        );

        return new IndustrialTurbineConfig(config);
    }

    public int getWidth() {
        return normalizeStoredWidth(
            tag.getInt(
                MekTerminalTags.INDUSTRIAL_TURBINE_WIDTH
            )
        );
    }

    // Width is square, odd, and large enough for the selected rotor count.
    public void setWidth(int requestedWidth) {
        int current = getWidth();
        int minimum = getMinimumWidth();

        int normalized = clamp(
            requestedWidth,
            minimum,
            MAX_WIDTH
        );

        if ((normalized & 1) == 0) {
            normalized += requestedWidth < current ? -1 : 1;
        }

        normalized = clamp(
            normalized,
            minimum,
            MAX_WIDTH
        );

        if ((normalized & 1) == 0) {
            normalized = roundUpOdd(normalized);
        }

        tag.putInt(
            MekTerminalTags.INDUSTRIAL_TURBINE_WIDTH,
            normalized
        );
    }

    public int getHeight() {
        return clamp(
            tag.getInt(
                MekTerminalTags.INDUSTRIAL_TURBINE_HEIGHT
            ),
            getMinimumHeight(),
            MAX_HEIGHT
        );
    }

    public void setHeight(int height) {
        tag.putInt(
            MekTerminalTags.INDUSTRIAL_TURBINE_HEIGHT,
            clamp(
                height,
                getMinimumHeight(),
                MAX_HEIGHT
            )
        );
    }

    public int getRotorCount() {
        return clamp(
            tag.getInt(
                MekTerminalTags.INDUSTRIAL_TURBINE_ROTORS
            ),
            MIN_ROTORS,
            MAX_ROTORS
        );
    }

    public void setRotorCount(int rotors) {
        tag.putInt(
            MekTerminalTags.INDUSTRIAL_TURBINE_ROTORS,
            clamp(
                rotors,
                MIN_ROTORS,
                MAX_ROTORS
            )
        );

        setWidth(getWidth());
        setHeight(getHeight());
    }

    public int getMinimumWidth() {
        int requiredByRotors = getRotorCount() / 2 + 3;

        return roundUpOdd(
            Math.max(
                MIN_WIDTH,
                requiredByRotors
            )
        );
    }

    public int getMinimumHeight() {
        return getRotorCount() + 4;
    }

    public int getComplexY() {
        return getRotorCount() + 1;
    }

    public int getFirstUpperY() {
        return getComplexY() + 1;
    }

    public int getUpperLayerCount() {
        return getHeight() - getRotorCount() - 3;
    }

    private void ensureDefaults() {
        if (
            !tag.contains(
                MekTerminalTags.INDUSTRIAL_TURBINE_ROTORS
            )
        ) {
            tag.putInt(
                MekTerminalTags.INDUSTRIAL_TURBINE_ROTORS,
                DEFAULT_ROTORS
            );
        }

        if (
            !tag.contains(
                MekTerminalTags.INDUSTRIAL_TURBINE_WIDTH
            )
        ) {
            tag.putInt(
                MekTerminalTags.INDUSTRIAL_TURBINE_WIDTH,
                DEFAULT_WIDTH
            );
        }

        if (
            !tag.contains(
                MekTerminalTags.INDUSTRIAL_TURBINE_HEIGHT
            )
        ) {
            tag.putInt(
                MekTerminalTags.INDUSTRIAL_TURBINE_HEIGHT,
                DEFAULT_HEIGHT
            );
        }

        setRotorCount(getRotorCount());
        setWidth(getWidth());
        setHeight(getHeight());
    }

    private int normalizeStoredWidth(int width) {
        return roundUpOdd(
            clamp(
                width,
                getMinimumWidth(),
                MAX_WIDTH
            )
        );
    }

    private int roundUpOdd(int value) {
        return (value & 1) == 0 ? value + 1 : value;
    }

    private int clamp(
        int value,
        int min,
        int max
    ) {
        return Math.max(
            min,
            Math.min(max, value)
        );
    }
}
