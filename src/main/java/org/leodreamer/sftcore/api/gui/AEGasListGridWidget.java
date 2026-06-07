package org.leodreamer.sftcore.api.gui;

import com.gregtechceu.gtceu.integration.ae2.gui.widget.list.AEListGridWidget;
import com.gregtechceu.gtceu.integration.ae2.utils.KeyStorage;

import net.minecraft.network.FriendlyByteBuf;

import appeng.api.stacks.AEKey;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import me.ramidzkh.mekae2.ae2.MekanismKeyType;

public class AEGasListGridWidget extends AEListGridWidget {

    public AEGasListGridWidget(int x, int y, int slotsY, KeyStorage internalList) {
        super(x, y, slotsY, internalList);
    }

    @Override
    protected void toPacket(FriendlyByteBuf buffer, AEKey key) {
        key.writeToPacket(buffer);
    }

    @Override
    protected AEKey fromPacket(FriendlyByteBuf buffer) {
        return MekanismKeyType.TYPE.readFromPacket(buffer);
    }

    @Override
    protected Widget createDisplayWidget(int x, int y, int index) {
        return new AEGasDisplayWidget(x, y, this, index);
    }
}
