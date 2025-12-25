package org.leodreamer.sftcore.integration.ae2.item;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import appeng.api.parts.IPartHost;
import appeng.api.parts.SelectedPart;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.core.localization.PlayerMessages;
import appeng.items.AEBaseItem;
import appeng.util.InteractionUtil;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@DataGenScanned
public class SuperUpgradeCardItem extends AEBaseItem {

    private final Item card;

    public SuperUpgradeCardItem(Item cardType, Properties properties) {
        super(properties.rarity(Rarity.RARE).stacksTo(1));
        this.card = cardType;
    }

    @RegisterLanguage("Can be used infinitely. Press shift and right click the ae block to install the update")
    static final String TOOLTIP = "sftcore.item.super_upgrade_card.tooltip";

    @Override
    public void appendHoverText(
        ItemStack stack, @Nullable Level level, List<Component> lines, TooltipFlag advancedTooltips
    ) {
        super.appendHoverText(stack, level, lines, advancedTooltips);
        lines.add(Component.translatable(TOOLTIP).withStyle(ChatFormatting.YELLOW));
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Player player = context.getPlayer();
        InteractionHand hand = context.getHand();
        if (player != null && InteractionUtil.isInAlternateUseMode(player)) {
            final BlockEntity te = context.getLevel().getBlockEntity(context.getClickedPos());
            IUpgradeInventory upgrades = null;

            if (te instanceof IPartHost part) {
                final SelectedPart sp = part.selectPartWorld(context.getClickLocation());
                if (sp.part instanceof IUpgradeableObject upgradeable) {
                    upgrades = upgradeable.getUpgrades();
                }
            } else if (te instanceof IUpgradeableObject upgradeable) {
                upgrades = upgradeable.getUpgrades();
            }

            if (upgrades != null && upgrades.size() > 0) {
                var heldStack = player.getItemInHand(hand);

                boolean isFull = true;
                for (int i = 0; i < upgrades.size(); i++) {
                    if (upgrades.getStackInSlot(i).isEmpty()) {
                        isFull = false;
                        break;
                    }
                }
                if (isFull) {
                    player.displayClientMessage(PlayerMessages.MaxUpgradesInstalled.text(), true);
                    return InteractionResult.FAIL;
                }

                var upgrade = ((SuperUpgradeCardItem) heldStack.getItem()).card;

                var maxInstalled = upgrades.getMaxInstalled(upgrade);
                var installed = upgrades.getInstalledUpgrades(upgrade);
                if (maxInstalled <= 0) {
                    player.displayClientMessage(PlayerMessages.UnsupportedUpgrade.text(), true);
                    return InteractionResult.FAIL;
                } else if (installed >= maxInstalled) {
                    player.displayClientMessage(PlayerMessages.MaxUpgradesOfTypeInstalled.text(), true);
                    return InteractionResult.FAIL;
                }
                if (player.getCommandSenderWorld().isClientSide()) {
                    return InteractionResult.PASS;
                }
                upgrades.addItems(new ItemStack(card, Integer.MAX_VALUE));
                return InteractionResult.sidedSuccess(player.getCommandSenderWorld().isClientSide());
            }
        }

        return super.onItemUseFirst(stack, context);
    }
}
