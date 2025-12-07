package org.leodreamer.sftcore.common.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;

@DataGenScanned
@Mod.EventBusSubscriber(modid = SFTCore.MOD_ID)
public class WoolBlock {

    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        BlockPos pos = event.getPos();
        Level level = event.getLevel();
        Block block = level.getBlockState(pos).getBlock();
        if (event.getEntity().isShiftKeyDown() || !isWoolBlock(block)) {
            return;
        }

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
        if (!level.isClientSide()) {
            dropString(level, pos);
        }

    }

    private static boolean isWoolBlock(Block block) {
        return block == Blocks.WHITE_WOOL ||
                block == Blocks.ORANGE_WOOL ||
                block == Blocks.MAGENTA_WOOL ||
                block == Blocks.LIGHT_BLUE_WOOL ||
                block == Blocks.YELLOW_WOOL ||
                block == Blocks.LIME_WOOL ||
                block == Blocks.PINK_WOOL ||
                block == Blocks.GRAY_WOOL ||
                block == Blocks.LIGHT_GRAY_WOOL ||
                block == Blocks.CYAN_WOOL ||
                block == Blocks.PURPLE_WOOL ||
                block == Blocks.BLUE_WOOL ||
                block == Blocks.BROWN_WOOL ||
                block == Blocks.GREEN_WOOL ||
                block == Blocks.RED_WOOL ||
                block == Blocks.BLACK_WOOL;
    }

    private static void dropString(Level level, BlockPos pos) {
        ItemStack string = new ItemStack(Items.STRING);
        ItemEntity itemEntity = new ItemEntity(level,
                pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                string);

        itemEntity.setDeltaMovement(
                level.random.nextDouble() * 0.1 - 0.05,
                0.2,
                level.random.nextDouble() * 0.1 - 0.05
        );

        level.addFreshEntity(itemEntity);
    }

    @RegisterLanguage("Right click wool blocks to get string")
    static String STRING_TOOLTIP = "sftcore.wool_to_string.tooltip";

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        if (event.getItemStack().getItem() == Items.STRING) {
            event.getToolTip().add(Component.literal(STRING_TOOLTIP).withStyle(ChatFormatting.GOLD));
        }
    }
}
