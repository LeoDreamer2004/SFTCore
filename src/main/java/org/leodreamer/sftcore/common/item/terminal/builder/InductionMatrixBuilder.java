package org.leodreamer.sftcore.common.item.terminal.builder;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.terminal.MekBuilderRegistry;
import org.leodreamer.sftcore.common.item.terminal.MekTerminalTags;
import org.leodreamer.sftcore.common.item.terminal.api.*;

import mekanism.common.registries.MekanismBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

@DataGenScanned
public class InductionMatrixBuilder implements MekMultiblockBuilder {

    @RegisterLanguage("Induction Matrix")
    public static final String TITLE = "item.sftcore.mek_terminal.tab.induction";

    @RegisterLanguage("Right-click the induction casing with Shift to start building")
    public static final String INVALID_START = "item.sftcore.mek_terminal.invalid_induction_start";

    private enum Part {
        FRAME,
        FACE,
        INNER
    }

    private record InnerCandidate(Item item, Block block, PlacementRole role) {}

    @Override
    public ResourceLocation id() {
        return MekBuilderRegistry.INDUCTION;
    }

    @Override
    public Component title() {
        return Component.translatable(TITLE);
    }

    @Override
    public boolean canStart(BuildContext ctx) {
        return ctx.level()
            .getBlockState(ctx.origin())
            .is(MekanismBlocks.INDUCTION_CASING.getBlock());
    }

    @Override
    public Component invalidStartMessage() {
        return Component.translatable(INVALID_START);
    }

    @Override
    public BuildPlan createPlan(BuildContext ctx, CompoundTag rootTag) {
        CompoundTag config = getInductionConfig(rootTag);

        int width = clamp(readInt(config, MekTerminalTags.INDUCTION_WIDTH, 5), 3, 18);
        int height = clamp(readInt(config, MekTerminalTags.INDUCTION_HEIGHT, 5), 3, 18);
        int depth = clamp(readInt(config, MekTerminalTags.INDUCTION_DEPTH, 5), 3, 18);
        String strategy = readString(
            config,
            MekTerminalTags.INDUCTION_FILL_STRATEGY,
            MekTerminalTags.STRATEGY_BALANCED
        );

        BuildPlan plan = new BuildPlan();
        List<BlockPos> innerPositions = new ArrayList<>();

        Block casing = MekanismBlocks.INDUCTION_CASING.getBlock();
        Block glass = MekanismBlocks.STRUCTURAL_GLASS.getBlock();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {
                    BlockPos pos = ctx.origin().offset(x, y, z);
                    Part part = classify(x, y, z, width, height, depth);

                    if (part == Part.FRAME) {
                        plan.add(new Placement(
                            pos,
                            casing.defaultBlockState(),
                            casing.asItem(),
                            PlacementRole.FRAME,
                            !pos.equals(ctx.origin())
                        ));
                    } else if (part == Part.FACE) {
                        plan.add(new Placement(
                            pos,
                            glass.defaultBlockState(),
                            glass.asItem(),
                            PlacementRole.FACE,
                            true
                        ));
                    } else {
                        innerPositions.add(pos);
                    }
                }
            }
        }

        fillInner(ctx, plan, innerPositions, strategy);
        return plan;
    }

    private CompoundTag getInductionConfig(CompoundTag rootTag) {
        CompoundTag root = rootTag.getCompound(MekTerminalTags.ROOT);
        CompoundTag config = root.getCompound(MekTerminalTags.INDUCTION);

        if (!config.contains(MekTerminalTags.INDUCTION_WIDTH)) {
            config.putInt(MekTerminalTags.INDUCTION_WIDTH, 5);
        }
        if (!config.contains(MekTerminalTags.INDUCTION_HEIGHT)) {
            config.putInt(MekTerminalTags.INDUCTION_HEIGHT, 5);
        }
        if (!config.contains(MekTerminalTags.INDUCTION_DEPTH)) {
            config.putInt(MekTerminalTags.INDUCTION_DEPTH, 5);
        }
        if (!config.contains(MekTerminalTags.INDUCTION_FILL_STRATEGY)) {
            config.putString(
                MekTerminalTags.INDUCTION_FILL_STRATEGY,
                MekTerminalTags.STRATEGY_BALANCED
            );
        }

        root.put(MekTerminalTags.INDUCTION, config);
        rootTag.put(MekTerminalTags.ROOT, root);
        return config;
    }

    private void fillInner(
        BuildContext ctx,
        BuildPlan plan,
        List<BlockPos> innerPositions,
        String strategy
    ) {
        InventorySnapshot snapshot = InventorySnapshot.of(ctx.player());

        List<InnerCandidate> cells = cells();
        List<InnerCandidate> providers = providers();

        boolean nextCell = true;

        for (BlockPos pos : innerPositions) {
            InnerCandidate selected = switch (strategy) {
                case MekTerminalTags.STRATEGY_CELL_FIRST -> firstAvailable(snapshot, cells, providers);
                case MekTerminalTags.STRATEGY_PROVIDER_FIRST -> firstAvailable(snapshot, providers, cells);
                default -> {
                    InnerCandidate candidate = nextCell
                        ? firstAvailable(snapshot, cells, providers)
                        : firstAvailable(snapshot, providers, cells);
                    nextCell = !nextCell;
                    yield candidate;
                }
            };

            if (selected == null) {
                // 没东西就留空。输导矩阵内部允许空气。
                continue;
            }

            snapshot.takeVirtual(selected.item());

            plan.add(new Placement(
                pos,
                selected.block().defaultBlockState(),
                selected.item(),
                selected.role(),
                true
            ));
        }
    }

    private InnerCandidate firstAvailable(
        InventorySnapshot snapshot,
        List<InnerCandidate> preferred,
        List<InnerCandidate> fallback
    ) {
        for (InnerCandidate candidate : preferred) {
            if (snapshot.count(candidate.item()) > 0) {
                return candidate;
            }
        }
        for (InnerCandidate candidate : fallback) {
            if (snapshot.count(candidate.item()) > 0) {
                return candidate;
            }
        }
        return null;
    }

    private List<InnerCandidate> cells() {
        return List.of(
            candidate(MekanismBlocks.ULTIMATE_INDUCTION_CELL.getBlock(), PlacementRole.INTERNAL_CELL),
            candidate(MekanismBlocks.ELITE_INDUCTION_CELL.getBlock(), PlacementRole.INTERNAL_CELL),
            candidate(MekanismBlocks.ADVANCED_INDUCTION_CELL.getBlock(), PlacementRole.INTERNAL_CELL),
            candidate(MekanismBlocks.BASIC_INDUCTION_CELL.getBlock(), PlacementRole.INTERNAL_CELL)
        );
    }

    private List<InnerCandidate> providers() {
        return List.of(
            candidate(MekanismBlocks.ULTIMATE_INDUCTION_PROVIDER.getBlock(), PlacementRole.INTERNAL_PROVIDER),
            candidate(MekanismBlocks.ELITE_INDUCTION_PROVIDER.getBlock(), PlacementRole.INTERNAL_PROVIDER),
            candidate(MekanismBlocks.ADVANCED_INDUCTION_PROVIDER.getBlock(), PlacementRole.INTERNAL_PROVIDER),
            candidate(MekanismBlocks.BASIC_INDUCTION_PROVIDER.getBlock(), PlacementRole.INTERNAL_PROVIDER)
        );
    }

    private InnerCandidate candidate(Block block, PlacementRole role) {
        return new InnerCandidate(block.asItem(), block, role);
    }

    private Part classify(int x, int y, int z, int w, int h, int d) {
        int borders = 0;

        if (x == 0 || x == w - 1) borders++;
        if (y == 0 || y == h - 1) borders++;
        if (z == 0 || z == d - 1) borders++;

        if (borders >= 2) {
            return Part.FRAME;
        }
        if (borders == 1) {
            return Part.FACE;
        }
        return Part.INNER;
    }

    private int readInt(CompoundTag tag, String key, int def) {
        return tag.contains(key) ? tag.getInt(key) : def;
    }

    private String readString(CompoundTag tag, String key, String def) {
        return tag.contains(key) ? tag.getString(key) : def;
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
