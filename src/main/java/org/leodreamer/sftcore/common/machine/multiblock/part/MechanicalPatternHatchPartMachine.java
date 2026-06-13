package org.leodreamer.sftcore.common.machine.multiblock.part;

import org.leodreamer.sftcore.common.item.mechanical.MechanicalEncapsulationPatternLogic;
import org.leodreamer.sftcore.common.machine.multiblock.MechanicalBoxMachine;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.widget.BlockableSlotWidget;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.sync_system.annotations.SaveField;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;

import net.minecraft.world.item.ItemStack;

import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MechanicalPatternHatchPartMachine extends TieredIOPartMachine {

    @SaveField
    @Getter
    private final NotifiableItemStackHandler inventory;

    public MechanicalPatternHatchPartMachine(BlockEntityCreationInfo info) {
        super(info, GTValues.LV, IO.NONE);
        this.inventory = attachTrait(
            new NotifiableItemStackHandler(9, IO.NONE, IO.NONE, SingleItemStackHandler::new)
                .setFilter(MechanicalEncapsulationPatternLogic::isEncoded)
                .shouldSearchContent(false)
        );
        this.inventory.addChangedListener(this::onPatternsChanged);
    }

    @Override
    public boolean canShared() {
        return false;
    }

    public List<ItemStack> getEncodedPatterns() {
        var patterns = new ArrayList<ItemStack>();
        for (int i = 0; i < inventory.getSlots(); i++) {
            var stack = inventory.getStackInSlot(i);
            if (MechanicalEncapsulationPatternLogic.isEncoded(stack)) {
                patterns.add(stack);
            }
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
    public @NotNull Widget createUIWidget() {
        var group = new WidgetGroup(0, 0, 18 * 3 + 16, 18 * 3 + 16);
        var container = new WidgetGroup(4, 4, 18 * 3 + 8, 18 * 3 + 8);
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                int slot = row * 3 + column;
                container.addWidget(
                    new BlockableSlotWidget(inventory.storage, slot, 4 + column * 18, 4 + row * 18)
                        .setBackground(GuiTextures.SLOT)
                );
            }
        }
        container.setBackground(GuiTextures.BACKGROUND_INVERSE);
        group.addWidget(container);
        return group;
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
