package org.leodreamer.sftcore.mixin.travelarchor;

import de.castcrafter.travelanchors.item.ItemTravelStaff;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.leodreamer.sftcore.api.registry.SFTTooltipsBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ItemTravelStaff.class)
public class TravelStaffMixin {

    @Inject(method = "appendHoverText", at = @At("TAIL"))
    private void $appendModifyTooltip(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag, CallbackInfo ci) {
        tooltip.addAll(SFTTooltipsBuilder.of()
                .insert(Component.translatable("sftcore.mixin.travel_anchor.travel_staff.tooltip").withStyle(ChatFormatting.AQUA))
                .modifiedBySFT().list());
    }
}
