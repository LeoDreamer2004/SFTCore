package org.leodreamer.sftcore.api.machine.multiblock;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.ITieredMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.utils.GTUtil;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.simibubi.create.infrastructure.config.AllConfigs;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.api.machine.multiblock.part.KineticPartMachine;
import org.leodreamer.sftcore.api.machine.trait.IKineticMachine;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@DataGenScanned
public class WorkableKineticMultiblockMachine extends KineticMultiblockMachine implements ITieredMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            WorkableKineticMultiblockMachine.class, KineticMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Getter
    @Persisted
    @DescSynced
    public float maxTorque = 0;

    public List<BlockPos> inputPartsMax = new ArrayList<>();

    public WorkableKineticMultiblockMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        for (IMultiPart part : getParts()) {
            if (part instanceof KineticPartMachine kineticPart) {
                if (kineticPart.getIO() == IO.IN) {
                    if (kineticPart.getKineticDefinition().torque > maxTorque) {
                        maxTorque = kineticPart.getKineticDefinition().torque;
                        inputPartsMax.clear();
                        inputPartsMax.add(kineticPart.getKineticHolder().getBlockPos());
                    } else if (kineticPart.getKineticDefinition().torque == maxTorque) {
                        {
                            inputPartsMax.add(kineticPart.getKineticHolder().getBlockPos());
                        }
                    }
                }
            }
        }
    }

    @Override
    public int getTier() {
        return GTUtil.getTierByVoltage((long) (maxTorque / 4)) + speed >= 128 ? 1 : 0;
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        boolean result = super.beforeWorking(recipe);
        previousSpeed = speed;
        if (speed != previousSpeed) {
            updateRotateBlocks(result);
        }
        return result;
    }

    @Override
    public void onChanged() {
        super.onChanged();
        updateMachineSpeed();
    }

    private void updateMachineSpeed() {
        speed = AllConfigs.server().kinetics.maxRotationSpeed.get();
        for (IMultiPart part : getParts()) {
            if (part instanceof IKineticMachine kineticPart && inputPartsMax.contains(kineticPart.getKineticHolder().getBlockPos())) {
                speed = Math.min(speed, Math.abs(kineticPart.getKineticHolder().getSpeed()));
            }
        }
    }

    @RegisterLanguage("Kinetic Speed: %sRPM")
    static final String SPEED = "sftcore.multiblock.kinetic.speed";

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        textList.add(Component.translatable(SPEED, speed));
    }
}