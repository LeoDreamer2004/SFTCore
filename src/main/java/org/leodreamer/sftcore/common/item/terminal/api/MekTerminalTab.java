package org.leodreamer.sftcore.common.item.terminal.api;

import com.gregtechceu.gtceu.api.gui.fancy.IFancyUIProvider;
import net.minecraft.resources.ResourceLocation;

public interface MekTerminalTab extends IFancyUIProvider {

    ResourceLocation id();

    MekMultiblockBuilder builder();
}
