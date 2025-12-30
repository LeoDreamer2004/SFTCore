package org.leodreamer.sftcore.api.gui;


import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.gui.widget.PhantomSlotWidget;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.function.Predicate;

public class PhantomTagPrefixSlot extends PhantomSlotWidget {
    public PhantomTagPrefixSlot(IItemHandlerModifiable itemHandler, int slotIndex, int xPosition, int yPosition, Predicate<TagPrefix> validator) {
        super(itemHandler, slotIndex, xPosition, yPosition, (stack) -> validator.test(getItemPrefix(stack)));
    }

    public void setTag(TagPrefix tag) {
        setItem(findExampleForPrefix(tag));
    }

    public TagPrefix getTag() {
        return getItemPrefix(getItem());
    }

    private static ItemStack findExampleForPrefix(TagPrefix tag) {
        for (var mat : GTCEuAPI.materialManager.getRegisteredMaterials()) {
            var stack = ChemicalHelper.get(tag, mat);
            if (!stack.isEmpty()) return stack;
        }
        return ItemStack.EMPTY;
    }

    private static TagPrefix getItemPrefix(ItemStack stack) {
        return ChemicalHelper.getPrefix(stack.getItem());
    }
}
