package org.leodreamer.sftcore.forge;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.common.command.SFTCommands;

@Mod.EventBusSubscriber(modid = SFTCore.MOD_ID)
public class ForgeEventListener {
    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        SFTCommands.register(event);
    }
}
