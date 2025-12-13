package org.leodreamer.sftcore.common.data;

import com.tterrag.registrate.providers.ProviderType;
import net.minecraftforge.data.event.GatherDataEvent;
import org.leodreamer.sftcore.common.data.lang.SFTLangHandler;
import org.leodreamer.sftcore.common.data.tag.SFTItemTagLoader;
import org.leodreamer.sftcore.integration.create.SFTCreateDataGen;

import static org.leodreamer.sftcore.SFTCore.REGISTRATE;

public class SFTDataGen {
    public static void gatherData(GatherDataEvent event) {
        REGISTRATE.addDataGenerator(ProviderType.LANG, SFTLangHandler::init);
        REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, SFTItemTagLoader::init);
        SFTCreateDataGen.gatherData(event);
    }
}
