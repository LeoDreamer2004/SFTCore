package org.leodreamer.sftcore.common.item.wildcard.handler;

import org.leodreamer.sftcore.mixin.gregtech.data.GTBucketItemAccessor;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKey;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.item.GTBucketItem;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.ItemStack;

import com.mojang.serialization.Codec;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GTMaterialHandler extends CustomItemStackHandler {

    public static final Codec<Material> MATERIAL_CODEC = Codec.STRING.xmap(
        GTMaterialHandler::materialFromId,
        material -> material.getResourceLocation().toString()
    );

    public GTMaterialHandler() {
        super(1);
    }

    public GTMaterialHandler(Material material) {
        this();
        setMaterial(material);
    }

    public void setMaterial(Material material) {
        setStackInSlot(0, findExampleForMaterial(material));
    }

    public Material getMaterial() {
        return materialFromSample(getStackInSlot(0));
    }

    public static Material materialFromId(String id) {
        if (id.isBlank()) {
            return GTMaterials.NULL;
        }
        var material = GTRegistries.MATERIALS.get(id);
        return material == null ? GTMaterials.NULL : material;
    }

    public static Material materialFromSample(ItemStack stack) {
        if (stack.isEmpty()) {
            return GTMaterials.NULL;
        }
        if (stack.getItem() instanceof GTBucketItem bucket) {
            return ((GTBucketItemAccessor) bucket).getMaterial();
        }
        try {
            return ChemicalHelper.getMaterialEntry(stack.getItem()).material();
        } catch (Exception ignored) {
            return GTMaterials.NULL;
        }
    }

    public static ItemStack findExampleForMaterial(Material material) {
        if (material == GTMaterials.NULL) {
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
