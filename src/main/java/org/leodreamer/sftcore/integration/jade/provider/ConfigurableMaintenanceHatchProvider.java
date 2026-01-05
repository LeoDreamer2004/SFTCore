package org.leodreamer.sftcore.integration.jade.provider;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMaintenanceMachine;
import com.gregtechceu.gtceu.utils.FormattingUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

@DataGenScanned
public class ConfigurableMaintenanceHatchProvider
    implements IBlockComponentProvider,
    IServerDataProvider<BlockAccessor> {

    private static final String DURATION_MULTIPLIER = "duration_multiplier";

    public static final ConfigurableMaintenanceHatchProvider INSTANCE = new ConfigurableMaintenanceHatchProvider();

    @RegisterLanguage("Configurable Maintenance Hatch")
    static final String JADE_CONFIG = "config.jade.plugin_sftcore.configurable_maintenance_hatch";

    @Override
    public ResourceLocation getUid() {
        return SFTCore.id("configurable_maintenance_hatch");
    }

    @RegisterLanguage("Recipe Duration Multiplier: %s")
    static final String TOOLTIP_MULTIPLIER = "sftcore.jade.gtceu.configurable_maintenance.duration_multiplier";

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag tag = accessor.getServerData();
        if (tag.contains(DURATION_MULTIPLIER)) {
            float multiplier = tag.getFloat(DURATION_MULTIPLIER);
            if (multiplier != 1.0F) {
                Component multiplierText = Component.literal(FormattingUtil.formatNumbers(multiplier))
                    .withStyle(ChatFormatting.GOLD);
                tooltip.add(Component.translatable(TOOLTIP_MULTIPLIER, multiplierText));
            }
        }
    }

    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
        if (
            accessor.getBlockEntity() instanceof IMachineBlockEntity mbe &&
                mbe.getMetaMachine() instanceof IMaintenanceMachine machine
        ) {
            tag.putFloat(DURATION_MULTIPLIER, machine.getDurationMultiplier());
        }
    }
}
