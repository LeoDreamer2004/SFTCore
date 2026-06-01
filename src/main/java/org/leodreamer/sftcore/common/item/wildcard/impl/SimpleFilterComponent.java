package org.leodreamer.sftcore.common.item.wildcard.impl;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.wildcard.WildcardCodecUtils;
import org.leodreamer.sftcore.common.item.wildcard.feature.IWildcardFilterComponent;
import org.leodreamer.sftcore.integration.ae2.gui.PhantomGTMaterialSlot;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.common.data.GTMaterials;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;

@DataGenScanned
public class SimpleFilterComponent implements IWildcardFilterComponent {

    public static final Codec<SimpleFilterComponent> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            WildcardCodecUtils.MATERIAL_CODEC.optionalFieldOf("material", GTMaterials.NULL)
                .forGetter(component -> component.material),
            Codec.BOOL.optionalFieldOf("whitelist", false)
                .forGetter(component -> component.whitelist)
        ).apply(instance, SimpleFilterComponent::new)
    );

    private Material material;

    @Getter
    private boolean whitelist;

    private PhantomGTMaterialSlot materialSlot;
    private LabelWidget materialLabel;
    private WidgetGroup parent = null;

    private static final IGuiTexture GROUP_BG_WHITE = ResourceBorderTexture.BUTTON_COMMON.copy()
        .setColor(ColorPattern.LIGHT_BLUE.color);

    private static final IGuiTexture GROUP_BG_BLACK = ResourceBorderTexture.BUTTON_COMMON.copy()
        .setColor(ColorPattern.BLUE.color);

    public static SimpleFilterComponent empty() {
        return new SimpleFilterComponent(GTMaterials.NULL, false);
    }

    public SimpleFilterComponent(Material material, boolean whitelist) {
        this.material = material;
        this.whitelist = whitelist;
    }

    @Override
    public void setWhitelist(boolean whiteList) {
        this.whitelist = whiteList;

        if (parent != null) {
            parent.setBackground(whiteList ? GROUP_BG_WHITE : GROUP_BG_BLACK);
        }
    }

    @Override
    public boolean test(Material material) {
        return whitelist == (this.material == material);
    }

    @Override
    public void createUILine(WidgetGroup line) {
        line.setBackground(whitelist ? GROUP_BG_WHITE : GROUP_BG_BLACK);
        parent = line;

        materialSlot = new PhantomGTMaterialSlot(new CustomItemStackHandler(), 0, 3, 3, this::updateMaterial);
        materialLabel = new LabelWidget(25, 7, getMaterialString(material));

        if (material != GTMaterials.NULL) {
            materialSlot.setMaterial(material);
        }

        line.addWidget(materialSlot);
        line.addWidget(materialLabel);
    }

    @RegisterLanguage("Material")
    private static final String SIMPLE_TOOLTIP_KEY = "sftcore.item.wildcard_pattern.tooltip.filter.simple";

    @Override
    public Component createTooltip() {
        return whitelistTooltip().append(" ").append(Component.translatable(SIMPLE_TOOLTIP_KEY))
            .append(Component.literal(" " + getMaterialString(material)).withStyle(ChatFormatting.BLUE));
    }

    private boolean updateMaterial(Material material) {
        var ok = material != GTMaterials.NULL;

        if (ok) {
            this.material = material;

            if (materialLabel != null) {
                materialLabel.setText(getMaterialString(material));
            }
        }

        return ok;
    }

    @Override
    public void onSave() {
        updateMaterial(materialSlot.getMaterial());
    }

    @RegisterLanguage("No material")
    private static final String NO_MATERIAL = "sftcore.item.wildcard_pattern.filter.simple.no_material";

    private static String getMaterialString(Material material) {
        if (material == GTMaterials.NULL) {
            return Component.translatable(NO_MATERIAL).getString();
        }

        return material.getLocalizedName().getString();
    }
}
