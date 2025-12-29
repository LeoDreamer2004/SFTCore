package org.leodreamer.sftcore.common.command;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.common.command.dump.DumpCommand;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.mojang.brigadier.builder.ArgumentBuilder;

import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = SFTCore.MOD_ID)
public class SFTCommands {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        Consumer<ArgumentBuilder<CommandSourceStack, ?>> registrar = (builder) -> event.getDispatcher()
            .register(Commands.literal(SFTCore.MOD_ID).then(builder));

        registrar.accept(DumpCommand.builder());
        registrar.accept(HandCommand.builder());
    }
}
