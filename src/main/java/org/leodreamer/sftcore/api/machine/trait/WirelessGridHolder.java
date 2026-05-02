package org.leodreamer.sftcore.api.machine.trait;

import com.gregtechceu.gtceu.api.machine.trait.MachineTrait;
import com.gregtechceu.gtceu.api.machine.trait.MachineTraitType;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;
import org.leodreamer.sftcore.common.save.WirelessSavedData;
import org.leodreamer.sftcore.integration.ae2.logic.WirelessGrid;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WirelessGridHolder extends MachineTrait {

    @Getter
    @Setter
    @Nullable
    private WirelessGrid grid;

    public static final MachineTraitType<?> TYPE = new MachineTraitType<>(WirelessGridHolder.class);

    @Override
    public MachineTraitType<?> getTraitType() {
        return TYPE;
    }

    @Override
    public void onMachineUnload() {
        super.onMachineUnload();
        var machine = getMachine();
        if (
            machine.getLevel() instanceof ServerLevel serverLevel &&
                serverLevel.getServer().isCurrentlySaving()
        ) return;

        if (grid != null) {
            grid.members().remove(machine.getBlockPos());
            WirelessSavedData.INSTANCE.setDirty();
        }
    }
}
