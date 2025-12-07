package org.leodreamer.sftcore.common.machine.multiblock.part;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMaintenanceMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.part.MultiblockPartMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredPartMachine;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib.gui.widget.ComponentPanelWidget;
import com.lowdragmc.lowdraglib.gui.widget.DraggableScrollableWidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class ConfigurableAutoMaintenanceHatchPartMachine extends TieredPartMachine implements IMaintenanceMachine {
    @Getter
    @Persisted
    private float durationMultiplier = 1f;

    private static final float MAX_DURATION_MULTIPLIER = 5f;
    private static final float MIN_DURATION_MULTIPLIER = 0.2f;
    private static final float DURATION_ACTION_AMOUNT = 0.2f;

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(ConfigurableAutoMaintenanceHatchPartMachine.class, MultiblockPartMachine.MANAGED_FIELD_HOLDER);

    public ConfigurableAutoMaintenanceHatchPartMachine(IMachineBlockEntity holder) {
        super(holder, GTValues.IV);
    }

    @Override
    public boolean isFullAuto() {
        return true;
    }

    @Override
    public void setTaped(boolean isTaped) {
    }

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
    public void setMaintenanceProblems(byte problems) {
    }

    @Override
    public int getTimeActive() {
        return 0;
    }

    @Override
    public void setTimeActive(int time) {
    }

    @Override
    public Widget createUIWidget() {
        WidgetGroup group;
        group = new WidgetGroup(0, 0, 150, 70);
        group.addWidget(new DraggableScrollableWidgetGroup(4, 4, 150 - 8, 70 - 8).setBackground(GuiTextures.DISPLAY)
                .addWidget(new ComponentPanelWidget(4, 5, list -> {
                    list.add(getTimeWidget());
                    var buttonText = Component.translatable("gtceu.maintenance.configurable_duration.modify");
                    buttonText.append(" ");
                    buttonText.append(ComponentPanelWidget.withButton(Component.literal("[-]"), "sub"));
                    buttonText.append(" ");
                    buttonText.append(ComponentPanelWidget.withButton(Component.literal("[+]"), "add"));
                    list.add(buttonText);
                }).setMaxWidthLimit(150 - 8 - 8 - 4).clickHandler((componentData, clickData) -> {
                    if (!clickData.isRemote) {
                        if (componentData.equals("sub")) {
                            durationMultiplier = Mth.clamp(durationMultiplier - DURATION_ACTION_AMOUNT,
                                    MIN_DURATION_MULTIPLIER, MAX_DURATION_MULTIPLIER);
                        } else if (componentData.equals("add")) {
                            durationMultiplier = Mth.clamp(durationMultiplier + DURATION_ACTION_AMOUNT,
                                    MIN_DURATION_MULTIPLIER, MAX_DURATION_MULTIPLIER);
                        }
                    }
                })));
        group.setBackground(GuiTextures.BACKGROUND_INVERSE);
        return group;
    }

    @Override
    public @NotNull ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    private Component getTimeWidget() {
        Component tooltip;
        if (durationMultiplier == 1.0) {
            tooltip = Component.translatable("gtceu.maintenance.configurable_duration.unchanged_description");
        } else {
            tooltip = Component.translatable("gtceu.maintenance.configurable_duration.changed_description",
                    FormattingUtil.formatNumber2Places(durationMultiplier));
        }
        return Component
                .translatable("gtceu.maintenance.configurable_duration",
                        FormattingUtil.formatNumber2Places(durationMultiplier))
                .setStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, tooltip)));
    }
}
