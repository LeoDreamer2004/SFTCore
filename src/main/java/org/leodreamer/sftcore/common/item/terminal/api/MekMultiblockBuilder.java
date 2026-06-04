package org.leodreamer.sftcore.common.item.terminal.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public interface MekMultiblockBuilder {

    ResourceLocation id();

    boolean canStart(BuildContext ctx);

    BuildPlan createPlan(BuildContext ctx, CompoundTag rootTag);

    default Component invalidStartMessage() {
        return Component.translatable(BuildReport.INVALID_START);
    }
}
