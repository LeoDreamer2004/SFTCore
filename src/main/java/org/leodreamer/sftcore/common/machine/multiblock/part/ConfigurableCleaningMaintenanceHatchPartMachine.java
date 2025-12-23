package org.leodreamer.sftcore.common.machine.multiblock.part;

import com.gregtechceu.gtceu.api.capability.ICleanroomReceiver;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.ICleanroomProvider;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.multiblock.CleanroomType;
import com.gregtechceu.gtceu.api.machine.multiblock.DummyCleanroom;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

import static com.gregtechceu.gtceu.api.GTValues.*;

public class ConfigurableCleaningMaintenanceHatchPartMachine
                                                             extends ConfigurableAutoMaintenanceHatchPartMachine {

    private final ICleanroomProvider DUMMY_CLEANROOM;

    @Getter
    private final CleanroomType cleanroomType;

    public ConfigurableCleaningMaintenanceHatchPartMachine(
                                                           IMachineBlockEntity holder, CleanroomType cleanroomType) {
        super(holder);
        this.cleanroomType = cleanroomType;
        DUMMY_CLEANROOM = DummyCleanroom.createForTypes(Collections.singletonList(cleanroomType));
    }

    @Override
    public void addedToController(@NotNull IMultiController controller) {
        super.addedToController(controller);
        if (controller instanceof ICleanroomReceiver receiver) {
            receiver.setCleanroom(DUMMY_CLEANROOM);
        }
    }

    @Override
    public void removedFromController(@NotNull IMultiController controller) {
        super.removedFromController(controller);
        if (controller instanceof ICleanroomReceiver receiver && receiver.getCleanroom() == DUMMY_CLEANROOM) {
            receiver.setCleanroom(null);
        }
    }

    @Override
    public int getTier() {
        return cleanroomType == CleanroomType.CLEANROOM ? LuV : UV;
    }
}
