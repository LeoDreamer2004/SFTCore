package org.leodreamer.sftcore.integration.ae2.item;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

import appeng.api.implementations.items.IMemoryCard;
import appeng.items.tools.quartz.QuartzCuttingKnifeItem;
import appeng.util.InteractionUtil;

@DataGenScanned
public class MemoryCardUtils {

    @RegisterLanguage("The Memory Card has some patterns in the clipboard. Clear it before cutting new patterns.")
    private static final String CONFIRM_CUT = "sftcore.mixin.ae2.memory_card.confirm_cut";

    @RegisterLanguage("The Memory Card has cut patterns to the clipboard.")
    private static final String CUT = "sftcore.mixin.ae2.memory_card.cut";

    public enum CuttingResult {
        NOT, // not cutting
        YES, // is cutting
        DANGER, // is cutting, but the memory card has already cut some patterns
    }

    public static CuttingResult isCutting(Player player) {
        if (!InteractionUtil.isInAlternateUseMode(player)) {
            return CuttingResult.NOT;
        }
        var offHand = player.getItemInHand(InteractionHand.OFF_HAND);
        if (!(offHand.getItem() instanceof QuartzCuttingKnifeItem)) {
            return CuttingResult.NOT;
        }

        var stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (stack.getItem() instanceof IMemoryCard card) {
            // only can copy when in offhand
            if (card.getData(stack).isEmpty()) {
                return CuttingResult.YES;
            } else {
                return CuttingResult.DANGER;
            }
        } else {
            return CuttingResult.NOT;
        }
    }

    public static void sendCutInfo(Player player) {
        if (player instanceof ServerPlayer) {
            player.sendSystemMessage(Component.translatable(CUT));
        }
    }

    public static void sendDangerousWarning(Player player) {
        if (player instanceof ServerPlayer) {
            player.sendSystemMessage(Component.translatable(CONFIRM_CUT));
        }
    }
}
