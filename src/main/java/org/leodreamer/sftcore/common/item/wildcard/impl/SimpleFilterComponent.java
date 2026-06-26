package org.leodreamer.sftcore.common.item.wildcard.impl;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.wildcard.feature.WildcardFilterComponent;
import org.leodreamer.sftcore.common.item.wildcard.handler.GTMaterialHandler;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.common.data.GTMaterials;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import brachy.modularui.drawable.Rectangle;
import brachy.modularui.value.sync.PanelSyncManager;
import brachy.modularui.widgets.layout.Flow;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;

@DataGenScanned
public class SimpleFilterComponent extends WildcardFilterComponent {

    public static final Codec<SimpleFilterComponent> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            GTMaterialHandler.MATERIAL_CODEC.optionalFieldOf("material", GTMaterials.NULL)
                .forGetter(component -> component.material),
            Codec.BOOL.optionalFieldOf("whitelist", false)
                .forGetter(component -> component.whitelist)
        ).apply(instance, SimpleFilterComponent::new)
    );

    @Getter
    private Material material;
    private GTMaterialHandler example;
    private static final int GROUP_BG_WHITE = 0xFF44AAFF;
    private static final int GROUP_BG_BLACK = 0xFF4852FF;

    @RegisterLanguage("Material")
    public static final String LABEL = "item.sftcore.wildcard_pattern.ui.filter.material";

    public static SimpleFilterComponent empty() {
        return new SimpleFilterComponent(GTMaterials.NULL, false);
    }

    public SimpleFilterComponent(Material material, boolean whitelist) {
        super(whitelist);
        this.material = material == null ? GTMaterials.NULL : material;
    }

    @Override
    public boolean test(Material material) {
        if (this.material == GTMaterials.NULL) {
            return true;
        }
        return whitelist == (this.material == material);
    }

    @Override
    protected void addLineContent(
        Flow row,
        PanelSyncManager syncManager,
        String lineSyncKey
    ) {
        var sampleSyncHandler = registerSampleSlot(
            new GTMaterialHandler(material), GTMaterialHandler.class, syncManager, lineSyncKey
        );
        this.example = sampleSlotHandler(sampleSyncHandler, GTMaterialHandler.class);
        this.example.setMaterial(material);
        row.child(createTypeButton(52, LABEL));
        row.child(createWhitelistButton(row));
        row.child(createSampleSlot(sampleSyncHandler));
    }

    @Override
    public void onSave() {
        material = example == null ? material : example.getMaterial();
    }

    @Override
    public Component createTooltip() {
        return whitelistTooltip().append(" ").append(Component.translatable(LABEL))
            .append(Component.literal(" " + getMaterialString(material)).withStyle(ChatFormatting.BLUE));
    }

    @RegisterLanguage("No material")
    private static final String NO_MATERIAL = "item.sftcore.wildcard_pattern.filter.simple.no_material";

    private static String getMaterialString(Material material) {
        if (material == GTMaterials.NULL) {
            return Component.translatable(NO_MATERIAL).getString();
        }

        return material.getLocalizedName().getString();
    }

    @Override
    protected Rectangle rowBackground() {
        return new Rectangle().color(whitelist ? GROUP_BG_WHITE : GROUP_BG_BLACK);
    }
}
