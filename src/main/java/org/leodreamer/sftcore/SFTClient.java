package org.leodreamer.sftcore;

import net.createmod.ponder.foundation.PonderIndex;
import org.leodreamer.sftcore.integration.ponder.SFTPonderPlugin;

public class SFTClient {
    public static void init() {
        PonderIndex.addPlugin(new SFTPonderPlugin());
    }
}
