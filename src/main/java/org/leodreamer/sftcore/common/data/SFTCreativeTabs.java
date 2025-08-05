package org.leodreamer.sftcore.common.data;

import com.gregtechceu.gtceu.common.data.GTCreativeModeTabs;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import org.leodreamer.sftcore.common.data.machine.SFTMultiMachines;

import static org.leodreamer.sftcore.SFTCore.*;

public final class SFTCreativeTabs {
    public static final RegistryEntry<CreativeModeTab> SFTCore = REGISTRATE
            .defaultCreativeTab(MOD_ID, builder -> builder
                    .displayItems(new GTCreativeModeTabs.RegistrateDisplayItemsGenerator(MOD_ID, REGISTRATE))
                    .title(Component.literal(NAME))
                    .icon(SFTMultiMachines.COMMON_MEKANISM_PROCESS_FACTORY::asStack)
                    .build())
            .register();

    static {
        REGISTRATE.creativeModeTab(() -> SFTCore);
    }

    public static void init() {
    }
}
