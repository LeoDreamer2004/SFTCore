package org.leodreamer.sftcore.common.item;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.data.lang.SFTTooltipsBuilder;

import com.gregtechceu.gtceu.api.item.component.IAddInformation;
import com.gregtechceu.gtceu.api.item.component.IInteractionItem;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IOverclockMachine;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.utils.FormattingUtil;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import cn.qiuye.gtmoremachine.api.misc.wireless.energy.WirelessEnergyContainer;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * These codes are partially from <a href=
 * "https://github.com/GregTech-Odyssey/GTOCore/blob/main/src/main/java/com/gtocore/common/item/TimeTwisterBehavior.java">GregTech
 * Odyssey</a> (while I'm also the contributor)
 */
@DataGenScanned
public class TimeBottleBehavior implements IInteractionItem, IAddInformation {

    @RegisterLanguage("Right click on a machine and finish the current recipe instantly with the wireless energy.")
    private static final String TOOLTIP = "item.sftcore.time_bottle.tooltip";

    @RegisterLanguage("Your wireless energy DOES NOT support the machine acceleration.")
    private static final String ENERGY_LACK = "item.sftcore.time_bottle.energy_lack";

    @RegisterLanguage("Using %s EU, accelerate the machine with %d ticks")
    private static final String ACCELERATE = "item.sftcore.time_bottle.accelerate";

    @Override
    public InteractionResult onItemUseFirst(ItemStack itemStack, UseOnContext context) {
        if (context.getLevel().isClientSide()) {
            return InteractionResult.PASS;
        }
        var player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }
        var container = WirelessEnergyContainer.getOrCreateContainer(context.getPlayer().getUUID());
        return accelerate(container, context) ? InteractionResult.CONSUME : InteractionResult.PASS;
    }

    private static boolean accelerate(WirelessEnergyContainer container, UseOnContext context) {
        var machine = MetaMachine.getMachine(context.getLevel(), context.getClickedPos());
        if (!(machine instanceof IRecipeLogicMachine rlm)) {
            return false;
        }
        var logic = rlm.getRecipeLogic();
        if (!logic.isWorking()) {
            return false;
        }

        if (!(machine instanceof IOverclockMachine overclockMachine)) {
            return false;
        }

        var recipe = logic.getLastOriginRecipe();
        if (recipe == null || recipe.getOutputEUt().getTotalEU() > 0) {
            return false;
        }

        int leftDuration = (int) ((logic.getDuration() - logic.getProgress()) * 0.95);
        long eu = leftDuration * overclockMachine.getOverclockVoltage();
        if (eu == 0) {
            return false;
        }

        var player = context.getPlayer();
        if (player == null) {
            return false;
        }

        if (container.removeEnergy(eu, null) != eu) {
            player.displayClientMessage(Component.translatable(ENERGY_LACK), true);
            return false;
        }

        logic.setProgress(logic.getProgress() + leftDuration);
        player.displayClientMessage(
            Component.translatable(ACCELERATE, FormattingUtil.formatNumbers(eu), leftDuration),
            true
        );
        return true;
    }

    @Override
    public void appendHoverText(
        ItemStack stack,
        @Nullable Level level,
        List<Component> components,
        TooltipFlag isAdvanced
    ) {
        SFTTooltipsBuilder.of()
            .insert(Component.translatable(TOOLTIP))
            .textureComeFrom("Time In a Bottle")
            .addTo(components);
    }
}
