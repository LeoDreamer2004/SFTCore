package org.leodreamer.sftcore.common.item.behavior.wildcard.impl;

import org.leodreamer.sftcore.common.item.behavior.wildcard.WildcardIOSerializers;
import org.leodreamer.sftcore.common.item.behavior.wildcard.feature.IWildcardIOComponent;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;

import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.stacks.GenericStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class WildcardPatternLogic {

    private final ItemStack stack;

    public enum IO {

        IN("in"),
        OUT("out");

        public final String key;

        IO(String key) {
            this.key = key;
        }
    }

    private WildcardPatternLogic(ItemStack stack) {
        this.stack = stack;
    }

    public static WildcardPatternLogic on(ItemStack stack) {
        return new WildcardPatternLogic(stack);
    }

    public @Nullable List<IWildcardIOComponent> getComponents(IO io) {
        if (stack.getOrCreateTag().get(io.key) instanceof ListTag outputTag) {
            return WildcardIOSerializers.fromNBT(outputTag);
        }
        return null;
    }

    public ItemStack setComponents(IO io, @NotNull List<IWildcardIOComponent> components) {
        var tag = stack.getOrCreateTag();
        var listTag = WildcardIOSerializers.toNBT(components);
        tag.put(io.key, listTag);
        stack.setTag(tag);
        return stack;
    }

    public @Nullable GenericStack[] getIOStacks(IO io, Material material) {
        var components = getComponents(io);
        if (components == null) return null;

        var stacks = new ArrayList<GenericStack>();
        for (var component : components) {
            var stack = component.apply(material);
            if (stack == null) return null;
            stacks.add(stack);
        }
        return stacks.toArray(new GenericStack[0]);
    }

    public Stream<IPatternDetails> generateAllPatterns(Level level) {
        return GTCEuAPI.materialManager.getRegisteredMaterials().stream()
            .map(material -> {
                var input = getIOStacks(IO.IN, material);
                var output = getIOStacks(IO.OUT, material);
                if (input == null || output == null) return null;
                // FIXME: a little silly here
                var item = PatternDetailsHelper.encodeProcessingPattern(input, output);
                return PatternDetailsHelper.decodePattern(item, level);
            })
            .filter(Objects::nonNull);
    }
}
