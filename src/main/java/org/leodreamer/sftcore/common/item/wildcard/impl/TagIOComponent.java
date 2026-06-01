package org.leodreamer.sftcore.common.item.wildcard.impl;

import org.leodreamer.sftcore.common.item.wildcard.feature.IWildcardIOComponent;
import org.leodreamer.sftcore.integration.ae2.gui.PhantomGTTagSlot;
import org.leodreamer.sftcore.integration.ae2.item.GenericGTTag;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import appeng.api.stacks.GenericStack;
import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.TextFieldWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.Nullable;

public class TagIOComponent implements IWildcardIOComponent {

    public static final Codec<TagIOComponent> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            GenericGTTag.CODEC.optionalFieldOf("tag", GenericGTTag.EMPTY).forGetter(component -> component.tag),
            Codec.INT.optionalFieldOf("amount", 1).forGetter(component -> component.amount)
        ).apply(instance, TagIOComponent::new)
    );

    private GenericGTTag tag;
    private int amount;

    private PhantomGTTagSlot tagSlot;
    private LabelWidget tagLabel;
    private TextFieldWidget amountEdit;

    private static final IGuiTexture GROUP_BG = ResourceBorderTexture.BUTTON_COMMON.copy()
        .setColor(ColorPattern.PURPLE.color);

    public static TagIOComponent empty() {
        return new TagIOComponent(GenericGTTag.EMPTY, 1);
    }

    public TagIOComponent(GenericGTTag tag, int amount) {
        this.tag = tag;
        this.amount = amount;
    }

    @Override
    public @Nullable GenericStack apply(Material material) {
        return tag.toGenericStack(material, amount);
    }

    @Override
    public void createUILine(WidgetGroup line) {
        line.setBackground(GROUP_BG);

        tagSlot = new PhantomGTTagSlot(new CustomItemStackHandler(), 0, 3, 3, this::updateTag);

        if (tag != GenericGTTag.EMPTY) {
            tagSlot.setTag(tag);
        }

        tagLabel = new LabelWidget(25, 7, tag.name());

        amountEdit = new TextFieldWidget(80, 5, 50, 15, this::getAmount, this::setAmount);
        amountEdit.setNumbersOnly(0, Integer.MAX_VALUE);
        amountEdit.setCurrentString(amount);

        line.addWidget(tagSlot);
        line.addWidget(tagLabel);
        line.addWidget(new LabelWidget(70, 7, "x"));
        line.addWidget(amountEdit);
    }

    @Override
    public Component createTooltip() {
        return Component.literal(tag.name()).withStyle(ChatFormatting.LIGHT_PURPLE)
            .append(Component.literal(" x " + getAmount()).withStyle(ChatFormatting.GRAY));
    }

    private boolean updateTag(GenericGTTag tag) {
        var ok = tag != GenericGTTag.EMPTY;

        if (ok) {
            tagLabel.setText(tag.name());
            this.tag = tag;
        }

        return ok;
    }

    private String getAmount() {
        return Integer.toString(amount);
    }

    private void setAmount(String str) {
        if (str == null || str.isEmpty()) {
            return;
        }

        amount = Integer.parseInt(str);
    }

    @Override
    public void onSave() {
        updateTag(tagSlot.getTag());
        amount = Integer.parseInt(amountEdit.getCurrentString());
    }

    @Override
    public String toString() {
        return "Component " + tag.name() + " x " + amount;
    }
}
