package org.leodreamer.sftcore.common.api.feature;

import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.lowdragmc.lowdraglib.syncdata.ISubscription;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface IMachineAdjustment extends IMultiPart {
    ItemStack getInnerMachineStack();

    @Nullable
    default MachineDefinition getInnerMachineDefinition() {
        var stack = getInnerMachineStack();
        if (stack.isEmpty()) {
            return null;
        }
        var item = stack.getItem();
        var rl = ForgeRegistries.ITEMS.getKey(item);
        var def = GTRegistries.MACHINES.get(rl);
        if (def == null || def instanceof MultiblockMachineDefinition
                || def.getRecipeTypes().length == 0)
            return null;
        return def;
    }

    @NotNull
    GTRecipeType getRecipeType();

    int getTier();

    ISubscription addListenerOnChanged(Consumer<IMachineAdjustment> listener);
}
