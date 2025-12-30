package org.leodreamer.sftcore.common.item.behavior.wildcard.impl;

import appeng.api.stacks.GenericStack;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.gui.widget.PhantomFluidWidget;
import com.gregtechceu.gtceu.api.transfer.fluid.CustomFluidTank;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.utils.GTMath;
import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.TextFieldWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import org.leodreamer.sftcore.api.gui.PhantomTagPrefixSlot;
import org.leodreamer.sftcore.common.item.behavior.wildcard.WildcardIOSerializers;
import org.leodreamer.sftcore.common.item.behavior.wildcard.feature.IWildcardIOComponent;
import org.leodreamer.sftcore.common.item.behavior.wildcard.feature.IWildcardIOSerializer;

public class TagPrefixIOComponent implements IWildcardIOComponent {

    private TagPrefix tag;
    private int amount;

    private PhantomTagPrefixSlot tagSlot;
    private LabelWidget tagLabel;
    private TextFieldWidget amountEdit;
    static IGuiTexture GROUP_BG = ResourceBorderTexture.BUTTON_COMMON.copy().setColor(ColorPattern.PURPLE.color);

    public static TagPrefixIOComponent empty() {
        return new TagPrefixIOComponent(TagPrefix.NULL_PREFIX, 1);
    }

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

        tagSlot = new PhantomTagPrefixSlot(new CustomItemStackHandler(), 0, 3, 3, this::updateTag);

        if (tag != TagPrefix.NULL_PREFIX) {
            tagSlot.setTag(tag);
        }
        tagLabel = new LabelWidget(25, 7, tag.name);

        amountEdit = new TextFieldWidget(80, 5, 50, 15, () -> amountEdit.toString(), (str) -> {
        });
        amountEdit.setNumbersOnly(0, Integer.MAX_VALUE);
        amountEdit.setCurrentString(GTMath.saturatedCast(amount));

        line.addWidget(tagSlot);
        line.addWidget(tagLabel);
        line.addWidget(new LabelWidget(70, 7, "x"));
        line.addWidget(amountEdit);
    }

    private boolean updateTag(TagPrefix tag) {
        var ok = tag != TagPrefix.NULL_PREFIX;
        if (ok) {
            tagLabel.setText(tag.name);
            this.tag = tag;
        }
        return ok;
    }

    @Override
    public void onSave() {
        updateTag(tagSlot.getTag());
        amount = Integer.parseInt(amountEdit.getCurrentString());
    }

    @Override
    public String toString() {
        return "Component " + tag.name + " x " + amount;
    }

    public static class Serializer implements IWildcardIOSerializer {

        @Override
        public String key() {
            return "tag_prefix";
        }

        @Override
        public CompoundTag writeToNBT(IWildcardIOComponent component) {
            var tagComponent = (TagPrefixIOComponent) component;
            var nbt = new CompoundTag();
            nbt.putString("tag", tagComponent.tag.name);
            nbt.putInt("amount", tagComponent.amount);
            return nbt;
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
