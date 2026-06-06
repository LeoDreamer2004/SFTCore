package org.leodreamer.sftcore.common.item.terminal.builder.impl;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.common.item.terminal.api.*;
import org.leodreamer.sftcore.common.item.terminal.builder.ICubeShapedBuilder;
import org.leodreamer.sftcore.common.item.terminal.builder.ISimpleRelativePosition;
import org.leodreamer.sftcore.common.item.terminal.config.impl.FissionReactorConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import mekanism.generators.common.registries.GeneratorsBlocks;

import java.util.ArrayList;
import java.util.Map;

@DataGenScanned
public class FissionReactorBuilder implements ISimpleRelativePosition, ICubeShapedBuilder {

    @Override
    public ResourceLocation id() {
        return SFTCore.id("fission_reactor");
    }

    @Override
    public Block clickAt() {
        return GeneratorsBlocks.FISSION_REACTOR_CASING.getBlock();
    }

    @Override
    public BuildDimensions dimensions(CompoundTag terminalTag) {
        var config = FissionReactorConfig.resolve(terminalTag);
        return new BuildDimensions(
            config.width.get(),
            config.height.get(),
            config.depth.get()
        );
    }

    @Override
    public Iterable<RelativeBuildPos> positions(CompoundTag terminalTag, BuildDimensions dimensions) {
        // tricky override to skip inner
        var positions = new ArrayList<RelativeBuildPos>(dimensions.surface());

        // faces only
        for (int x = 0; x < dimensions.width(); x++) {
            for (int z = 0; z < dimensions.depth(); z++) {
                positions.add(new RelativeBuildPos(x, 0, z));
                positions.add(new RelativeBuildPos(x, dimensions.height() - 1, z));
            }
        }

        for (int y = 1; y < dimensions.height() - 1; y++) {
            for (int x = 0; x < dimensions.width(); x++) {
                positions.add(new RelativeBuildPos(x, y, 0));
                positions.add(new RelativeBuildPos(x, y, dimensions.depth() - 1));
            }

            for (int z = 1; z < dimensions.depth() - 1; z++) {
                positions.add(new RelativeBuildPos(0, y, z));
                positions.add(new RelativeBuildPos(dimensions.width() - 1, y, z));
            }
        }

        return positions;
    }

    @Override
    public PlacementCandidate candidateForPart(
        CompoundTag terminalTag,
        Part part,
        RelativeBuildPos pos,
        InventorySnapshot inventory
    ) {
        return switch (part) {
            case CORNER, EDGE -> PlacementCandidate.simple(GeneratorsBlocks.FISSION_REACTOR_CASING);
            case FACE -> pos.y() == 1 ?
                PlacementCandidate.anyOf(
                    inventory,
                    GeneratorsBlocks.FISSION_REACTOR_PORT,
                    GeneratorsBlocks.REACTOR_GLASS
                ) : PlacementCandidate.simple(GeneratorsBlocks.REACTOR_GLASS);
            case INNER -> PlacementCandidate.air();
        };
    }

    @Override
    public BuildPlan createPlan(BuildContext ctx, CompoundTag terminalTag) {
        var plan = ISimpleRelativePosition.super.createPlan(ctx, terminalTag);

        // The inner positions are skipped, so use our own methods here to add fuel and rods
        var dimensions = dimensions(terminalTag);
        var origin = origin(ctx, terminalTag, dimensions);
        var inventory = InventorySnapshot.of(ctx.player());

        addFuelColumns(ctx, plan, origin, dimensions, inventory);

        return plan;
    }

    private void addFuelColumns(
        BuildContext ctx,
        BuildPlan plan,
        BlockPos origin,
        BuildDimensions dimensions,
        InventorySnapshot inventory
    ) {
        var fuelBlock = GeneratorsBlocks.FISSION_FUEL_ASSEMBLY.getBlock();
        var rodBlock = GeneratorsBlocks.CONTROL_ROD_ASSEMBLY.getBlock();

        var fuelItem = fuelBlock.asItem();
        var rodItem = rodBlock.asItem();

        int fuelLeft = inventory.count(fuelItem);
        int rodsLeft = inventory.count(rodItem);

        if (fuelLeft <= 0 || rodsLeft <= 0) {
            return;
        }

        // control rod is y = H - 2.
        // fuel can occupy y = 1 ... H - 3.
        int maxFuelHeight = dimensions.height() - 3;

        for (int x = 1; x < dimensions.width() - 1; x++) {
            for (int z = 1; z < dimensions.depth() - 1; z++) {
                if (!isFuelColumnPosition(x, z)) {
                    continue;
                }

                if (fuelLeft <= 0 || rodsLeft <= 0) {
                    return;
                }

                int fuelHeight = Math.min(maxFuelHeight, fuelLeft);
                if (fuelHeight <= 0) {
                    return;
                }

                if (!canPlaceOrKeepColumn(ctx, origin, x, z, fuelHeight, fuelBlock, rodBlock)) {
                    continue;
                }

                for (int y = 1; y <= fuelHeight; y++) {
                    var worldPos = origin.offset(x, y, z);
                    var state = ctx.level().getBlockState(worldPos);

                    if (state.isAir()) {
                        plan.add(PlacementCandidate.simple(fuelBlock).toPlacement(worldPos));
                        fuelLeft--;
                    }
                }

                var rodPos = origin.offset(x, fuelHeight + 1, z);
                var rodState = ctx.level().getBlockState(rodPos);
                if (rodState.isAir()) {
                    plan.add(PlacementCandidate.simple(rodBlock).toPlacement(rodPos));
                    rodsLeft--;
                }
            }
        }
    }

    private boolean isFuelColumnPosition(int x, int z) {
        return ((x + z) & 1) == 0;
    }

    private boolean canPlaceOrKeepColumn(
        BuildContext ctx,
        BlockPos origin,
        int x,
        int z,
        int fuelHeight,
        Block fuelBlock,
        Block rodBlock
    ) {
        for (int y = 1; y <= fuelHeight; y++) {
            var state = ctx.level().getBlockState(origin.offset(x, y, z));
            if (!state.isAir() && !state.is(fuelBlock)) {
                return false;
            }
        }

        var rodState = ctx.level().getBlockState(origin.offset(x, fuelHeight + 1, z));
        return rodState.isAir() || rodState.is(rodBlock);
    }

    @Override
    public Map<BlockPos, BlockState> previewStates(CompoundTag terminalTag) {
        var dimensions = dimensions(terminalTag);
        var result = ISimpleRelativePosition.super.previewStates(terminalTag);

        int maxFuelHeight = dimensions.height() - 3;

        for (int x = 1; x < dimensions.width() - 1; x++) {
            for (int z = 1; z < dimensions.depth() - 1; z++) {
                if (!isFuelColumnPosition(x, z)) {
                    continue;
                }

                for (int y = 1; y <= maxFuelHeight; y++) {
                    result.put(
                        new BlockPos(x, y, z),
                        GeneratorsBlocks.FISSION_FUEL_ASSEMBLY.getBlock().defaultBlockState()
                    );
                }

                result.put(
                    new BlockPos(x, maxFuelHeight + 1, z),
                    GeneratorsBlocks.CONTROL_ROD_ASSEMBLY.getBlock().defaultBlockState()
                );
            }
        }

        return result;
    }
}
