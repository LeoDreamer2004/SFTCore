package org.leodreamer.sftcore.common.item.wildcard.impl;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.wildcard.feature.WildcardFilterComponent;
import org.leodreamer.sftcore.common.item.wildcard.handler.GTPropertyHandler;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;

import brachy.modularui.drawable.Rectangle;
import brachy.modularui.value.sync.PanelSyncManager;
import brachy.modularui.widgets.layout.Flow;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey.EMPTY;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@DataGenScanned
public class PropertyFilterComponent extends WildcardFilterComponent {

    public static final Codec<PropertyFilterComponent> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            GTPropertyHandler.PROPERTY_CODEC.optionalFieldOf("property", EMPTY)
                .forGetter(component -> component.property),
            GTPropertyHandler.MATERIAL_CODEC.optionalFieldOf("example", Optional.empty())
                .forGetter(component -> Optional.ofNullable(component.example)),
            Codec.BOOL.optionalFieldOf("whitelist", false)
                .forGetter(component -> component.whitelist)
        ).apply(
            instance,
            (property, example, whitelist) -> new PropertyFilterComponent(property, example.orElse(null), whitelist)
        )
    );

    @Getter
    @Nullable
    private Material example;
    @Getter
    @NotNull
    private PropertyKey<?> property;
    private GTPropertyHandler exampleSlot;
    private String detail;

    private static final int GROUP_BG_WHITE = 0xFFFFFF33;
    private static final int GROUP_BG_BLACK = 0xFFFF8800;

    @RegisterLanguage("Property")
    public static final String LABEL = "item.sftcore.wildcard_pattern.ui.filter.property";

    public static PropertyFilterComponent empty() {
        return new PropertyFilterComponent(EMPTY, null, false);
    }

    public PropertyFilterComponent(PropertyKey<?> property, @Nullable Material example, boolean whitelist) {
        super(whitelist);
        this.example = example;
        this.property = property;
        this.detail = property == EMPTY ? "" : property.toString();
    }

    @Override
    public boolean test(Material material) {
        if (property == EMPTY) {
            return true;
        }
        return whitelist == material.hasProperty(property);
    }

    @Override
    protected void addLineContent(
        Flow row,
        PanelSyncManager syncManager,
        String lineSyncKey
    ) {
        var sampleSyncHandler = registerSampleSlot(
            new GTPropertyHandler(property, example), GTPropertyHandler.class, syncManager, lineSyncKey
        );
        this.exampleSlot = sampleSlotHandler(sampleSyncHandler, GTPropertyHandler.class);
        this.exampleSlot.setMaterial(example);
        this.exampleSlot.setProperty(property);
        row.child(createTypeButton(52, LABEL));
        row.child(createWhitelistButton(row));
        row.child(createSampleSlot(sampleSyncHandler));
        row.child(detailField(() -> detail, text -> detail = text));
    }

    @Override
    public void onSave() {
        if (exampleSlot == null) {
            return;
        }
        exampleSlot.setPropertyName(detail);
        example = exampleSlot.getMaterial();
        property = exampleSlot.getProperty();
    }

    @Override
    public Component createTooltip() {
        return whitelistTooltip().append(" ").append(Component.translatable(LABEL))
            .append(Component.literal(" " + property).withStyle(ChatFormatting.RED));
    }

    @RegisterLanguage("no property")
    private static final String NO_PROPERTY = "item.sftcore.wildcard_pattern.filter.property.no_property";

    @Override
    protected Rectangle rowBackground() {
        return new Rectangle().color(whitelist ? GROUP_BG_WHITE : GROUP_BG_BLACK);
    }
}
