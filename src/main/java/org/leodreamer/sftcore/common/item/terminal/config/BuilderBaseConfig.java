package org.leodreamer.sftcore.common.item.terminal.config;

import net.minecraft.nbt.CompoundTag;

public abstract class BuilderBaseConfig {

    protected final CompoundTag tag;

    protected BuilderBaseConfig(CompoundTag terminalTag, String subTagKey) {
        this.tag = terminalTag.getCompound(subTagKey);
        terminalTag.put(subTagKey, this.tag);
    }
}
