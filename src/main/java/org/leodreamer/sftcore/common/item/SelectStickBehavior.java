package org.leodreamer.sftcore.common.item;

import com.gregtechceu.gtceu.api.item.component.IInteractionItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;
import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.command.dump.DumpCommand;

@DataGenScanned
public class SelectStickBehavior implements IInteractionItem {

    @RegisterLanguage("Select the first point at %s")
    static final String SELECT_FIRST = "item.sftcore.dump.select_stick.first";

    @RegisterLanguage("Select the second point at %s")
    static final String SELECT_SECOND = "item.sftcore.dump.select_stick.second";

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;

        BlockPos pos = context.getClickedPos();
        String posStr = pos.getX() + " " + pos.getY() + " " + pos.getZ();
        if (!player.isSecondaryUseActive()) {
            DumpCommand.SelectedData.setSelectedPos1(player, pos);
            if (context.getLevel().isClientSide)
                player.sendSystemMessage(Component.translatable(SELECT_FIRST, posStr));
        } else {
            DumpCommand.SelectedData.setSelectedPos2(player, pos);
            if (context.getLevel().isClientSide)
                player.sendSystemMessage(Component.translatable(SELECT_SECOND, posStr));
        }
        return InteractionResult.PASS;
    }
}
