package org.leodreamer.sftcore.mixin.gregtech.block;

import org.leodreamer.sftcore.integration.ae2.feature.IMemoryCardInteraction;

import com.gregtechceu.gtceu.api.block.IMachineBlock;
import com.gregtechceu.gtceu.api.block.MetaMachineBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import appeng.api.implementations.items.MemoryCardMessages;
import appeng.items.tools.MemoryCardItem;
import appeng.util.InteractionUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MetaMachineBlock.class)
public abstract class MetaMachineBlockMixin implements IMachineBlock {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true, remap = false)
    private void allowAEMemoryCard(
        BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit,
        CallbackInfoReturnable<InteractionResult> cir
    ) {
        var machine = getMachine(world, pos);
        if (machine instanceof IMemoryCardInteraction interaction) {
            var stack = player.getItemInHand(hand);
            if (stack.getItem() instanceof MemoryCardItem card) {
                var data = card.getData(stack);
                if (InteractionUtil.isInAlternateUseMode(player)) {
                    interaction.sftcore$exportSettings(data, player);
                    card.notifyUser(player, MemoryCardMessages.SETTINGS_SAVED);
                    card.setMemoryCardContents(stack, interaction.sftcore$memoryId(), data);
                } else {
                    interaction.sftcore$importSettings(data, player);
                    card.notifyUser(player, MemoryCardMessages.SETTINGS_LOADED);
                }
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        }
    }
}
