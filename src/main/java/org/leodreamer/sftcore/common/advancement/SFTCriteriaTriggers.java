package org.leodreamer.sftcore.common.advancement;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.common.advancement.trigger.FormedGTMultiblockTrigger;

@Mod.EventBusSubscriber(modid = SFTCore.MOD_ID)
public final class SFTCriteriaTriggers {

    public static final FormedGTMultiblockTrigger FORMED_GT_MULTIBLOCK =
        new FormedGTMultiblockTrigger();

    private static boolean registered = false;

    private SFTCriteriaTriggers() {
    }

    public static void register() {
        if (registered) {
            return;
        }
        CriteriaTriggers.register(FORMED_GT_MULTIBLOCK);
        registered = true;
    }

    @SubscribeEvent
    public void onSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(SFTCriteriaTriggers::register);
    }
}
