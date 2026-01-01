package org.leodreamer.sftcore.api.machine.trait;

import org.leodreamer.sftcore.common.save.WirelessSavedData;
import org.leodreamer.sftcore.integration.ae2.logic.WirelessGrid;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.trait.MachineTrait;

import net.minecraft.server.level.ServerLevel;

import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

public class WirelessGridHolder extends MachineTrait {

    @Getter
    @Setter
    @Nullable
    private WirelessGrid grid;

    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(WirelessGridHolder.class);

    public WirelessGridHolder(MetaMachine machine) {
        super(machine);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onMachineUnLoad() {
        super.onMachineUnLoad();
        if (
            machine.getLevel() instanceof ServerLevel serverLevel &&
                serverLevel.getServer().isCurrentlySaving()
        ) return;

        if (grid != null) {
            grid.members().remove(machine.getPos());
            WirelessSavedData.INSTANCE.setDirty();
        }
    }
}
