package org.leodreamer.sftcore.integration.mek;

import mekanism.api.chemical.gas.IGasHandler;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public final class SFTMekanismCapabilities {
    public static final Capability<IGasHandler> GAS_HANDLER =
        CapabilityManager.get(new CapabilityToken<>() {});

    private SFTMekanismCapabilities() {
    }
}
