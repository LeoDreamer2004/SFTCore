package org.leodreamer.sftcore.common.command;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

@DataGenScanned
public class HandCommand {

    @RegisterLanguage("Hand item: ")
    private static final String ITEM = "commands.sftcore.hand.item";

    @RegisterLanguage("NBT: ")
    private static final String NBT = "commands.sftcore.hand.nbt";

    public static LiteralArgumentBuilder<CommandSourceStack> builder() {
        return Commands.literal("hand").executes(
            context -> {
                // get the item in the player hand
                var player = context.getSource().getPlayerOrException();
                var itemStack = player.getMainHandItem();
                context.getSource().sendSystemMessage(
                    Component.translatable(ITEM).append(itemStack.getHoverName().getString())
                );
                context.getSource().sendSystemMessage(
                    Component.translatable(NBT)
                        .append(itemStack.getTag() != null ? itemStack.getTag().toString() : "empty")
                );
                return 1;
            }
        );
    }
}
