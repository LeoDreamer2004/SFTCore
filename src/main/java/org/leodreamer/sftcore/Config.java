package org.leodreamer.sftcore;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = SFTCore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.BooleanValue KEEP_DISTINCT = BUILDER
            .comment("Whether to delete the repeated items which occurred in blocks")
            .define("keepDistinct", true);
    public static ForgeConfigSpec SPEC = BUILDER.build();
    public static boolean keepDistinct;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        keepDistinct = KEEP_DISTINCT.get();
    }
}
