package org.leodreamer.sftcore.common.item.terminal.config.impl;

import org.leodreamer.sftcore.common.item.terminal.config.BoundedIntEntry;
import org.leodreamer.sftcore.common.item.terminal.config.BuilderBaseConfig;

import net.minecraft.nbt.CompoundTag;

public final class FissionReactorConfig extends BuilderBaseConfig {

    public final BoundedIntEntry width;
    public final BoundedIntEntry height;
    public final BoundedIntEntry depth;

    public FissionReactorConfig(CompoundTag terminalTag) {
        super(terminalTag, "fission_reactor");
        this.width = new BoundedIntEntry(tag, "width", 3, 18, 5);
        this.height = new BoundedIntEntry(tag, "height", 4, 18, 5);
        this.depth = new BoundedIntEntry(tag, "depth", 3, 18, 5);
    }

    public static FissionReactorConfig resolve(CompoundTag terminalTag) {
        return new FissionReactorConfig(terminalTag);
    }
}
