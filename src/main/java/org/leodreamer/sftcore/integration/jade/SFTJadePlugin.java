package org.leodreamer.sftcore.integration.jade;

import org.leodreamer.sftcore.integration.jade.provider.ConfigurableMaintenanceHatchProvider;
import org.leodreamer.sftcore.integration.jade.provider.PatternContainerProvider;

import com.gregtechceu.gtceu.api.block.MetaMachineBlock;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class SFTJadePlugin implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(
            ConfigurableMaintenanceHatchProvider.INSTANCE, MetaMachineBlockEntity.class
        );
        registration.registerBlockDataProvider(
            PatternContainerProvider.INSTANCE, BlockEntity.class
        );
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(
            ConfigurableMaintenanceHatchProvider.INSTANCE, MetaMachineBlock.class
        );
        registration.registerBlockComponent(
            PatternContainerProvider.INSTANCE, Block.class
        );
    }
}
