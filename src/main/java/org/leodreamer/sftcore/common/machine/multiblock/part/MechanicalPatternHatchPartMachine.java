package org.leodreamer.sftcore.common.machine.multiblock.part;

import org.leodreamer.sftcore.common.data.SFTItems;
import org.leodreamer.sftcore.common.machine.multiblock.MechanicalBoxMachine;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.feature.IMuiMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.sync_system.annotations.SaveField;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.common.mui.GTGuiTextures;

import net.minecraft.world.item.ItemStack;

import brachy.modularui.factory.PosGuiData;
import brachy.modularui.screen.UISettings;
import brachy.modularui.value.sync.PanelSyncManager;
import brachy.modularui.widget.ParentWidget;
import brachy.modularui.widgets.layout.Grid;
import brachy.modularui.widgets.slot.ItemSlot;
import brachy.modularui.widgets.slot.ModularSlot;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class MechanicalPatternHatchPartMachine extends TieredIOPartMachine implements IMuiMachine {

    @SaveField
    @Getter
    private final NotifiableItemStackHandler inventory;

    public MechanicalPatternHatchPartMachine(BlockEntityCreationInfo info) {
        super(info, GTValues.LV, IO.NONE);
        this.inventory = attachTrait(
            new NotifiableItemStackHandler(9, IO.NONE, IO.NONE, SingleItemStackHandler::new)
                .setFilter(stack -> stack.is(SFTItems.CREATE_ENCAPSULATION_PATTERN.asItem()))
                .shouldSearchContent(false)
        );
        this.inventory.addChangedListener(this::onPatternsChanged);
    }

    @Override
    public boolean canShared(MultiblockControllerMachine controller, String substructureName) {
        return false;
    }

    public List<ItemStack> getEncodedPatterns() {
        var patterns = new ArrayList<ItemStack>();
        for (int i = 0; i < inventory.getSlots(); i++) {
            patterns.add(inventory.getStackInSlot(i));
        }
        return patterns;
    }

    private void onPatternsChanged() {
        getControllers()
            .stream()
            .filter(MechanicalBoxMachine.class::isInstance)
            .map(MechanicalBoxMachine.class::cast)
            .forEach(MechanicalBoxMachine::onPatternsChanged);
    }

    @Override
    public void buildMainUI(
        ParentWidget<?> mainWidget, PosGuiData guiData, PanelSyncManager syncManager,
        UISettings settings
    ) {
        mainWidget.child(
            new Grid()
                .coverChildren()
                .center()
                .gridOfSizeWidth(
                    9, 3, (x, y, index) -> new ItemSlot()
                        .slot(new ModularSlot(inventory.storage, index))
                        .background(GTGuiTextures.SLOT)
                )
        );
    }

    private static class SingleItemStackHandler extends CustomItemStackHandler {

        public SingleItemStackHandler(int size) {
            super(size);
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }
    }
}
