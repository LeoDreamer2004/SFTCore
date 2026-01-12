package org.leodreamer.sftcore.common.data;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;

import com.gregtechceu.gtceu.common.data.GTCreativeModeTabs;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;

import com.tterrag.registrate.util.entry.RegistryEntry;

import static org.leodreamer.sftcore.SFTCore.*;

@DataGenScanned
public final class SFTCreativeTabs {

    @RegisterLanguage("Starter For Technology")
    private static final String CREATIVE_TAB = "sftcore.creative_tab.sftcore";

    public static final RegistryEntry<CreativeModeTab> SFTCore = REGISTRATE
        .defaultCreativeTab(
            MOD_ID,
            builder -> builder
                .displayItems(
                    new GTCreativeModeTabs.RegistrateDisplayItemsGenerator(
                        MOD_ID, REGISTRATE
                    )
                )
                .title(Component.translatable(CREATIVE_TAB))
                .icon(SFTItems.UU_MATTER::asStack)
                .build()
        )
        .register();

    static {
        REGISTRATE.creativeModeTab(() -> SFTCore);
    }

    public static void init() {}
}
