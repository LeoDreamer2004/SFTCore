package org.leodreamer.sftcore.api.kinetics;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.machine.feature.IMuiMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.recipe.RecipeLogic;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;

import brachy.modularui.api.drawable.Text;
import brachy.modularui.api.widget.IWidget;
import brachy.modularui.factory.PosGuiData;
import brachy.modularui.screen.UISettings;
import brachy.modularui.value.sync.BooleanSyncValue;
import brachy.modularui.value.sync.PanelSyncManager;
import brachy.modularui.value.sync.StringSyncValue;
import brachy.modularui.widget.ParentWidget;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

@DataGenScanned
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WorkableKineticMultiblockMachine extends WorkableMultiblockMachine implements IMuiMachine {

    public WorkableKineticMultiblockMachine(BlockEntityCreationInfo info) {
        super(info);
    }

    public WorkableKineticMultiblockMachine(BlockEntityCreationInfo info, RecipeLogic recipeLogic) {
        super(info, recipeLogic);
    }

    @Override
    public WorkableKineticMultiblockMachine self() {
        return this;
    }

    @RegisterLanguage("Input RPM: %s RPM")
    static final String INPUT_RPM = "sftcore.multiblock.kinetic.input_rpm";

    @RegisterLanguage("Available Stress: %ssu")
    static final String AVAILABLE_STRESS = "sftcore.multiblock.kinetic.available_stress";

    @Override
    public void buildMainUI(
        ParentWidget<?> mainWidget,
        PosGuiData guiData,
        PanelSyncManager syncManager,
        UISettings settings
    ) {
        mainWidget.child(getMainTextPanel(syncManager).margin(4, 2));
    }

    @Override
    public List<IWidget> getWidgetsForDisplay(PanelSyncManager syncManager) {
        List<IWidget> widgets = new ArrayList<>();
        widgets.addAll(getKineticWidgets(syncManager));
        widgets.addAll(super.getWidgetsForDisplay(syncManager));
        return widgets;
    }

    private List<IWidget> getKineticWidgets(PanelSyncManager syncManager) {
        BooleanSyncValue isFormed = syncManager.getOrCreateSyncHandler(
            "isFormed",
            BooleanSyncValue.class,
            () -> new BooleanSyncValue(this::isFormed)
        );
        StringSyncValue rpm = syncManager.getOrCreateSyncHandler(
            "kineticInputRpm",
            StringSyncValue.class,
            () -> new StringSyncValue(
                () -> KineticRecipeHelper.format(KineticRecipeHelper.getMaxInputRPM(recipeLogic)),
                value -> {}
            )
        );
        StringSyncValue stress = syncManager.getOrCreateSyncHandler(
            "kineticAvailableStress",
            StringSyncValue.class,
            () -> new StringSyncValue(
                () -> KineticRecipeHelper.format(KineticRecipeHelper.getTotalAvailableStress(recipeLogic)),
                value -> {}
            )
        );

        return List.of(
            Text.dynamic(() -> Component.translatable(INPUT_RPM, rpm.getStringValue()).withStyle(ChatFormatting.GRAY))
                .asWidget()
                .setEnabledIf(widget -> isFormed.getBoolValue()),
            Text.dynamic(
                () -> Component.translatable(AVAILABLE_STRESS, stress.getStringValue())
                    .withStyle(ChatFormatting.GRAY)
            )
                .asWidget()
                .setEnabledIf(widget -> isFormed.getBoolValue())
        );
    }
}
