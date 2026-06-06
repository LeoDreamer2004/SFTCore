package org.leodreamer.sftcore.common.item.terminal.config.impl;

import org.leodreamer.sftcore.common.item.terminal.config.BoundedIntEntry;
import org.leodreamer.sftcore.common.item.terminal.config.BuilderBaseConfig;

import net.minecraft.nbt.CompoundTag;

public final class IndustrialTurbineConfig extends BuilderBaseConfig {

    private static final String TAG_WIDTH = "width";
    private static final String TAG_HEIGHT = "height";
    private static final String TAG_ROTORS = "rotors";

    public final BoundedIntEntry height;
    public final BoundedIntEntry rotors;

    public IndustrialTurbineConfig(CompoundTag terminalTag) {
        super(terminalTag, "industrial_turbine");
        this.rotors = new BoundedIntEntry(
            tag, TAG_ROTORS, 1, 14, 4
        );
        this.height = new BoundedIntEntry(
            tag, TAG_HEIGHT,
            this::getMinimumHeight, () -> 18, 8
        );
        if (!tag.contains(TAG_WIDTH)) {
            tag.putInt(TAG_WIDTH, 5);
        }
        setWidth(getWidth());
    }

    public static IndustrialTurbineConfig resolve(CompoundTag terminalTag) {
        return new IndustrialTurbineConfig(terminalTag);
    }

    // Width (custom: odd, dynamic min from rotors, direction-aware rounding)

    public int getWidth() {
        return roundUpOdd(
            clamp(
                tag.getInt(TAG_WIDTH),
                getMinimumWidth(),
                17
            )
        );
    }

    public void setWidth(int requestedWidth) {
        int current = getWidth();
        int minimum = getMinimumWidth();

        int normalized = clamp(requestedWidth, minimum, 17);
        if ((normalized & 1) == 0) {
            normalized += requestedWidth < current ? -1 : 1;
        }
        normalized = clamp(normalized, minimum, 17);
        if ((normalized & 1) == 0) {
            normalized = roundUpOdd(normalized);
        }
        tag.putInt(TAG_WIDTH, normalized);
    }

    public int getMinimumWidth() {
        return roundUpOdd(Math.max(5, rotors.get() / 2 + 3));
    }

    public int getMinimumHeight() {
        return rotors.get() + 4;
    }

    public int getComplexY() {
        return rotors.get() + 1;
    }

    public int getFirstUpperY() {
        return getComplexY() + 1;
    }

    private int roundUpOdd(int value) {
        return (value & 1) == 0 ? value + 1 : value;
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
