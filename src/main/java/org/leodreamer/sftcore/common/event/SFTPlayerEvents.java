package org.leodreamer.sftcore.common.event;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.common.advancement.SFTCriteriaTriggers;

import com.gregtechceu.gtceu.api.capability.GTCapabilityHelper;
import com.gregtechceu.gtceu.common.data.GTItems;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SFTCore.MOD_ID)
public final class SFTPlayerEvents {

    private SFTPlayerEvents() {}

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        if (!(event.player instanceof ServerPlayer player)) {
            return;
        }

        if (player.level().isClientSide) {
            return;
        }

        if (player.tickCount % 20 != 0) {
            return;
        }

        if (hasFullUltimateBattery(player)) {
            SFTCriteriaTriggers.ULTIMATE_BATTERY_FULL.trigger(player);
        }
    }

    private static boolean hasFullUltimateBattery(ServerPlayer player) {
        var inventory = player.getInventory();

        for (var stack : inventory.items) {
            if (isFullUltimateBattery(stack)) {
                return true;
            }
        }

        for (var stack : inventory.offhand) {
            if (isFullUltimateBattery(stack)) {
                return true;
            }
        }

        return false;
    }

    private static boolean isFullUltimateBattery(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        if (stack.getItem() != GTItems.ULTIMATE_BATTERY.get()) {
            return false;
        }

        var electricItem = GTCapabilityHelper.getElectricItem(stack);

        if (electricItem == null) {
            return false;
        }

        long maxCharge = electricItem.getMaxCharge();

        return maxCharge > 0 && electricItem.getCharge() >= maxCharge;
    }
}
