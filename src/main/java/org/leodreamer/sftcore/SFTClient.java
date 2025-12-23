package org.leodreamer.sftcore;

import org.leodreamer.sftcore.integration.ponder.SFTPonderPlugin;

import net.createmod.ponder.foundation.PonderIndex;

public class SFTClient {

    public static void init() {
        PonderIndex.addPlugin(new SFTPonderPlugin());
    }
}
