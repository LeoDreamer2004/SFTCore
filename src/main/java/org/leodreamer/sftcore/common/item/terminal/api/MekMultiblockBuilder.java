package org.leodreamer.sftcore.common.item.terminal.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import org.leodreamer.sftcore.common.item.MekTerminalBehavior;

public interface MekMultiblockBuilder {

    ResourceLocation id();

    Component title();

    boolean canStart(BuildContext ctx);

    BuildPlan createPlan(BuildContext ctx, CompoundTag rootTag);

    default Component invalidStartMessage() {
        return Component.translatable(MekTerminalBehavior.INVALID_START);
    }
}
