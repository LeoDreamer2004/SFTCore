package org.leodreamer.sftcore.common.item;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.command.dump.DumpCommand;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;

import org.jetbrains.annotations.NotNull;

@DataGenScanned
public class SelectStickItem extends Item {

    @RegisterLanguage("Select the first point at %s")
    static final String selectFirst = "item.sftcore.dump.select_stick.first";

    @RegisterLanguage("Select the second point at %s")
    static final String selectSecond = "item.sftcore.dump.select_stick.second";

    public SelectStickItem(Properties properties) {
        super(properties.rarity(Rarity.EPIC));
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;

        BlockPos pos = context.getClickedPos();
        String posStr = pos.getX() + " " + pos.getY() + " " + pos.getZ();
        if (!player.isSecondaryUseActive()) {
            DumpCommand.SelectedData.setSelectedPos1(player, pos);
            if (context.getLevel().isClientSide)
                player.sendSystemMessage(Component.translatable(selectFirst, posStr));
        } else {
            DumpCommand.SelectedData.setSelectedPos2(player, pos);
            if (context.getLevel().isClientSide)
                player.sendSystemMessage(Component.translatable(selectSecond, posStr));
        }
        return InteractionResult.PASS;
    }
}
