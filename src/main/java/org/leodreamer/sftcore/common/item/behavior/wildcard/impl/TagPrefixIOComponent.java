package org.leodreamer.sftcore.common.item.behavior.wildcard.impl;

import org.leodreamer.sftcore.common.item.behavior.wildcard.WildcardIOSerializers;
import org.leodreamer.sftcore.common.item.behavior.wildcard.feature.IWildcardIOComponent;
import org.leodreamer.sftcore.common.item.behavior.wildcard.feature.IWildcardIOSerializer;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.gui.widget.PhantomSlotWidget;
import com.gregtechceu.gtceu.api.gui.widget.SlotWidget;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.utils.GTMath;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import appeng.api.stacks.GenericStack;
import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.TextFieldWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;

public class TagPrefixIOComponent implements IWildcardIOComponent {

    private TagPrefix tag;
    private int amount;

    private SlotWidget itemSlot;
    private LabelWidget tagLabel;
    private TextFieldWidget amountEdit;
    static IGuiTexture GROUP_BG = ResourceBorderTexture.BUTTON_COMMON.copy().setColor(ColorPattern.PURPLE.color);

    public static TagPrefixIOComponent EMPTY = new TagPrefixIOComponent(TagPrefix.NULL_PREFIX, 1);

    public TagPrefixIOComponent(TagPrefix tag, int amount) {
        this.tag = tag;
        this.amount = amount;
    }

    @Override
    public GenericStack apply(Material material) {
        var stack = ChemicalHelper.get(tag, material, amount);
        return GenericStack.fromItemStack(stack);
    }

    @Override
    public IWildcardIOSerializer getSerializer() {
        return WildcardIOSerializers.TAG_PREFIX;
    }

    @Override
    public void createUILine(WidgetGroup line) {
        line.setBackground(GROUP_BG);

        itemSlot = new PhantomSlotWidget(new CustomItemStackHandler(), 0, 3, 3, (stack) -> {
            if (getItemPrefix(stack) != TagPrefix.NULL_PREFIX) {
                updateItemAndTag(stack);
                return true;
            }
            return false;
        });

        if (tag != TagPrefix.NULL_PREFIX) {
            itemSlot.setItem(findExampleForPrefix(tag));
        }

        tagLabel = new LabelWidget(25, 7, tag.name);

        amountEdit = new TextFieldWidget(80, 5, 50, 15, () -> amountEdit.toString(), (str) -> {});
        amountEdit.setNumbersOnly(0, Integer.MAX_VALUE);
        amountEdit.setCurrentString(GTMath.saturatedCast(amount));

        line.addWidget(itemSlot);
        line.addWidget(tagLabel);
        line.addWidget(new LabelWidget(70, 7, "x"));
        line.addWidget(amountEdit);
    }

    private void updateItemAndTag(ItemStack stack) {
        tag = getItemPrefix(stack);
        tagLabel.setText(tag.name);
    }

    @Override
    public void onSave() {
        updateItemAndTag(itemSlot.getItem());
        amount = Integer.parseInt(amountEdit.getCurrentString());
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

    public static class Serializer implements IWildcardIOSerializer {

        @Override
        public String key() {
            return "tag_prefix";
        }

        @Override
        public CompoundTag writeToNBT(IWildcardIOComponent component) {
            if (component instanceof TagPrefixIOComponent tagComponent) {
                var nbt = new CompoundTag();
                nbt.putString("tag", tagComponent.tag.name);
                nbt.putInt("amount", tagComponent.amount);
                return nbt;
            }
            return new CompoundTag();
        }

        @Override
        public IWildcardIOComponent readFromNBT(CompoundTag nbt) {
            var tagName = nbt.getString("tag");
            var amount = nbt.getInt("amount");
            var tag = TagPrefix.get(tagName);
            return new TagPrefixIOComponent(tag, amount);
        }
    }
}
