package org.leodreamer.sftcore.common.item.behavior;

import com.gregtechceu.gtceu.api.capability.GTCapabilityHelper;
import com.gregtechceu.gtceu.api.item.component.IAddInformation;
import com.gregtechceu.gtceu.api.item.component.IInteractionItem;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.feature.IOverclockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.common.data.GTRecipeCapabilities;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.hepdd.gtmthings.api.misc.WirelessEnergyContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.leodreamer.sftcore.api.registry.SFTTooltips;

import java.util.List;
import java.util.Objects;

public class TimeBottleBehavior implements IInteractionItem, IAddInformation {

    @Override
    public InteractionResult onItemUseFirst(ItemStack itemStack, UseOnContext context) {
        if (context.getLevel().isClientSide()) return InteractionResult.PASS;
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;

        var container = WirelessEnergyContainer.getOrCreateContainer(context.getPlayer().getUUID());
        if (accelerate(container, context)) {
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    private static boolean accelerate(WirelessEnergyContainer container, UseOnContext context) {
        var logic = GTCapabilityHelper.getRecipeLogic(context.getLevel(), context.getClickedPos(), null);
        if (logic == null || !logic.isWorking()) {
            return false;
        }
        MetaMachine machine = logic.getMachine();

        if (!(machine instanceof IOverclockMachine overclockMachine)) {
            return false;
        }

        GTRecipe recipe = logic.getLastOriginRecipe();
        if (recipe == null || recipe.getOutputEUt().getTotalEU() > 0) {
            return false;
        }

        int leftDuration = (int) ((logic.getDuration() - logic.getProgress()) * 0.95);
        long eu = leftDuration * overclockMachine.getOverclockVoltage();
        if (eu == 0) {
            return false;
        }

        if (container.removeEnergy(eu, null) != eu) {
            Objects.requireNonNull(context.getPlayer()).
                    displayClientMessage(Component.translatable("item.sftcore.time_bottle.energy_lack"), true);
            return false;
        }

        logic.setProgress(logic.getProgress() + leftDuration);
        Objects.requireNonNull(context.getPlayer()).
                displayClientMessage(Component.translatable("item.sftcore.time_bottle.accelerate", FormattingUtil.formatNumbers(eu), leftDuration), true);
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.translatable("item.sftcore.time_bottle.tooltip"));
        tooltipComponents.add(SFTTooltips.textureComeFrom("Time In a Bottle"));
    }
}
