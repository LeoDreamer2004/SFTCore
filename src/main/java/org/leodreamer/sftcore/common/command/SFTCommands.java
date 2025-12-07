package org.leodreamer.sftcore.common.command;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.common.command.dump.DumpCommand;

@Mod.EventBusSubscriber(modid = SFTCore.MOD_ID)
public class SFTCommands {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        DumpCommand.register(event.getDispatcher());
    }
}
