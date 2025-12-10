package org.leodreamer.sftcore.api.machine.multiblock;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget;
import com.gregtechceu.gtceu.api.gui.fancy.IFancyUIProvider;
import com.gregtechceu.gtceu.api.gui.fancy.TooltipsPanel;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IDisplayUIMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockDisplayText;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import org.leodreamer.sftcore.api.recipe.capability.StressRecipeCapability;
import org.leodreamer.sftcore.common.machine.trait.NotifiableStressTrait;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class KineticMultiblockMachine extends WorkableMultiblockMachine implements IFancyUIMachine, IDisplayUIMachine {
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            KineticMultiblockMachine.class, WorkableMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Getter
    public LongSet rotateBlocks;
    @Getter
    public LongSet blazeBlocks;

    @Getter
    @Persisted
    @DescSynced
    public float speed = 64;
    @Getter
    @Persisted
    @DescSynced
    public float previousSpeed = 0;

    public KineticMultiblockMachine(IMachineBlockEntity holder) {
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
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        getCapabilitiesFlat(IO.OUT, StressRecipeCapability.CAP).forEach(iRecipeHandler -> {
            if (iRecipeHandler instanceof NotifiableStressTrait notifiableStressTrait) {
                notifiableStressTrait.preWorking();
            }
        });
        return super.beforeWorking(recipe);
    }

    public void postWorking() {
        getCapabilitiesFlat(IO.OUT, StressRecipeCapability.CAP).forEach(iRecipeHandler -> {
            if (iRecipeHandler instanceof NotifiableStressTrait notifiableStressTrait) {
                notifiableStressTrait.postWorking();
            }
        });
    }

    public void preWorking() {
        getCapabilitiesFlat(IO.OUT, StressRecipeCapability.CAP).forEach(iRecipeHandler -> {
            if (iRecipeHandler instanceof NotifiableStressTrait notifiableStressTrait) {
                notifiableStressTrait.preWorking();
            }
        });
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

    public void updateBlazeBlocks(boolean active) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
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
                        Method method = BlazeBurnerBlockEntity.class.getDeclaredMethod("setBlockHeat", BlazeBurnerBlock.HeatLevel.class);
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

    @Override
    public void addDisplayText(List<Component> textList) {
        if (recipeLogic.isWaiting()) {
            textList.add(Component.translatable("gtceu.multiblock.waiting").withStyle(ChatFormatting.RED));
            for (var reason : recipeLogic.getFancyTooltip()) {
                textList.add(Component.literal(" - " + reason.getString()));
            }
        }
        MultiblockDisplayText.builder(textList, isFormed())
                .setWorkingStatus(recipeLogic.isWorkingEnabled(), recipeLogic.isActive())
                .addMachineModeLine(getRecipeType(), getRecipeTypes().length > 1)
                .addWorkingStatusLine()
                .addProgressLine(recipeLogic.getProgress(), recipeLogic.getMaxProgress(),
                        recipeLogic.getProgressPercent())
                .addOutputLines(recipeLogic.getLastRecipe());
        getDefinition().getAdditionalDisplay().accept(this, textList);
        IDisplayUIMachine.super.addDisplayText(textList);
    }

    @Override
    public Widget createUIWidget() {
        var group = new WidgetGroup(0, 0, 182 + 8, 117 + 8);
        group.addWidget(new DraggableScrollableWidgetGroup(4, 4, 182, 117).setBackground(getScreenTexture())
                .addWidget(new LabelWidget(4, 5, self().getBlockState().getBlock().getDescriptionId()))
                .addWidget(new ComponentPanelWidget(4, 17, this::addDisplayText)
                        .textSupplier(Objects.requireNonNull(this.getLevel()).isClientSide ? null : this::addDisplayText)
                        .setMaxWidthLimit(200)
                        .clickHandler(this::handleDisplayClick)));
        group.setBackground(GuiTextures.BACKGROUND_INVERSE);
        return group;
    }

    @Override
    public ModularUI createUI(Player entityPlayer) {
        return new ModularUI(198, 208, this, entityPlayer).widget(new FancyMachineUIWidget(this, 198, 208));
    }

    @Override
    public List<IFancyUIProvider> getSubTabs() {
        return getParts().stream().filter(Objects::nonNull).map(IFancyUIProvider.class::cast).toList();
    }

    @Override
    public void attachTooltips(TooltipsPanel tooltipsPanel) {
        for (IMultiPart part : getParts()) {
            part.attachFancyTooltipsToController(this, tooltipsPanel);
        }
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
                if (machine instanceof KineticMultiblockMachine kineticMultiblockMachine) {
                    kineticMultiblockMachine.postWorking();
                }
            }
            if (last != Status.WORKING && getStatus() == Status.WORKING) {
                if (machine instanceof KineticMultiblockMachine kineticMultiblockMachine) {
                    kineticMultiblockMachine.preWorking();
                }
            }
        }

        @Override
        public void inValid() {
            if (lastRecipe != null && machine.onWorking()) {
                if (machine instanceof KineticMultiblockMachine kineticMultiblockMachine) {
                    kineticMultiblockMachine.postWorking();
                }
            }
        }
    }
}