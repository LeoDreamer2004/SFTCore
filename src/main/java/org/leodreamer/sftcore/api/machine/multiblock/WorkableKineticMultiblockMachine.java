package org.leodreamer.sftcore.api.machine.multiblock;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.api.gui.SFTGuiTextures;
import org.leodreamer.sftcore.api.machine.multiblock.part.KineticPartMachine;
import org.leodreamer.sftcore.api.machine.trait.IKineticMachine;
import org.leodreamer.sftcore.api.recipe.capability.StressRecipeCapability;
import org.leodreamer.sftcore.common.machine.trait.NotifiableStressTrait;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.machine.feature.ITieredMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IDisplayUIMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockDisplayText;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.utils.GTUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;

import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import com.simibubi.create.infrastructure.config.AllConfigs;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@DataGenScanned
public class WorkableKineticMultiblockMachine extends WorkableMultiblockMachine
    implements IDisplayUIMachine, ITieredMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
        WorkableKineticMultiblockMachine.class, WorkableMultiblockMachine.MANAGED_FIELD_HOLDER
    );

    @Getter
    public LongSet rotateBlocks;
    @Getter
    public LongSet blazeBlocks;

    @Getter
    @Persisted
    @DescSynced
    public float maxTorque = 0;

    public List<BlockPos> inputPartsMax = new ArrayList<>();

    @Getter
    @Persisted
    @DescSynced
    public float speed = 64;
    @Getter
    @Persisted
    @DescSynced
    public float previousSpeed = 0;

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
        previousSpeed = 0;
        rotateBlocks = getMultiblockState().getMatchContext().getOrDefault("roBlocks", LongSets.emptySet());
        blazeBlocks = getMultiblockState().getMatchContext().getOrDefault("bbBlocks", LongSets.emptySet());
        updateActiveBlocks(recipeLogic.isWorking());
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

    //////////////////////////////////////
    // ********* Recipe Logic **********//

    /// ///////////////////////////////////

    @Override
    protected RecipeLogic createRecipeLogic(Object... args) {
        return new KineticRecipeLogic(this);
    }

    @Override
    public KineticRecipeLogic getRecipeLogic() {
        return (KineticRecipeLogic) super.getRecipeLogic();
    }

    @Override
    public int getTier() {
        return GTUtil.getTierByVoltage((long) (maxTorque / 4)) + speed >= 128 ? 1 : 0;
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        getCapabilitiesFlat(IO.OUT, StressRecipeCapability.CAP)
            .forEach(
                iRecipeHandler -> {
                    if (iRecipeHandler instanceof NotifiableStressTrait notifiableStressTrait) {
                        notifiableStressTrait.preWorking();
                    }
                }
            );
        boolean result = super.beforeWorking(recipe);
        previousSpeed = speed;
        if (speed != previousSpeed) {
            updateRotateBlocks(result);
        }
        return result;
    }

    public void postWorking() {
        getCapabilitiesFlat(IO.OUT, StressRecipeCapability.CAP)
            .forEach(
                iRecipeHandler -> {
                    if (iRecipeHandler instanceof NotifiableStressTrait notifiableStressTrait) {
                        notifiableStressTrait.postWorking();
                    }
                }
            );
    }

    public void preWorking() {
        getCapabilitiesFlat(IO.OUT, StressRecipeCapability.CAP)
            .forEach(
                iRecipeHandler -> {
                    if (iRecipeHandler instanceof NotifiableStressTrait notifiableStressTrait) {
                        notifiableStressTrait.preWorking();
                    }
                }
            );
    }

    @Override
    public void onChanged() {
        super.onChanged();
        updateMachineSpeed();
    }

    private void updateMachineSpeed() {
        speed = AllConfigs.server().kinetics.maxRotationSpeed.get();
        for (IMultiPart part : getParts()) {
            if (
                part instanceof IKineticMachine kineticPart &&
                    inputPartsMax.contains(kineticPart.getKineticHolder().getBlockPos())
            ) {
                speed = Math.min(speed, Math.abs(kineticPart.getKineticHolder().getSpeed()));
            }
        }
    }

    @Override
    public void updateActiveBlocks(boolean active) {
        super.updateActiveBlocks(active);
        updateRotateBlocks(active);
        try {
            updateBlazeBlocks(active);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateRotateBlocks(boolean active) {
        if (rotateBlocks != null) {
            for (Long pos : rotateBlocks) {
                var blockPos = BlockPos.of(pos);
                var blockEntity = Objects.requireNonNull(getLevel()).getBlockEntity(blockPos);
                if (blockEntity != null) {
                    updateRotateBlock(active, blockEntity);
                }
            }
        }
    }

    public void updateRotateBlock(boolean active, BlockEntity blockEntity) {
        if (blockEntity instanceof KineticBlockEntity kineticBlockEntity) {
            if (active) {
                kineticBlockEntity.setSpeed(speed);
                kineticBlockEntity.onSpeedChanged(previousSpeed);
                kineticBlockEntity.sendData();
            } else {
                kineticBlockEntity.setSpeed(0);
                kineticBlockEntity.onSpeedChanged(speed);
                kineticBlockEntity.sendData();
            }
        }
    }

    public void updateBlazeBlocks(boolean active)
        throws NoSuchMethodException, InvocationTargetException,
        IllegalAccessException {
        if (blazeBlocks != null) {
            for (Long pos : blazeBlocks) {
                var blockPos = BlockPos.of(pos);
                if (Objects.requireNonNull(getLevel()).getBlockEntity(blockPos) != null) {
                    var blockEntity = Objects.requireNonNull(getLevel()).getBlockEntity(blockPos);
                    BlazeBurnerBlock.HeatLevel heat = BlazeBurnerBlock.HeatLevel.SMOULDERING;
                    if (active) {
                        if (speed >= 256) {
                            heat = BlazeBurnerBlock.HeatLevel.SEETHING;
                        } else if (speed >= 128) {
                            heat = BlazeBurnerBlock.HeatLevel.KINDLED;
                        } else {
                            heat = BlazeBurnerBlock.HeatLevel.FADING;
                        }
                    }
                    if (blockEntity instanceof BlazeBurnerBlockEntity blazeBurnerBlockEntity) {
                        Method method = BlazeBurnerBlockEntity.class.getDeclaredMethod(
                            "setBlockHeat", BlazeBurnerBlock.HeatLevel.class
                        );
                        method.setAccessible(true);
                        method.invoke(blazeBurnerBlockEntity, heat);
                    }
                }
            }
        }
    }

    //////////////////////////////////////
    // ********** GUI ***********//
    /// ///////////////////////////////////

    @RegisterLanguage("Kinetic Speed: %sRPM")
    static final String SPEED = "sftcore.multiblock.kinetic.speed";

    @Override
    public void addDisplayText(List<Component> textList) {
        if (recipeLogic.isWaiting()) {
            textList.add(
                Component.translatable("gtceu.multiblock.waiting").withStyle(ChatFormatting.RED)
            );
            for (var reason : recipeLogic.getFancyTooltip()) {
                textList.add(Component.literal(" - " + reason.getString()));
            }
        }
        MultiblockDisplayText.builder(textList, isFormed())
            .setWorkingStatus(recipeLogic.isWorkingEnabled(), recipeLogic.isActive())
            .addMachineModeLine(getRecipeType(), getRecipeTypes().length > 1)
            .addWorkingStatusLine()
            .addProgressLine(
                recipeLogic.getProgress(),
                recipeLogic.getMaxProgress(),
                recipeLogic.getProgressPercent()
            )
            .addOutputLines(recipeLogic.getLastRecipe());
        getDefinition().getAdditionalDisplay().accept(this, textList);
        textList.add(Component.translatable(SPEED, speed));
        IDisplayUIMachine.super.addDisplayText(textList);
    }

    @Override
    public IGuiTexture getScreenTexture() {
        return SFTGuiTextures.DISPLAY_CREATE;
    }

    public static class KineticRecipeLogic extends RecipeLogic {

        public KineticRecipeLogic(IRecipeLogicMachine machine) {
            super(machine);
        }

        @Override
        public void handleRecipeWorking() {
            Status last = this.getStatus();
            super.handleRecipeWorking();
            if (last == Status.WORKING && getStatus() != Status.WORKING) {
                if (machine instanceof WorkableKineticMultiblockMachine kineticMultiblockMachine) {
                    kineticMultiblockMachine.postWorking();
                }
            }
            if (last != Status.WORKING && getStatus() == Status.WORKING) {
                if (machine instanceof WorkableKineticMultiblockMachine kineticMultiblockMachine) {
                    kineticMultiblockMachine.preWorking();
                }
            }
        }

        @Override
        public void inValid() {
            if (lastRecipe != null && machine.onWorking()) {
                if (machine instanceof WorkableKineticMultiblockMachine kineticMultiblockMachine) {
                    kineticMultiblockMachine.postWorking();
                }
            }
        }
    }
}
