package org.leodreamer.sftcore.api.wrapper;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

public class TooltipedBlockItem extends BlockItem {

    Component[] tooltips;

    public TooltipedBlockItem(Block pBlock, Properties pProperties, Component... pTooltips) {
        super(pBlock, pProperties);
        tooltips = pTooltips;
    }

    @Override
    public void appendHoverText(
        @NotNull ItemStack stack,
        @Nullable Level level,
        @NotNull List<Component> tooltip,
        @NotNull TooltipFlag flag
    ) {
        tooltip.addAll(Arrays.asList(tooltips));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
