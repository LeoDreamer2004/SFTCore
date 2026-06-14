package org.leodreamer.sftcore.common.item.cepattern;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import org.leodreamer.sftcore.SFTCore;

import java.util.List;

public record CEPatternData(List<ResourceLocation> recipeIds, List<Integer> multipliers) {

    public static final CEPatternData EMPTY = new CEPatternData(List.of(), List.of());

    public static final Codec<CEPatternData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.listOf().optionalFieldOf("recipes", List.of()).forGetter(CEPatternData::recipeIds),
        Codec.INT.listOf().optionalFieldOf("multipliers", List.of()).forGetter(CEPatternData::multipliers)
    ).apply(instance, CEPatternData::new));

    public CEPatternData(List<ResourceLocation> recipeIds, List<Integer> multipliers) {
        int size = Math.min(Math.min(recipeIds.size(), multipliers.size()), CEPatternLogic.MAX_STEPS);
        this.recipeIds = recipeIds.subList(0, size);
        this.multipliers = multipliers.subList(0, size);
    }

    @Override
    public int hashCode() {
        return 31 * recipeIds.hashCode() + multipliers.hashCode();
    }

    public static CEPatternData read(CompoundTag tag) {
        return CEPatternData.CODEC.parse(NbtOps.INSTANCE, tag)
            .resultOrPartial(SFTCore.LOGGER::error)
            .orElse(CEPatternData.EMPTY);
    }

    public CompoundTag write() {
        return CEPatternData.CODEC.encodeStart(NbtOps.INSTANCE, this)
            .resultOrPartial(SFTCore.LOGGER::error)
            .filter(CompoundTag.class::isInstance)
            .map(CompoundTag.class::cast)
            .orElse(new CompoundTag());
    }
}
