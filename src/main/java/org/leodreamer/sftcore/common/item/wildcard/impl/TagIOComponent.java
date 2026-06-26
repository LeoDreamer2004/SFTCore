package org.leodreamer.sftcore.common.item.wildcard.impl;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.wildcard.feature.WildcardIOComponent;
import org.leodreamer.sftcore.common.item.wildcard.handler.GTTagHandler;
import org.leodreamer.sftcore.integration.ae2.item.GenericGTTag;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import appeng.api.stacks.GenericStack;
import brachy.modularui.api.drawable.Text;
import brachy.modularui.drawable.Rectangle;
import brachy.modularui.utils.Alignment;
import brachy.modularui.utils.Color;
import brachy.modularui.value.sync.PanelSyncManager;
import brachy.modularui.widgets.TextWidget;
import brachy.modularui.widgets.layout.Flow;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

@Accessors(fluent = true)
@DataGenScanned
public class TagIOComponent extends WildcardIOComponent {

    public static final Codec<TagIOComponent> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            GenericGTTag.CODEC.optionalFieldOf("tag", GenericGTTag.EMPTY).forGetter(component -> component.tag),
            Codec.INT.optionalFieldOf("amount", 1).forGetter(component -> component.amount)
        ).apply(instance, TagIOComponent::new)
    );

    @Getter
    private GenericGTTag tag;
    @Getter
    private int amount;
    private GTTagHandler sample;
    private String amountText;
    private static final int GROUP_BG = 0xFF9933FF;

    @RegisterLanguage("Tag")
    public static final String LABEL = "item.sftcore.wildcard_pattern.ui.io.tag";

    public static TagIOComponent empty() {
        return new TagIOComponent(GenericGTTag.EMPTY, 1);
    }

    public TagIOComponent(GenericGTTag tag, int amount) {
        this.tag = tag == null ? GenericGTTag.EMPTY : tag;
        this.amount = amount;
        this.amountText = Integer.toString(amount);
    }

    @Override
    public @Nullable GenericStack apply(Material material) {
        if (tag.equals(GenericGTTag.EMPTY)) {
            return null;
        }
        return tag.toGenericStack(material, amount);
    }

    @Override
    protected void addLineContent(
        Flow row,
        PanelSyncManager syncManager,
        String lineSyncKey
    ) {
        var sampleSyncHandler = registerSampleSlot(new GTTagHandler(tag), GTTagHandler.class, syncManager, lineSyncKey);
        this.sample = sampleSlotHandler(sampleSyncHandler, GTTagHandler.class);
        this.sample.setTag(tag);
        row.child(createTypeButton(32, LABEL));
        row.child(createSampleSlot(sampleSyncHandler));
        row.child(
            new TextWidget<>(Text.dynamic(() -> Component.literal(currentTagName())))
                .width(63)
                .height(16)
                .maxWidth(63)
                .textAlign(Alignment.Center)
                .color(Color.WHITE.main)
                .tooltipDynamic(tooltip -> tooltip.addLine(Text.dynamic(() -> Component.literal(currentTagName()))))
        );
        row.child(Text.str("x").asWidget().width(12).textAlign(Alignment.Center).marginLeft(2));
        row.child(amountField(34, () -> amountText, text -> amountText = text));
    }

    @Override
    public Component createTooltip() {
        return Component.literal(tag.name()).withStyle(ChatFormatting.LIGHT_PURPLE)
            .append(Component.literal(" x " + amount).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public void onSave() {
        tag = sample == null ? tag : sample.getTag();
        amount = parseIntAmount(amountText);
    }

    @Override
    public String toString() {
        return "Component " + tag.name() + " x " + amount;
    }

    private String currentTagName() {
        return (sample == null ? tag : sample.getTag()).name();
    }

    @Override
    protected Rectangle rowBackground() {
        return new Rectangle().color(GROUP_BG);
    }
}
