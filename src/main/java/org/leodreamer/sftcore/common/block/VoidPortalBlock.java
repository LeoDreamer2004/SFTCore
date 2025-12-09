package org.leodreamer.sftcore.common.block;

import com.mojang.blaze3d.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.data.SFTBlocks;
import org.leodreamer.sftcore.common.data.SFTDimensions;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@DataGenScanned
public class VoidPortalBlock extends Block {

    public VoidPortalBlock(Properties properties) {
        super(properties);
    }

    public static final BlockPos VOID_SPAWN_POINT = new BlockPos(0, 80, 0);

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player pPlayer, InteractionHand hand, BlockHitResult hit) {

        if (!level.isClientSide && hand == InteractionHand.MAIN_HAND) {
            var server = level.getServer();
            var player = (ServerPlayer) pPlayer;
            if (server == null) {
                return InteractionResult.PASS;
            }
            var dimension = level.dimension();

            if (dimension == Level.OVERWORLD) {
                ServerLevel target = server.getLevel(SFTDimensions.VOID_DIMENSION);
                if (target == null) {
                    return InteractionResult.PASS;
                }
                player.teleportTo(
                        target,
                        VOID_SPAWN_POINT.getX() + 0.5,
                        VOID_SPAWN_POINT.getY(),
                        VOID_SPAWN_POINT.getZ() + 0.5,
                        player.getYRot(),
                        player.getXRot()
                );
                makePlatform(target);
            } else if (dimension == SFTDimensions.VOID_DIMENSION) {
                teleportToSpawnPoint(server, player);
            }

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private static void makePlatform(ServerLevel level) {
        BlockPos blockpos = VOID_SPAWN_POINT.below();
        level.setBlockAndUpdate(blockpos, SFTBlocks.VOID_PORTAL.getDefaultState());
    }

    private static void teleportToSpawnPoint(MinecraftServer server, ServerPlayer player) {
        BlockPos position = player.getRespawnPosition();
        ServerLevel dimension = server.getLevel(player.getRespawnDimension());

        if (position == null || dimension == null) {
            dimension = server.overworld();
            position = dimension.getSharedSpawnPos();
        }

        position = position.above();

        player.teleportTo(
                dimension,
                position.getX() + 0.5,
                position.getY(),
                position.getZ() + 0.5,
                player.getYRot(),
                player.getXRot()
        );
    }

    @RegisterLanguage("Right click and see what will happen")
    static final String TOOLTIP = "sftcore.void_portal.tooltip";

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable BlockGetter
            pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        pTooltip.add(Component.translatable(TOOLTIP));
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
    }
}
