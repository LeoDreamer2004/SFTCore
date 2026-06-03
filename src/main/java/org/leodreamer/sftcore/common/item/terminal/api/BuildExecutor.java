package org.leodreamer.sftcore.common.item.terminal.api;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public final class BuildExecutor {

    private BuildExecutor() {}

    public static BuildReport execute(BuildContext ctx, BuildPlan plan) {
        var report = new BuildReport();

        for (var placement : plan.placements()) {
            if (placeOne(ctx, placement, report)) {
                report.placed++;
            }
        }

        return report;
    }

    private static boolean placeOne(BuildContext ctx, Placement placement, BuildReport report) {
        var pos = placement.pos();
        var oldState = ctx.level().getBlockState(pos);

        if (oldState.is(placement.state().getBlock())) {
            report.existing++;
            return false;
        }

        if (!oldState.isAir()) {
            report.blocked++;
            return false;
        }

        if (placement.consumeItem() && !consume(ctx.player(), placement.requiredItem())) {
            report.addMissing(placement.requiredItem());
            return false;
        }

        boolean ok = ctx.level().setBlock(pos, placement.state(), Block.UPDATE_ALL);
        if (!ok) {
            report.failed++;

            if (placement.consumeItem() && !ctx.player().getAbilities().instabuild) {
                ctx.player().getInventory().add(new ItemStack(placement.requiredItem()));
            }
        }

        return ok;
    }

    private static boolean consume(Player player, Item item) {
        if (player.getAbilities().instabuild) {
            return true;
        }

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            var stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && stack.is(item)) {
                stack.shrink(1);
                player.getInventory().setChanged();
                return true;
            }
        }

        return false;
    }
}
