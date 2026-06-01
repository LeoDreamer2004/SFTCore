package org.leodreamer.sftcore.common.item.wildcard.impl;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.wildcard.WildcardCodecUtils;
import org.leodreamer.sftcore.common.item.wildcard.feature.IWildcardFilterComponent;
import org.leodreamer.sftcore.integration.ae2.gui.PhantomGTMaterialSlot;
import org.leodreamer.sftcore.mixin.gregtech.data.MaterialPropertiesAccessor;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.common.data.GTMaterials;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.widget.SelectorWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey.EMPTY;

@DataGenScanned
public class PropertyFilterComponent implements IWildcardFilterComponent {

    public static final Codec<PropertyFilterComponent> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            WildcardCodecUtils.PROPERTY_CODEC.optionalFieldOf("flag", EMPTY)
                .forGetter(component -> component.property),
            WildcardCodecUtils.MATERIAL_CODEC.optionalFieldOf("example", GTMaterials.NULL)
                .forGetter(component -> component.example),
            Codec.BOOL.optionalFieldOf("whitelist", false)
                .forGetter(component -> component.whitelist)
        ).apply(instance, PropertyFilterComponent::new)
    );

    private Material example;

    @NotNull
    private PropertyKey<?> property;

    @Getter
    private boolean whitelist;

    private PhantomGTMaterialSlot exampleSlot;
    private SelectorWidget propertySelector;
    private WidgetGroup parent = null;

    private static final IGuiTexture GROUP_BG_WHITE = ResourceBorderTexture.BUTTON_COMMON.copy()
        .setColor(ColorPattern.YELLOW.color);

    private static final IGuiTexture GROUP_BG_BLACK = ResourceBorderTexture.BUTTON_COMMON.copy()
        .setColor(ColorPattern.ORANGE.color);

    public static PropertyFilterComponent empty() {
        return new PropertyFilterComponent(EMPTY, GTMaterials.NULL, false);
    }

    public PropertyFilterComponent(@NotNull PropertyKey<?> property, Material example, boolean whitelist) {
        this.example = example;
        this.property = property;
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
        return whitelist == material.hasProperty(property);
    }

    @Override
    public void createUILine(WidgetGroup line) {
        line.setBackground(whitelist ? GROUP_BG_WHITE : GROUP_BG_BLACK);
        parent = line;

        exampleSlot = new PhantomGTMaterialSlot(new CustomItemStackHandler(), 0, 3, 3, this::changeExample);

        propertySelector = new MySelectorWidget(25, 5, 80, 15, getMaterialPropertyNames(example));
        propertySelector.setOnChanged(this::updateProperty);

        if (example != GTMaterials.NULL) {
            exampleSlot.setMaterial(example);
        }

        propertySelector.setValue(property.toString());

        line.addWidget(exampleSlot);
        line.addWidget(propertySelector);
    }

    @RegisterLanguage("Property")
    private static final String PROPERTY_TOOLTIP_KEY = "sftcore.item.wildcard_pattern.tooltip.filter.property";

    @Override
    public Component createTooltip() {
        return whitelistTooltip().append(" ").append(Component.translatable(PROPERTY_TOOLTIP_KEY))
            .append(Component.literal(" " + property).withStyle(ChatFormatting.RED));
    }

    private boolean changeExample(Material material) {
        var ok = material != GTMaterials.NULL;

        if (ok) {
            this.example = material;

            var properties = getMaterialPropertyNames(material);
            propertySelector.setCandidates(properties);
            propertySelector.setValue(properties.get(0));
            updateProperty(properties.get(0));
        }

        return ok;
    }

    private void updateProperty(String propName) {
        property = WildcardCodecUtils.getPropertyByName(propName);
    }

    @Override
    public void onSave() {
        example = exampleSlot.getMaterial();
        updateProperty(propertySelector.getValue());
    }

    @RegisterLanguage("no property")
    private static final String NO_PROPERTY = "sftcore.item.wildcard_pattern.filter.property.no_property";

    private static List<String> getMaterialPropertyNames(Material material) {
        var properties = ((MaterialPropertiesAccessor) material.getProperties()).getProperties().keySet();

        if (properties.isEmpty()) {
            return List.of(Component.translatable(NO_PROPERTY).getString());
        }

        return properties.stream().map(PropertyKey::toString).toList();
    }

    private static class MySelectorWidget extends SelectorWidget {

        public MySelectorWidget(int x, int y, int width, int height, List<String> candidates) {
            super(x, y, width, height, candidates, ColorPattern.WHITE.color);
            button.setBackground(GuiTextures.BUTTON);
            textTexture.setColor(ColorPattern.DARK_GRAY.color);
        }
    }
}
