package org.leodreamer.sftcore.common.item.behavior;

import com.gregtechceu.gtceu.api.capability.GTCapabilityHelper;
import com.gregtechceu.gtceu.api.item.component.IAddInformation;
import com.gregtechceu.gtceu.api.item.component.IInteractionItem;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IOverclockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
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
import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.api.registry.SFTTooltipsBuilder;

import java.util.List;
import java.util.Objects;

@DataGenScanned
public class TimeBottleBehavior implements IInteractionItem, IAddInformation {

    @RegisterLanguage("Right click on a machine and finish the current recipe instantly with the wireless energy.")
    static String TOOLTIP = "item.sftcore.time_bottle.tooltip";
    @RegisterLanguage("Your wireless energy DOES NOT support the machine acceleration.")
    static String ENERGY_LACK = "item.sftcore.time_bottle.energy_lack";
    @RegisterLanguage("Using %s EU, accelerate the machine with %d ticks")
    static String ACCELERATE = "item.sftcore.time_bottle.accelerate";

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
                    displayClientMessage(Component.translatable(ENERGY_LACK), true);
            return false;
        }

        logic.setProgress(logic.getProgress() + leftDuration);
        Objects.requireNonNull(context.getPlayer()).
                displayClientMessage(Component.translatable(ACCELERATE, FormattingUtil.formatNumbers(eu), leftDuration), true);
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.translatable(TOOLTIP));
        tooltipComponents.addAll(SFTTooltipsBuilder.of().textureComeFrom("Time In a Bottle").all());
    }
}
