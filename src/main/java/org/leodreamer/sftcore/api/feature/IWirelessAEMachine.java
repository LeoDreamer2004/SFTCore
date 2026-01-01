package org.leodreamer.sftcore.api.feature;

import org.leodreamer.sftcore.api.machine.trait.WirelessGridHolder;

import com.gregtechceu.gtceu.integration.ae2.machine.feature.IGridConnectedMachine;

public interface IWirelessAEMachine extends IGridConnectedMachine {

    WirelessGridHolder sftcore$getWirelessHolder();
}
