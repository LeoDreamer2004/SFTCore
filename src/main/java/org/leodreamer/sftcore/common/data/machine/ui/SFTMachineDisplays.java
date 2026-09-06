package org.leodreamer.sftcore.common.data.machine.ui;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.multiblock.CoilWorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.utils.FormattingUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import brachy.modularui.api.drawable.Text;
import brachy.modularui.api.widget.IWidget;
import brachy.modularui.value.sync.BooleanSyncValue;
import brachy.modularui.value.sync.IntSyncValue;
import brachy.modularui.value.sync.PanelSyncManager;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class SFTMachineDisplays {

    public static BiFunction<MultiblockControllerMachine, PanelSyncManager, List<IWidget>> coilMachineTempDisplay(
        Function<CoilWorkableElectricMultiblockMachine, Integer> tempFunc
    ) {
        return (controller, syncManager) -> {
            if (!(controller instanceof CoilWorkableElectricMultiblockMachine coilMachine)) {
                return Collections.emptyList();
            }
            var isFormed = syncManager.getOrCreateSyncHandler(
                "sftcoreIsFormed", BooleanSyncValue.class,
                () -> new BooleanSyncValue(controller::isFormed)
            );
            var temperature = syncManager.getOrCreateSyncHandler(
                "sftcoreCoilTemperature", IntSyncValue.class,
                () -> new IntSyncValue(() -> tempFunc.apply(coilMachine))
            );
            return Collections.singletonList(
                Text.dynamic(
                    () -> Component.translatable(
                        "gtceu.multiblock.blast_furnace.max_temperature",
                        Component.literal(FormattingUtil.formatNumbers(temperature.getIntValue()) + "K")
                            .setStyle(Style.EMPTY.withColor(ChatFormatting.RED))
                    )
                ).asWidget().setEnabledIf(widget -> isFormed.getBoolValue())
            );
        };
    }

    public static final BiFunction<MultiblockControllerMachine, PanelSyncManager, List<IWidget>> simpleCoilDisplay = coilMachineTempDisplay(
        coilMachine -> coilMachine.getCoilType().getCoilTemperature()
    );

    public static final BiFunction<MultiblockControllerMachine, PanelSyncManager, List<IWidget>> ebfCoilDisplay = coilMachineTempDisplay(
        coilMachine -> coilMachine.getCoilType().getCoilTemperature() +
            100 * Math.max(0, coilMachine.getTier() - GTValues.MV)
    );
}
