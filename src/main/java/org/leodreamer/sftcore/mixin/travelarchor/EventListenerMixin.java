package org.leodreamer.sftcore.mixin.travelarchor;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;

import de.castcrafter.travelanchors.EventListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EventListener.class)
public class EventListenerMixin {

    @Redirect(
              method = "onRightClick",
              at = @At(
                       value = "INVOKE",
                       target = "Lnet/minecraft/world/item/ItemCooldowns;addCooldown(Lnet/minecraft/world/item/Item;I)V"))
    private void travelAnchors$skipCooldown(ItemCooldowns cooldowns, Item item, int coolDownTicks) {
        // Do nothing
    }
}
