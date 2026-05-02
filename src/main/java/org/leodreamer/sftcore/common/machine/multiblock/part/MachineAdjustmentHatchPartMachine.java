package org.leodreamer.sftcore.common.machine.multiblock.part;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.widget.BlockableSlotWidget;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.common.machine.multiblock.part.ItemBusPartMachine;
import com.gregtechceu.gtceu.utils.ISubscription;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import lombok.Getter;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.leodreamer.sftcore.api.feature.IMachineAdjustment;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.DUMMY_RECIPES;

public class MachineAdjustmentHatchPartMachine extends ItemBusPartMachine
    implements IMachineAdjustment {

    @Getter
    @NotNull
    private GTRecipeType recipeType = DUMMY_RECIPES;

    @Getter
    private int tier;

    public MachineAdjustmentHatchPartMachine(BlockEntityCreationInfo info) {
        super(info, GTValues.LV, IO.IN);
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

        // small machine should only have one recipe type
        this.recipeType = def.getRecipeTypes()[0];
        this.tier = def.getTier();
    }

    private void clearCache() {
        this.recipeType = DUMMY_RECIPES;
        this.tier = 0;
    }

    //////////////////////////////////////
    // ********** GUI ***********//

    /// ///////////////////////////////////
    @Override
    public @NotNull Widget createUIWidget() {
        var group = new WidgetGroup(0, 0, 18 + 16, 18 + 16);
        var container = new WidgetGroup(4, 4, 18 + 8, 18 + 8);
        container.addWidget(
            new BlockableSlotWidget(getInventory().storage, 0, 4, 4)
                .setBackground(GuiTextures.SLOT, GuiTextures.IN_SLOT_OVERLAY)
        );
        container.setBackground(GuiTextures.BACKGROUND_INVERSE);
        group.addWidget(container);
        return group;
    }

    @Override
    public ItemStack getInnerMachineStack() {
        return getInventory().getStackInSlot(0);
    }

    @Override
    public ISubscription addListenerOnChanged(Consumer<IMachineAdjustment> listener) {
        return getInventory().addChangedListener(
            () -> {
                updateMachineSubscription();
                listener.accept(this);
            }
        );
    }
}
