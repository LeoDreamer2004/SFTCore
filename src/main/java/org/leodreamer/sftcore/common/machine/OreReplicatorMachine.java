package org.leodreamer.sftcore.common.machine;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IMuiMachine;
import com.gregtechceu.gtceu.api.machine.trait.notifiable.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.sync_system.annotations.SaveField;
import com.gregtechceu.gtceu.common.mui.GTGuiTextures;
import com.gregtechceu.gtceu.utils.ISubscription;

import net.minecraft.core.BlockPos;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;

import brachy.modularui.factory.PosGuiData;
import brachy.modularui.screen.UISettings;
import brachy.modularui.value.sync.PanelSyncManager;
import brachy.modularui.widget.ParentWidget;
import brachy.modularui.widgets.layout.Grid;
import brachy.modularui.widgets.slot.ItemSlot;
import brachy.modularui.widgets.slot.ModularSlot;
import com.mojang.blaze3d.MethodsReturnNonnullByDefault;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class OreReplicatorMachine extends MetaMachine implements IMuiMachine {

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
    public void buildMainUI(
        ParentWidget<?> mainWidget,
        PosGuiData guiData,
        PanelSyncManager syncManager,
        UISettings settings
    ) {
        mainWidget.child(
            new Grid()
                .coverChildren()
                .center()
                .gridOfSizeWidth(
                    1, 1, (x, y, index) -> new ItemSlot()
                        .slot(new ModularSlot(inventory.storage, 0).accessibility(false, true))
                        .background(GTGuiTextures.SLOT, GTGuiTextures.OUT_SLOT_OVERLAY)
                )
        );
    }
}
