package org.leodreamer.sftcore.common.item.terminal.config.impl;

import org.leodreamer.sftcore.common.item.terminal.config.BoundedIntEntry;
import org.leodreamer.sftcore.common.item.terminal.config.BuilderBaseConfig;

import net.minecraft.nbt.CompoundTag;

public final class InductionMatrixConfig extends BuilderBaseConfig {

    public final BoundedIntEntry width;
    public final BoundedIntEntry height;
    public final BoundedIntEntry depth;

    public InductionMatrixConfig(CompoundTag terminalTag) {
        super(terminalTag, "induction_matrix");
        this.width = new BoundedIntEntry(tag, "width", 3, 18, 5);
        this.height = new BoundedIntEntry(tag, "height", 3, 18, 5);
        this.depth = new BoundedIntEntry(tag, "depth", 3, 18, 5);
    }

    public static InductionMatrixConfig resolve(CompoundTag terminalTag) {
        return new InductionMatrixConfig(terminalTag);
    }
}
