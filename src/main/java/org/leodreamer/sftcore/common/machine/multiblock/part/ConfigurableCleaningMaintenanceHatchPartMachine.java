package org.leodreamer.sftcore.common.machine.multiblock.part;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.machine.multiblock.CleanroomType;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.common.machine.trait.CleanroomProviderTrait;
import com.gregtechceu.gtceu.common.machine.trait.CleanroomReceiverTrait;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Set;

import static com.gregtechceu.gtceu.api.GTValues.LuV;
import static com.gregtechceu.gtceu.api.GTValues.UV;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ConfigurableCleaningMaintenanceHatchPartMachine
    extends ConfigurableAutoMaintenanceHatchPartMachine {

    private final CleanroomProviderTrait cleanroomProvider;

    @Getter
    private final CleanroomType cleanroomType;

    public ConfigurableCleaningMaintenanceHatchPartMachine(
        BlockEntityCreationInfo info,
        CleanroomType cleanroomType
    ) {
        super(info, LuV);
        this.cleanroomType = cleanroomType;
        cleanroomProvider = attachTrait(new CleanroomProviderTrait(Set.of(cleanroomType)));
    }

    @Override
    public void addedToController(MultiblockControllerMachine controller) {
        super.addedToController(controller);
        controller.self().getTraitOptional(CleanroomReceiverTrait.TYPE)
            .ifPresent(t -> t.setCleanroomProvider(cleanroomProvider));
    }

    @Override
    public void removedFromController(MultiblockControllerMachine controller) {
        super.removedFromController(controller);
        controller.getTraitOptional(CleanroomReceiverTrait.TYPE)
            .ifPresent(CleanroomReceiverTrait::removeCleanroom);
    }

    @Override
    public int getTier() {
        return cleanroomType == CleanroomType.CLEANROOM ? LuV : UV;
    }
}
