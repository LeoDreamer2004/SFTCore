package org.leodreamer.sftcore.common.item.wildcard.handler;

import org.leodreamer.sftcore.mixin.gregtech.data.GTBucketItemAccessor;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKey;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.common.item.GTBucketItem;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.ItemStack;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GTMaterialHandler extends CustomItemStackHandler {

    public static final Codec<Optional<Material>> MATERIAL_CODEC = Codec.STRING.xmap(
        id -> Optional.ofNullable(materialFromId(id)),
        material -> material.map(value -> value.getResourceLocation().toString()).orElse("")
    );

    public GTMaterialHandler() {
        super(1);
    }

    public GTMaterialHandler(@Nullable Material material) {
        this();
        setMaterial(material);
    }

    public void setMaterial(@Nullable Material material) {
        setStackInSlot(0, findExampleForMaterial(material));
    }

    public @Nullable Material getMaterial() {
        return materialFromSample(getStackInSlot(0));
    }

    public static @Nullable Material materialFromId(String id) {
        if (id.isBlank()) {
            return null;
        }
        return GTRegistries.MATERIALS.get(id);
    }

    public static @Nullable Material materialFromSample(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        if (stack.getItem() instanceof GTBucketItem bucket) {
            return ((GTBucketItemAccessor) bucket).getMaterial();
        }
        try {
            return ChemicalHelper.getMaterialEntry(stack.getItem()).material();
        } catch (Exception ignored) {
            return null;
        }
    }

    public static ItemStack findExampleForMaterial(@Nullable Material material) {
        if (material == null) {
            return ItemStack.EMPTY;
        }

        var dust = ChemicalHelper.get(TagPrefix.dust, material);
        if (!dust.isEmpty()) {
            return dust;
        }
        try {
            return material.getFluid().getBucket().getDefaultInstance();
        } catch (Exception ignored) {}

        for (var tag : GTRegistries.TAG_PREFIXES.values()) {
            var stack = ChemicalHelper.get(tag, material);
            if (!stack.isEmpty()) {
                return stack;
            }
        }
        for (var key : FluidStorageKey.allKeys()) {
            try {
                var fluid = material.getProperty(PropertyKey.FLUID).getStorage().get(key);
                if (fluid != null) {
                    return fluid.getBucket().getDefaultInstance();
                }
            } catch (Exception ignored) {}
        }
        return ItemStack.EMPTY;
    }
}
