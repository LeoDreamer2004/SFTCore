package org.leodreamer.sftcore.common.item.terminal.config.impl;

import org.leodreamer.sftcore.common.item.terminal.config.BoundedIntEntry;
import org.leodreamer.sftcore.common.item.terminal.config.BuilderBaseConfig;

import net.minecraft.nbt.CompoundTag;

public final class ThermalEvaporationConfig extends BuilderBaseConfig {

    public static final int WIDTH = 4;
    public static final int DEPTH = 4;

    public final BoundedIntEntry height;

    public ThermalEvaporationConfig(CompoundTag terminalTag) {
        super(terminalTag, "thermal_evaporation");
        this.height = new BoundedIntEntry(tag, "height", 3, 18, 4);
    }

    public static ThermalEvaporationConfig resolve(CompoundTag terminalTag) {
        return new ThermalEvaporationConfig(terminalTag);
    }
}
