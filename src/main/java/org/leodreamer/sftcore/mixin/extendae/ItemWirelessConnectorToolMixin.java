package org.leodreamer.sftcore.mixin.extendae;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.api.feature.IWirelessAEMachine;
import org.leodreamer.sftcore.common.data.lang.MixinTooltips;
import org.leodreamer.sftcore.common.machine.GTWirelessControllerMachine;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

import com.glodblock.github.extendedae.common.items.ItemWirelessConnectTool;
import com.glodblock.github.extendedae.common.tileentities.TileWirelessConnector;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Optional;

@Mixin(ItemWirelessConnectTool.class)
public class ItemWirelessConnectorToolMixin extends Item {

    public ItemWirelessConnectorToolMixin(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        var player = context.getPlayer();
        if (player == null) return super.useOn(context);

        ItemStack stack = context.getItemInHand();
        var tag = stack.getOrCreateTag();

        var pos = context.getClickedPos();
        var be = context.getLevel().getBlockEntity(pos);

        if (be instanceof TileWirelessConnector) {
            return InteractionResult.PASS; // let the original handle it
        }

        var toTag = tag.get("to");

        if (be instanceof IMachineBlockEntity mbe && mbe.getMetaMachine() instanceof GTWirelessControllerMachine) {
            var posTag = BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, pos).getOrThrow(false, SFTCore.LOGGER::error);
            tag.put("to", posTag);

            if (context.getLevel().isClientSide) {
                player.displayClientMessage(
                    Component.translatable(MixinTooltips.WIRELESS_SELECTED, pos.getX(), pos.getY(), pos.getZ()), true
                );
            }

            return InteractionResult.SUCCESS;
        }

        if (be instanceof IMachineBlockEntity mbe && mbe.getMetaMachine() instanceof IWirelessAEMachine wireless) {
            var node = wireless.getGridNode();
            if (node == null) {
                return InteractionResult.FAIL; // client
            }

            Optional<BlockPos> anotherPos = BlockPos.CODEC.parse(NbtOps.INSTANCE, toTag).result();

            if (anotherPos.isEmpty()) {
                player.displayClientMessage(Component.translatable(MixinTooltips.WIRELESS_MISSING), true);
                return InteractionResult.FAIL;
            }

            BlockPos targetPos = anotherPos.get();
            var another = context.getLevel().getBlockEntity(targetPos);

            if (
                another instanceof IMachineBlockEntity otherMbe &&
                    otherMbe.getMetaMachine() instanceof GTWirelessControllerMachine controller
            ) {
                controller.join(wireless);
                player.displayClientMessage(Component.translatable(MixinTooltips.WIRELESS_CONNECT), true);
                return InteractionResult.SUCCESS;
            } else {
                player.displayClientMessage(Component.translatable(MixinTooltips.WIRELESS_MISSING), true);
                return InteractionResult.FAIL;
            }
        }

        return super.useOn(context);
    }
}
