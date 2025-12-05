package org.leodreamer.sftcore.common.data;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import org.leodreamer.sftcore.SFTCore;

public class SFTDimensions {
    public static final ResourceKey<Level> VOID_DIMENSION = ResourceKey.create(Registries.DIMENSION, SFTCore.id("void"));

    public static final ResourceKey<DimensionType> VOID_DIMENSION_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, SFTCore.id("void"));
}
