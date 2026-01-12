package org.leodreamer.sftcore.common.data.machine.ui;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.multiblock.CoilWorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.utils.FormattingUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class SFTMachineDisplays {

    public static BiConsumer<IMultiController, List<Component>> coilMachineTempDisplay(
        Function<CoilWorkableElectricMultiblockMachine, Integer> tempFunc
    ) {
        return (controller, components) -> {
            if (!controller.isFormed()) return;
            if (!(controller instanceof CoilWorkableElectricMultiblockMachine coilMachine)) return;

            int temp = tempFunc.apply(coilMachine);
            String tempStr = FormattingUtil.formatNumbers(temp) + "K";
            components.add(
                Component.translatable(
                    "gtceu.multiblock.blast_furnace.max_temperature",
                    Component.literal(tempStr)
                        .setStyle(Style.EMPTY.withColor(ChatFormatting.RED))
                )
            );
        };
    }

    public static final BiConsumer<IMultiController, List<Component>> simpleCoilDisplay = coilMachineTempDisplay(
        coilMachine -> coilMachine.getCoilType().getCoilTemperature()
    );

    public static final BiConsumer<IMultiController, List<Component>> ebfCoilDisplay = coilMachineTempDisplay(
        coilMachine -> coilMachine.getCoilType().getCoilTemperature() +
            100 * Math.max(0, coilMachine.getTier() - GTValues.MV)
    );
}
