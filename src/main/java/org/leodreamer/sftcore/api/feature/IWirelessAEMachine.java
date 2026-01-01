package org.leodreamer.sftcore.api.feature;

import com.gregtechceu.gtceu.integration.ae2.machine.feature.IGridConnectedMachine;
import org.leodreamer.sftcore.api.machine.trait.WirelessGridHolder;

public interface IWirelessAEMachine extends IGridConnectedMachine {
    WirelessGridHolder sftcore$getWirelessHolder();
}
