package org.leodreamer.sftcore.common.item.terminal.builder.impl;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.common.item.terminal.api.BuildContext;
import org.leodreamer.sftcore.common.item.terminal.api.BuildDimensions;
import org.leodreamer.sftcore.common.item.terminal.api.PlacementCandidate;
import org.leodreamer.sftcore.common.item.terminal.api.RelativeBuildPos;
import org.leodreamer.sftcore.common.item.terminal.builder.IPatternShapedBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import mekanism.common.registries.MekanismBlocks;

@DataGenScanned
public class SPSBuilder implements IPatternShapedBuilder {

    private static final RelativeBuildPos ANCHOR = new RelativeBuildPos(3, 0, 0);

    private static final Pattern PATTERN = Pattern.start()
        // y = 0
        .layer(
            "  XXX  ",
            " XGGGX ",
            "XGGGGGX",
            "XGGGGGX",
            "XGGGGGX",
            " XGGGX ",
            "  XXX  "
        )
        // y = 1
        .layer(
            " XGGGX ",
            "X     X",
            "G     G",
            "G     G",
            "G     G",
            "X     X",
            " XGGGX "
        )
        // y = 2
        .layer(
            "XGGGGGX",
            "G     G",
            "G     G",
            "G     G",
            "G     G",
            "G     G",
            "XGGGGGX"
        )
        // y = 3
        .layer(
            "XGGPGGX",
            "G     G",
            "G     G",
            "PC    G",
            "G     G",
            "G     G",
            "XGGPGGX"
        )
        // y = 4
        .layer(
            "XGGGGGX",
            "G     G",
            "G     G",
            "G     G",
            "G     G",
            "G     G",
            "XGGGGGX"
        )
        // y = 5
        .layer(
            " XGGGX ",
            "X     X",
            "G     G",
            "G     G",
            "G     G",
            "X     X",
            " XGGGX "
        )
        // y = 6
        .layer(
            "  XXX  ",
            " XGGGX ",
            "XGGGGGX",
            "XGGGGGX",
            "XGGGGGX",
            " XGGGX ",
            "  XXX  "
        )
        .where('X', PlacementCandidate.simple(MekanismBlocks.SPS_CASING))
        .where('G', PlacementCandidate.simple(MekanismBlocks.STRUCTURAL_GLASS))
        .where(
            'P',
            (inv, pos) -> PlacementCandidate
                .anyOf(inv, MekanismBlocks.SPS_PORT, MekanismBlocks.STRUCTURAL_GLASS)
        )
        .where('C', PlacementCandidate.simple(MekanismBlocks.SUPERCHARGED_COIL))
        .build();

    @Override
    public ResourceLocation id() {
        return SFTCore.id("sps");
    }

    @Override
    public Block clickAt(BuildContext ctx) {
        return MekanismBlocks.SPS_CASING.getBlock();
    }

    @Override
    public BlockPos origin(
        BuildContext ctx,
        CompoundTag terminalTag,
        BuildDimensions dimensions
    ) {
        return ANCHOR.minusTo(ctx.clicked());
    }

    @Override
    public Pattern pattern(CompoundTag terminalTag) {
        return PATTERN;
    }
}
