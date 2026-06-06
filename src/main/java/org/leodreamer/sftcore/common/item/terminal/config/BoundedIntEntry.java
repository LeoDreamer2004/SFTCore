package org.leodreamer.sftcore.common.item.terminal.config;

import net.minecraft.nbt.CompoundTag;

import java.util.function.IntSupplier;

public final class BoundedIntEntry {

    private final CompoundTag tag;
    private final String key;
    private final IntSupplier minFn;
    private final IntSupplier maxFn;
    private final int defaultValue;

    public BoundedIntEntry(CompoundTag tag, String key, int min, int max, int defaultValue) {
        this(tag, key, () -> min, () -> max, defaultValue);
    }

    public BoundedIntEntry(CompoundTag tag, String key, IntSupplier minFn, IntSupplier maxFn, int defaultValue) {
        this.tag = tag;
        this.key = key;
        this.minFn = minFn;
        this.maxFn = maxFn;
        this.defaultValue = defaultValue;
        ensureDefault();
    }

    public int get() {
        return clamp(tag.getInt(key));
    }

    public void set(int value) {
        tag.putInt(key, clamp(value));
    }

    private int clamp(int value) {
        return Math.max(minFn.getAsInt(), Math.min(maxFn.getAsInt(), value));
    }

    void ensureDefault() {
        if (!tag.contains(key)) {
            tag.putInt(key, defaultValue);
        }
    }
}
