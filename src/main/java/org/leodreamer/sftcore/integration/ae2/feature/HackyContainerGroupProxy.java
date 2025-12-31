package org.leodreamer.sftcore.integration.ae2.feature;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import appeng.api.implementations.blockentities.PatternContainerGroup;
import appeng.api.stacks.AEItemKey;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Hacky way to record more info in the container group
 */
@DataGenScanned
public class HackyContainerGroupProxy {

    private final @Nullable AEItemKey icon;
    private Component name;
    private final List<Component> tooltip;

    private HackyContainerGroupProxy(PatternContainerGroup group) {
        this.icon = group.icon();
        this.name = group.name();
        this.tooltip = new ArrayList<>(group.tooltip()); // In case of immutable list
    }

    public static HackyContainerGroupProxy of(PatternContainerGroup group) {
        return new HackyContainerGroupProxy(group);
    }

    public PatternContainerGroup get() {
        return new PatternContainerGroup(icon, name, tooltip);
    }

    @RegisterLanguage("Part From: ")
    private static final String PART_FROM = "sftcore.mixin.ae2.pattern.gt_part_from";

    @RegisterLanguage("Machine Position: %d %d %d")
    private static final String MACHINE_POS = "sftcore.mixin.ae2.pattern.machine_pos";

    public HackyContainerGroupProxy setBlockPos(BlockPos pos) {
        tooltip.addFirst(Component.translatable(MACHINE_POS, pos.getX(), pos.getY(), pos.getZ()));
        return this;
    }

    private static final Pattern posPattern = Pattern.compile("(-?\\d+) (-?\\d+) (-?\\d+)$");

    public @Nullable BlockPos getBlockBos() {
        try {
            // tooltip: "block position: x y z"
            // use regex to extract x, y, z, at the end of the string
            var line = tooltip.getFirst().getString();
            var match = posPattern.matcher(line);
            if (!match.find()) {
                return null;
            }
            int x = Integer.parseInt(match.group(1));
            int y = Integer.parseInt(match.group(2));
            int z = Integer.parseInt(match.group(3));
            return new BlockPos(x, y, z);
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    public HackyContainerGroupProxy setCircuit(int circuit) {
        if (circuit != 0) {
            name = name.copy().append(" - " + circuit);
        }
        return this;
    }

    public int getCircuit() {
        try {
            var str = name.getString();
            var parts = str.split(" - ");
            return Integer.parseInt(parts[parts.length - 1]);
        } catch (RuntimeException ignored) {
            return 0;
        }
    };

    public HackyContainerGroupProxy recordPartFrom(Component displayName) {
        tooltip.add(Component.translatable(PART_FROM).append(displayName));
        return this;
    }
}
