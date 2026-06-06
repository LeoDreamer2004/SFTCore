package org.leodreamer.sftcore.common.item.terminal.config.impl;

import org.leodreamer.sftcore.common.item.terminal.config.BoundedIntEntry;
import org.leodreamer.sftcore.common.item.terminal.config.BuilderBaseConfig;

import net.minecraft.nbt.CompoundTag;

public final class ThermalBoilerConfig extends BuilderBaseConfig {

    public final BoundedIntEntry width;
    public final BoundedIntEntry height;
    public final BoundedIntEntry depth;
    public final BoundedIntEntry lowerHeight;

    public ThermalBoilerConfig(CompoundTag terminalTag) {
        super(terminalTag, "thermal_boiler");
        this.width = new BoundedIntEntry(tag, "width", 3, 18, 5);
        this.height = new BoundedIntEntry(tag, "height", 5, 18, 6);
        this.depth = new BoundedIntEntry(tag, "depth", 3, 18, 5);
        this.lowerHeight = new BoundedIntEntry(
            tag, "lower_height",
            () -> 1, this::maxLowerHeight, 2
        );
    }

    public static ThermalBoilerConfig resolve(CompoundTag terminalTag) {
        return new ThermalBoilerConfig(terminalTag);
    }

    public int getDisperserY() {
        return lowerHeight.get() + 1;
    }

    private int maxLowerHeight() {
        return height.get() - 4;
    }
}
