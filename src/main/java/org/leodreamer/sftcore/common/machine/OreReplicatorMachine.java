package org.leodreamer.sftcore.common.machine;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.widget.BlockableSlotWidget;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.sync_system.annotations.SaveField;
import com.gregtechceu.gtceu.utils.ISubscription;

import net.minecraft.core.BlockPos;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;

import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.mojang.blaze3d.MethodsReturnNonnullByDefault;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class OreReplicatorMachine extends MetaMachine implements IFancyUIMachine {

    @Getter
    @SaveField
    private final NotifiableItemStackHandler inventory;

    @Nullable
    private Item oreCache;

    @Nullable
    private ISubscription oreInventorySubs;

    public OreReplicatorMachine(BlockEntityCreationInfo info) {
        super(info);

        this.inventory = attachTrait(
            new NotifiableItemStackHandler(1, IO.NONE, IO.OUT)
                .shouldSearchContent(false)
        );
    }

    @Override
    public void onLoad() {
        super.onLoad();

        if (getLevel() instanceof ServerLevel serverLevel) {
            serverLevel.getServer().tell(new TickTask(0, this::redetectAboveOre));
        }

        oreInventorySubs = inventory.addChangedListener(this::fillInventory);
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

        if (fromPos.equals(getBlockPos().above())) {
            redetectAboveOre();
        }
    }

    private void redetectAboveOre() {
        var world = getLevel();
        if (world == null) {
            return;
        }

        var above = world.getBlockState(getBlockPos().above());
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
        if (oreCache == null) {
            return;
        }

        var stack = inventory.getStackInSlot(0);
        if (stack.getCount() >= stack.getMaxStackSize()) {
            return;
        }

        if (stack.isEmpty() || stack.is(oreCache)) {
            inventory.setStackInSlot(0, new ItemStack(oreCache, oreCache.getDefaultInstance().getMaxStackSize()));
        }
    }

    @Override
    public Widget createUIWidget() {
        var group = new WidgetGroup(0, 0, 18 + 16, 18 + 16);

        var container = new WidgetGroup(4, 4, 18 + 8, 18 + 8);
        container.addWidget(
            new BlockableSlotWidget(inventory.storage, 0, 4, 4)
                .setBackground(GuiTextures.SLOT, GuiTextures.OUT_SLOT_OVERLAY)
        );
        container.setBackground(GuiTextures.BACKGROUND_INVERSE);

        group.addWidget(container);
        return group;
    }
}
