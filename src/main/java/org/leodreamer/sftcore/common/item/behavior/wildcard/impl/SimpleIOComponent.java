package org.leodreamer.sftcore.common.item.behavior.wildcard.impl;

import org.leodreamer.sftcore.common.item.behavior.wildcard.WildcardIOSerializers;
import org.leodreamer.sftcore.common.item.behavior.wildcard.feature.IWildcardIOComponent;
import org.leodreamer.sftcore.common.item.behavior.wildcard.feature.IWildcardIOSerializer;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.gui.widget.PhantomSlotWidget;
import com.gregtechceu.gtceu.api.gui.widget.SlotWidget;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.utils.GTMath;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Items;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.TextFieldWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;

// TODO: allow fluid
public class SimpleIOComponent implements IWildcardIOComponent {

    private GenericStack stack;

    private SlotWidget itemSlot;
    private TextFieldWidget amountEdit;
    static IGuiTexture GROUP_BG = ResourceBorderTexture.BUTTON_COMMON.copy().setColor(ColorPattern.CYAN.color);

    public static SimpleIOComponent EMPTY = new SimpleIOComponent(new GenericStack(AEItemKey.of(Items.AIR), 1));

    public SimpleIOComponent(GenericStack stack) {
        this.stack = stack;
    }

    @Override
    public GenericStack apply(Material material) {
        return stack;
    }

    @Override
    public IWildcardIOSerializer getSerializer() {
        return WildcardIOSerializers.SIMPLE;
    }

    @Override
    public void createUILine(WidgetGroup line) {
        line.setBackground(GROUP_BG);

        itemSlot = new PhantomSlotWidget(new CustomItemStackHandler(), 0, 3, 3, (s) -> true);
        if (stack.what() instanceof AEItemKey item) {
            itemSlot.setItem(item.toStack());
        }
        amountEdit = new TextFieldWidget(80, 5, 50, 15, () -> amountEdit.toString(), (str) -> {});
        amountEdit.setNumbersOnly(0, Integer.MAX_VALUE);
        amountEdit.setCurrentString(GTMath.saturatedCast(stack.amount()));

        line.addWidget(itemSlot);
        line.addWidget(new LabelWidget(70, 7, "x"));
        line.addWidget(amountEdit);
    }

    @Override
    public void onSave() {
        var itemStack = itemSlot.getItem().copy();
        int count = Integer.parseInt(amountEdit.getCurrentString());
        itemStack.setCount(count);
        stack = GenericStack.fromItemStack(itemStack);
    }

    public static class Serializer implements IWildcardIOSerializer {

        @Override
        public String key() {
            return "simple";
        }

        @Override
        public CompoundTag writeToNBT(IWildcardIOComponent component) {
            if (component instanceof SimpleIOComponent simple) {
                return GenericStack.writeTag(simple.stack);
            }
            return new CompoundTag();
        }

        @Override
        public SimpleIOComponent readFromNBT(CompoundTag nbt) {
            return new SimpleIOComponent(GenericStack.readTag(nbt));
        }
    }
}
