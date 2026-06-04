package org.leodreamer.sftcore.common.item.terminal.builder;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.terminal.MekBuilderRegistry;
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
        var config = InductionMatrixConfig.getOrCreate(rootTag);

        int width = InductionMatrixConfig.getWidth(config);
        int height = InductionMatrixConfig.getHeight(config);
        int depth = InductionMatrixConfig.getDepth(config);

        var plan = new BuildPlan();
        var innerPositions = new ArrayList<BlockPos>();

        var casing = MekanismBlocks.INDUCTION_CASING.getBlock();
        var glass = MekanismBlocks.STRUCTURAL_GLASS.getBlock();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {
                    var pos = ctx.origin().offset(x, y, z);
                    var part = classify(x, y, z, width, height, depth);

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

        fillInnerCellFirst(ctx, plan, innerPositions);
        return plan;
    }

    private void fillInnerCellFirst(
        BuildContext ctx,
        BuildPlan plan,
        List<BlockPos> innerPositions
    ) {
        var snapshot = InventorySnapshot.of(ctx.player());

        var cells = cells();
        var providers = providers();

        for (var pos : innerPositions) {
            var selected = firstAvailable(snapshot, cells, providers);

            if (selected == null) {
                // leave air in the induction matrix
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
        for (var candidate : preferred) {
            if (snapshot.count(candidate.item()) > 0) {
                return candidate;
            }
        }
        for (var candidate : fallback) {
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
}
