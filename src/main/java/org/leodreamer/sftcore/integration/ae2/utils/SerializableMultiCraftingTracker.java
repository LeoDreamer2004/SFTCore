package org.leodreamer.sftcore.integration.ae2.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

import appeng.api.networking.crafting.ICraftingRequester;
import appeng.helpers.MultiCraftingTracker;

public class SerializableMultiCraftingTracker extends MultiCraftingTracker implements INBTSerializable<CompoundTag> {

    public SerializableMultiCraftingTracker(ICraftingRequester o, int size) {
        super(o, size);
    }

    @Override
    public CompoundTag serializeNBT() {
        var nbt = new CompoundTag();
        writeToNBT(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        readFromNBT(nbt);
    }
}
