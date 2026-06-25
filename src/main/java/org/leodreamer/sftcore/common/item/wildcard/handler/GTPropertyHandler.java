package org.leodreamer.sftcore.common.item.wildcard.handler;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;

import net.minecraft.MethodsReturnNonnullByDefault;

import com.mojang.serialization.Codec;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey.ALLOY_BLAST;
import static com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey.ARMOR;
import static com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey.BLAST;
import static com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey.DUST;
import static com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey.EMPTY;
import static com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey.FLUID;
import static com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey.FLUID_PIPE;
import static com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey.GEM;
import static com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey.HAZARD;
import static com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey.INGOT;
import static com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey.ITEM_PIPE;
import static com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey.ORE;
import static com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey.POLYMER;
import static com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey.ROTOR;
import static com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey.TOOL;
import static com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey.WIRE;
import static com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey.WOOD;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GTPropertyHandler extends GTMaterialHandler {

    public static final PropertyKey<?>[] ALL_PROPERTY_KEYS = new PropertyKey[] {
        EMPTY,
        BLAST,
        ALLOY_BLAST,
        DUST,
        FLUID_PIPE,
        FLUID,
        GEM,
        INGOT,
        POLYMER,
        ITEM_PIPE,
        ORE,
        TOOL,
        ARMOR,
        ROTOR,
        WIRE,
        WOOD,
        HAZARD
    };

    public static final Codec<PropertyKey<?>> PROPERTY_CODEC = Codec.STRING.xmap(
        GTPropertyHandler::propertyFromName,
        PropertyKey::toString
    );

    @NotNull
    @Getter
    @Setter
    private PropertyKey<?> property = EMPTY;

    public GTPropertyHandler(PropertyKey<?> property, Material example) {
        super(example);
        setProperty(property);
    }

    public void setPropertyName(String propertyName) {
        this.property = propertyFromName(propertyName);
    }

    public static PropertyKey<?> propertyFromName(String propertyName) {
        if (propertyName.isBlank()) {
            return EMPTY;
        }
        for (var key : ALL_PROPERTY_KEYS) {
            if (key.toString().equalsIgnoreCase(propertyName)) {
                return key;
            }
        }
        return EMPTY;
    }
}
