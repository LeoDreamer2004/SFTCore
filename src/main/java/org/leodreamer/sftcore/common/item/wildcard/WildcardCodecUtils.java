package org.leodreamer.sftcore.common.item.wildcard;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlag;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.common.data.GTMaterials;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import static com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey.*;

public final class WildcardCodecUtils {

    private WildcardCodecUtils() {}

    public static final Codec<Material> MATERIAL_CODEC = Codec.STRING.xmap(id -> {
        Material material = GTRegistries.MATERIALS.get(id);
        return material == null ? GTMaterials.NULL : material;
    }, material -> material.getResourceLocation().toString());

    public static final Codec<MaterialFlag> MATERIAL_FLAG_CODEC = Codec.STRING.comapFlatMap(name -> {
        MaterialFlag flag = MaterialFlag.getByName(name);
        if (flag == null) {
            return DataResult.error(() -> "Unknown material flag: " + name);
        }
        return DataResult.success(flag);
    }, MaterialFlag::toString);

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
        WildcardCodecUtils::getPropertyByName,
        PropertyKey::toString
    );

    public static PropertyKey<?> getPropertyByName(String propName) {
        if (propName == null || propName.isEmpty()) {
            return EMPTY;
        }

        for (var key : ALL_PROPERTY_KEYS) {
            if (key.toString().equalsIgnoreCase(propName)) {
                return key;
            }
        }

        return EMPTY;
    }
}
