package org.leodreamer.sftcore.common.machine;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.common.machine.multiblock.part.ItemBusPartMachine;

import net.minecraft.core.BlockPos;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;

import com.lowdragmc.lowdraglib.syncdata.ISubscription;
import com.mojang.blaze3d.MethodsReturnNonnullByDefault;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class OreReplicatorMachine extends ItemBusPartMachine {

    @Nullable
    private Item oreCache;
    @Nullable
    private ISubscription oreInventorySubs;

    public OreReplicatorMachine(IMachineBlockEntity holder) {
        super(holder, GTValues.ULV, IO.OUT);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (getLevel() instanceof ServerLevel serverLevel) {
            serverLevel.getServer().tell(new TickTask(0, this::redetectAboveOre));
        }
        oreInventorySubs = getInventory().addChangedListener(this::fillInventory);
    }

    @Override
    public void onUnload() {
        super.onUnload();
        if (oreInventorySubs != null) {
            oreInventorySubs.unsubscribe();
            oreInventorySubs = null;
        }
    }

    @Override
    public void onNeighborChanged(Block block, BlockPos fromPos, boolean isMoving) {
        super.onNeighborChanged(block, fromPos, isMoving);
        if (fromPos.equals(getPos().above())) {
            redetectAboveOre();
        }
    }

    private void redetectAboveOre() {
        var world = getLevel();
        if (world == null) return;

        var above = world.getBlockState(getPos().above());
        if (isOre(above)) {
            oreCache = above.getBlock().asItem();
            fillInventory();
        } else {
            oreCache = null;
        }
    }

    private static boolean isOre(BlockState state) {
        return state.is(Tags.Blocks.ORES);
    }

    private void fillInventory() {
        if (oreCache == null) return;
        var stack = getInventory().getStackInSlot(0);
        if (stack.getCount() == 64) return;
        if (stack.isEmpty() || stack.is(oreCache)) {
            stack = new ItemStack(oreCache, 64);
            getInventory().setStackInSlot(0, stack);
        }
    }
}
