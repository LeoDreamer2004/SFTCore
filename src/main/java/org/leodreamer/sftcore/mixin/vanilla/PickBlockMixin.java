package org.leodreamer.sftcore.mixin.vanilla;

import org.leodreamer.sftcore.integration.ae2.sync.PickBlockAERequestPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;

import appeng.core.sync.network.NetworkHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Minecraft.class)
public class PickBlockMixin {

    @WrapOperation(
        method = "pickBlock",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Inventory;findSlotMatchingItem(Lnet/minecraft/world/item/ItemStack;)I"
        )
    )
    private int sftcore$pickBlockFallback(Inventory inventory, ItemStack stack, Operation<Integer> original) {
        int slot = original.call(inventory, stack);
        if (slot != -1 || stack.isEmpty()) {
            return slot;
        }

        var minecraft = Minecraft.getInstance();
        if (
            minecraft.player == null || minecraft.gameMode == null ||
                minecraft.gameMode.getPlayerMode() != GameType.SURVIVAL
        ) {
            return slot;
        }

        NetworkHandler.instance().sendToServer(new PickBlockAERequestPacket(stack));
        return slot;
    }
}
