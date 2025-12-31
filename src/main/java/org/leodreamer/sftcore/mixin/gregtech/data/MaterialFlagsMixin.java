package org.leodreamer.sftcore.mixin.gregtech.data;

import org.leodreamer.sftcore.api.feature.IMaterialFlags;

import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlag;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Set;

@Mixin(MaterialFlags.class)
public class MaterialFlagsMixin implements IMaterialFlags {

    @Shadow(remap = false)
    @Final
    private Set<MaterialFlag> flags;

    @Unique
    @Override
    public Set<MaterialFlag> sftcore$getFlags() {
        return flags;
    }
}
