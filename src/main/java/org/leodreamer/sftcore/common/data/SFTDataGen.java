package org.leodreamer.sftcore.common.data;

import org.leodreamer.sftcore.common.data.lang.SFTLangHandler;

import net.minecraftforge.data.event.GatherDataEvent;

import com.tterrag.registrate.providers.ProviderType;

import static org.leodreamer.sftcore.SFTCore.REGISTRATE;

public class SFTDataGen {

    public static void gatherData(GatherDataEvent event) {
        REGISTRATE.addDataGenerator(ProviderType.LANG, SFTLangHandler::init);
    }
}
