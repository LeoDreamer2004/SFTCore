package org.leodreamer.sftcore.common.item.wildcard;

import org.leodreamer.sftcore.common.item.wildcard.feature.IWildcardFilterComponent;
import org.leodreamer.sftcore.common.item.wildcard.feature.IWildcardIOComponent;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.registry.GTRegistries;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.stacks.GenericStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
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

    public @NotNull List<IWildcardIOComponent> getIOComponents(IO io) {
        return WildcardComponentCodecs.readIO(stack.getOrCreateTag(), io);
    }

    public ItemStack setIOComponents(IO io, @NotNull List<? extends IWildcardIOComponent> components) {
        WildcardComponentCodecs.writeIO(stack.getOrCreateTag(), io, components);
        return stack;
    }

    public @NotNull List<IWildcardFilterComponent> getFilterComponents() {
        return WildcardComponentCodecs.readFilters(stack.getOrCreateTag());
    }

    public ItemStack setFilterComponents(@NotNull List<? extends IWildcardFilterComponent> components) {
        WildcardComponentCodecs.writeFilters(stack.getOrCreateTag(), components);
        return stack;
    }

    public @Nullable GenericStack[] getIOStacks(IO io, Material material) {
        var components = getIOComponents(io);

        if (components.isEmpty()) {
            return null;
        }

        var stacks = new ArrayList<GenericStack>();

        for (var component : components) {
            var stack = component.apply(material);

            if (stack != null && stack.what() != null && stack.amount() > 0) {
                stacks.add(stack);
            }
        }

        return stacks.toArray(new GenericStack[0]);
    }

    public boolean test(Material material) {
        if (material == null) {
            return false;
        }

        for (var component : getFilterComponents()) {
            if (!component.test(material)) {
                return false;
            }
        }

        return true;
    }

    public Stream<IPatternDetails> generateAllPatterns(Level level) {
        return generateAllPatterns(level, false);
    }

    public Stream<IPatternDetails> generateAllPatterns(Level level, boolean stable) {
        var stream = GTRegistries.MATERIALS.values().stream();
        if (stable) {
            stream = stream.sorted(Comparator.comparing(GTRegistries.MATERIALS::getKey));
        }
        return stream.filter(this::test)
            .map(material -> {
                var input = getIOStacks(IO.IN, material);
                var output = getIOStacks(IO.OUT, material);

                if (input == null || output == null || input.length == 0 || output.length == 0) {
                    return null;
                }

                var encodedPattern = PatternDetailsHelper.encodeProcessingPattern(input, output);
                return PatternDetailsHelper.decodePattern(encodedPattern, level);
            })
            .filter(Objects::nonNull);
    }
}
