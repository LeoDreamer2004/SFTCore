package org.leodreamer.sftcore.common.machine.multiblock.part;

import org.leodreamer.sftcore.api.feature.IMachineAdjustment;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.widget.BlockableSlotWidget;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.sync_system.annotations.SaveField;
import com.gregtechceu.gtceu.utils.ISubscription;

import net.minecraft.world.item.ItemStack;

import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.DUMMY_RECIPES;

public class MachineAdjustmentHatchPartMachine extends TieredIOPartMachine implements IMachineAdjustment {

    @SaveField
    @Getter
    private final NotifiableItemStackHandler inventory;

    @Getter
    @NotNull
    private GTRecipeType recipeType = DUMMY_RECIPES;

    @Getter
    private int tier;

    public MachineAdjustmentHatchPartMachine(BlockEntityCreationInfo info) {
        super(info, GTValues.LV, IO.NONE);

        this.inventory = attachTrait(
            new NotifiableItemStackHandler(1, IO.NONE, IO.NONE)
                .shouldSearchContent(false)
        );

        this.inventory.addChangedListener(this::updateMachineSubscription);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        updateMachineSubscription();
    }

    @Override
    public boolean canShared() {
        return false;
    }

    protected void updateMachineSubscription() {
        var def = getInnerMachineDefinition();
        if (def == null) {
            clearCache();
            return;
        }

        var recipeTypes = def.getRecipeTypes();
        if (recipeTypes.length == 0) {
            clearCache();
            return;
        }

        // small machine should only have one recipe type
        this.recipeType = recipeTypes[0];
        this.tier = def.getTier();
    }

    private void clearCache() {
        this.recipeType = DUMMY_RECIPES;
        this.tier = 0;
    }

    @Override
    public @NotNull Widget createUIWidget() {
        var group = new WidgetGroup(0, 0, 18 + 16, 18 + 16);

        var container = new WidgetGroup(4, 4, 18 + 8, 18 + 8);
        container.addWidget(
            new BlockableSlotWidget(inventory.storage, 0, 4, 4)
                .setBackground(GuiTextures.SLOT, GuiTextures.IN_SLOT_OVERLAY)
        );
        container.setBackground(GuiTextures.BACKGROUND_INVERSE);

        group.addWidget(container);
        return group;
    }

    @Override
    public ItemStack getInnerMachineStack() {
        return inventory.getStackInSlot(0);
    }

    @Override
    public ISubscription addListenerOnChanged(Consumer<IMachineAdjustment> listener) {
        return inventory.addChangedListener(() -> {
            updateMachineSubscription();
            listener.accept(this);
        });
    }
}
