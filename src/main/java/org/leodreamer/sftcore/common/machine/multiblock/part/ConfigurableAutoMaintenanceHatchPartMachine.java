package org.leodreamer.sftcore.common.machine.multiblock.part;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.machine.feature.IMuiMachine;
import com.gregtechceu.gtceu.api.sync_system.annotations.SaveField;
import com.gregtechceu.gtceu.common.machine.multiblock.part.MaintenanceHatchPartMachine;
import com.gregtechceu.gtceu.common.mui.GTGuiTextures;
import com.gregtechceu.gtceu.utils.FormattingUtil;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;

import brachy.modularui.api.drawable.Text;
import brachy.modularui.factory.PosGuiData;
import brachy.modularui.screen.UISettings;
import brachy.modularui.value.sync.FloatSyncValue;
import brachy.modularui.value.sync.PanelSyncManager;
import brachy.modularui.widget.ParentWidget;
import brachy.modularui.widgets.TextWidget;
import brachy.modularui.widgets.layout.Flow;
import brachy.modularui.widgets.textfield.TextFieldWidget;
import lombok.Getter;

public class ConfigurableAutoMaintenanceHatchPartMachine extends MaintenanceHatchPartMachine
    implements IMuiMachine {

    @Getter
    @SaveField
    private float durationMultiplier = 1f;

    private static final float MAX_DURATION_MULTIPLIER = 5f;
    private static final float MIN_DURATION_MULTIPLIER = 0.2f;
    private static final float DURATION_ACTION_AMOUNT = 0.2f;

    public ConfigurableAutoMaintenanceHatchPartMachine(BlockEntityCreationInfo info) {
        super(info, GTValues.IV, true);
    }

    public ConfigurableAutoMaintenanceHatchPartMachine(BlockEntityCreationInfo info, int tier) {
        super(info, tier, true);
    }

    @Override
    public boolean isFullAuto() {
        return true;
    }

    @Override
    public void setTaped(boolean isTaped) {}

    @Override
    public boolean isTaped() {
        return false;
    }

    @Override
    public byte startProblems() {
        return NO_PROBLEMS;
    }

    @Override
    public byte getMaintenanceProblems() {
        return NO_PROBLEMS;
    }

    @Override
    public void setMaintenanceProblems(byte problems) {}

    @Override
    public int getTimeActive() {
        return 0;
    }

    @Override
    public void setTimeActive(int time) {}

    @Override
    public void buildMainUI(
        ParentWidget<?> mainWidget, PosGuiData guiData, PanelSyncManager syncManager,
        UISettings settings
    ) {
        mainWidget.child(
            Flow.col()
                .width(150)
                .coverChildrenHeight()
                .center()
                .padding(5)
                .childPadding(5)
                .background(GTGuiTextures.DISPLAY)
                .child(new TextWidget<>(Text.dynamic(this::getTimeWidget)))
                .child(
                    Flow.row()
                        .coverChildren()
                        .childPadding(5)
                        .child(new TextWidget<>(Text.lang("gtceu.maintenance.configurable_duration.modify")))
                        .child(
                            new TextFieldWidget()
                                .width(45)
                                .height(18)
                                .setNumbersDouble(() -> MIN_DURATION_MULTIPLIER, () -> MAX_DURATION_MULTIPLIER)
                                .setDefaultNumber(1)
                                .value(new FloatSyncValue(this::getDurationMultiplier, this::setDurationMultiplier))
                        )
                )
        );
    }

    public void setDurationMultiplier(float durationMultiplier) {
        this.durationMultiplier = Mth.clamp(
            durationMultiplier,
            MIN_DURATION_MULTIPLIER,
            MAX_DURATION_MULTIPLIER
        );
    }

    private Component getTimeWidget() {
        Component tooltip;
        if (durationMultiplier == 1.0) {
            tooltip = Component.translatable("gtceu.maintenance.configurable_duration.unchanged_description");
        } else {
            tooltip = Component.translatable(
                "gtceu.maintenance.configurable_duration.changed_description",
                FormattingUtil.formatNumber2Places(durationMultiplier)
            );
        }
        return Component.translatable(
            "gtceu.maintenance.configurable_duration",
            FormattingUtil.formatNumber2Places(durationMultiplier)
        )
            .setStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, tooltip)));
    }
}
