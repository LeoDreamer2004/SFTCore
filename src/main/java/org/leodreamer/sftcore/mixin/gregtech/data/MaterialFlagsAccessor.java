package org.leodreamer.sftcore.mixin.gregtech.data;

import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlag;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(MaterialFlags.class)
public interface MaterialFlagsAccessor {

    @Accessor(value = "flags", remap = false)
    Set<MaterialFlag> getFlags();
}
