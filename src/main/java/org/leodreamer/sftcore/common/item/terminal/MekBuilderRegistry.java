package org.leodreamer.sftcore.common.item.terminal;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.common.item.terminal.api.MekMultiblockBuilder;
import org.leodreamer.sftcore.common.item.terminal.api.MekTerminalTab;
import org.leodreamer.sftcore.common.item.terminal.builder.InductionMatrixBuilder;
import org.leodreamer.sftcore.common.item.terminal.gui.InductionMatrixTab;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class MekBuilderRegistry {

    public static final ResourceLocation INDUCTION = SFTCore.id("induction_matrix");

    private static final Map<ResourceLocation, Entry> ENTRIES = new LinkedHashMap<>();

    static {
        init();
    }

    private static void init() {
        var induction = new InductionMatrixBuilder();
        register(induction, (stack, onSave) -> new InductionMatrixTab(induction, stack, onSave));
    }

    private MekBuilderRegistry() {}

    public static void register(MekMultiblockBuilder builder, TabFactory tabFactory) {
        ENTRIES.put(builder.id(), new Entry(builder, tabFactory));
    }

    public static Collection<Entry> entries() {
        return ENTRIES.values();
    }

    public static MekMultiblockBuilder selected(ItemStack stack) {
        var entry = selectedEntry(stack);
        return entry == null ? null : entry.builder();
    }

    public static MekTerminalTab selectedTab(ItemStack stack, Consumer<ItemStack> onSave) {
        var entry = selectedEntry(stack);
        return entry == null ? null : entry.tabFactory().create(stack, onSave);
    }

    public static void setSelected(ItemStack stack, ResourceLocation id) {
        var tag = stack.getOrCreateTag();
        var root = tag.getCompound(MekTerminalTags.ROOT);
        root.putString(MekTerminalTags.SELECTED_TAB, id.toString());
        tag.put(MekTerminalTags.ROOT, root);
    }

    private static Entry selectedEntry(ItemStack stack) {
        var root = stack.getOrCreateTag().getCompound(MekTerminalTags.ROOT);
        var selected = root.getString(MekTerminalTags.SELECTED_TAB);

        if (!selected.isEmpty()) {
            var id = ResourceLocation.tryParse(selected);
            if (id != null) {
                var entry = ENTRIES.get(id);
                if (entry != null) {
                    return entry;
                }
            }
        }

        return ENTRIES.get(INDUCTION);
    }

    public record Entry(MekMultiblockBuilder builder, TabFactory tabFactory) {
    }

    @FunctionalInterface
    public interface TabFactory {
        MekTerminalTab create(ItemStack stack, Consumer<ItemStack> onSave);
    }
}
