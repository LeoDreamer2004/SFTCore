package org.leodreamer.sftcore.common.item.cepattern;

import org.leodreamer.sftcore.SFTCore;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.ArrayList;
import java.util.List;

public final class CEPatternData {

    public static final int MAX_STEPS = 9;

    public static final Codec<CEPatternData> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            ResourceLocation.CODEC.listOf()
                .optionalFieldOf("recipes", new ArrayList<>())
                .forGetter(CEPatternData::recipeIds),
            Codec.INT.listOf()
                .optionalFieldOf("multipliers", new ArrayList<>())
                .forGetter(CEPatternData::multipliers)
        ).apply(instance, CEPatternData::new)
    );

    // always make sure that these two lists keep the same length
    private final ArrayList<ResourceLocation> recipeIds;
    private final ArrayList<Integer> multipliers;

    public static CEPatternData empty() {
        return new CEPatternData(new ArrayList<>(), new ArrayList<>());
    }

    public CEPatternData(List<ResourceLocation> recipeIds, List<Integer> multipliers) {
        int size = Math.min(Math.min(recipeIds.size(), multipliers.size()), MAX_STEPS);
        this.recipeIds = new ArrayList<>(recipeIds.subList(0, size));
        this.multipliers = new ArrayList<>(multipliers.subList(0, size));
    }

    // NOTICE: READONLY. Do not change the recipeIds!
    public List<ResourceLocation> recipeIds() {
        return recipeIds;
    }

    // NOTICE: READONLY. Do not change the multipliers!
    public List<Integer> multipliers() {
        return multipliers;
    }

    public int size() {
        return recipeIds.size();
    }

    public boolean isEmpty() {
        return recipeIds.isEmpty();
    }

    @Override
    public int hashCode() {
        return 31 * recipeIds.hashCode() + multipliers.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CEPatternData other)) {
            return false;
        }
        return recipeIds.equals(other.recipeIds) && multipliers.equals(other.multipliers);
    }

    public static CEPatternData read(CompoundTag tag) {
        return CEPatternData.CODEC.parse(NbtOps.INSTANCE, tag)
            .resultOrPartial(SFTCore.LOGGER::error)
            .orElseGet(CEPatternData::empty);
    }

    public CompoundTag write() {
        return CEPatternData.CODEC.encodeStart(NbtOps.INSTANCE, this)
            .resultOrPartial(SFTCore.LOGGER::error)
            .filter(CompoundTag.class::isInstance)
            .map(CompoundTag.class::cast)
            .orElse(new CompoundTag());
    }

    public boolean addRecipe(ResourceLocation id) {
        if (recipeIds.size() >= MAX_STEPS) {
            return false;
        }
        recipeIds.add(id);
        multipliers.add(1);
        return true;
    }

    public boolean removeRecipe(int index) {
        if (index < 0 || index >= recipeIds.size()) {
            return false;
        }
        recipeIds.remove(index);
        multipliers.remove(index);
        return true;
    }

    /**
     * Compile the data into a {@link CECompiledRecipe}
     */
    public CECompiledRecipe compile(Level level) {
        return new CECompiledRecipe(level, this);
    }
}
