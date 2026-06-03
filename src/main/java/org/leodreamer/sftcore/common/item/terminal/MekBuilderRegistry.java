package org.leodreamer.sftcore.common.item.terminal;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.common.item.terminal.api.MekMultiblockBuilder;
import org.leodreamer.sftcore.common.item.terminal.builder.InductionMatrixBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;

public final class MekBuilderRegistry {

    private static final Map<ResourceLocation, MekMultiblockBuilder> BUILDERS = new LinkedHashMap<>();

    public static final ResourceLocation INDUCTION =
        SFTCore.id("induction_matrix");

    private MekBuilderRegistry() {}

    public static void init() {
        register(new InductionMatrixBuilder());
    }

    public static void register(MekMultiblockBuilder builder) {
        BUILDERS.put(builder.id(), builder);
    }

    public static MekMultiblockBuilder selected(ItemStack stack) {
        var root = stack.getOrCreateTag().getCompound(MekTerminalTags.ROOT);
        String selected = root.getString(MekTerminalTags.SELECTED_TAB);

        if (!selected.isEmpty()) {
            var builder = BUILDERS.get(ResourceLocation.parse(selected));
            if (builder != null) {
                return builder;
            }
        }

        return BUILDERS.get(INDUCTION);
    }

    public static void setSelected(ItemStack stack, ResourceLocation id) {
        var tag = stack.getOrCreateTag();
        var root = tag.getCompound(MekTerminalTags.ROOT);
        root.putString(MekTerminalTags.SELECTED_TAB, id.toString());
        tag.put(MekTerminalTags.ROOT, root);
    }
}
