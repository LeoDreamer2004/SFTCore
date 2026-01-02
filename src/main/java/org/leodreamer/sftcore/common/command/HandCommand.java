package org.leodreamer.sftcore.common.command;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.jetbrains.annotations.Nullable;

@DataGenScanned
public class HandCommand {

    @RegisterLanguage("Hand item: ")
    private static final String ITEM = "commands.sftcore.hand.item";

    @RegisterLanguage("NBT: ")
    private static final String NBT = "commands.sftcore.hand.nbt";

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static LiteralArgumentBuilder<CommandSourceStack> builder() {
        return Commands.literal("hand")
            .then(
                Commands.argument("prettyPrint", BoolArgumentType.bool()).executes(
                    context -> {
                        boolean prettyPrint = BoolArgumentType.getBool(context, "prettyPrint");
                        return executeHandCommand(context.getSource(), prettyPrint);
                    }
                )
            )
            .executes(
                context -> executeHandCommand(context.getSource(), true)
            );
    }

    private static int executeHandCommand(CommandSourceStack context, boolean pretty) throws CommandSyntaxException {
        var player = context.getPlayerOrException();
        var itemStack = player.getMainHandItem();
        context.sendSystemMessage(Component.translatable(ITEM).append(itemStack.getHoverName().getString()));

        context.sendSystemMessage(Component.translatable(NBT));
        String nbtOutput = nbtString(itemStack.getTag(), pretty);
        context.sendSystemMessage(Component.literal(nbtOutput));
        return 1;
    }

    private static String nbtString(@Nullable CompoundTag tag, boolean pretty) {
        if (tag == null) {
            return "empty";
        }
        return pretty ? gson.toJson(gson.fromJson(tag.toString(), Object.class)) : tag.toString();
    }
}
