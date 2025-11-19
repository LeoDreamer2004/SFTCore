package org.leodreamer.sftcore.api.registry;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class SFTTooltips {
    public static Component textureComeFrom(String where) {
        return Component.translatable("sftcore.texture_come_from", where)
                .withStyle(ChatFormatting.GRAY);
    }

    public static Component structureComeFrom(String where) {
        return Component.translatable("sftcore.structure_come_from", where)
                .withStyle(ChatFormatting.GRAY);
    }

    public static Component modifiedBySFT() {
        return Component.translatable("sftcore.modified_by_sft")
                .withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC, ChatFormatting.UNDERLINE);
    }
}
