package org.leodreamer.sftcore.mixin.gregtech.data;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.item.GTBucketItem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GTBucketItem.class)
public interface GTBucketItemAccessor {

    @Accessor(value = "material", remap = false)
    Material getMaterial();
}
