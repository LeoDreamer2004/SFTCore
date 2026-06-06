package org.leodreamer.sftcore.common.item.terminal.builder.impl;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.common.item.terminal.api.BuildContext;
import org.leodreamer.sftcore.common.item.terminal.api.BuildDimensions;
import org.leodreamer.sftcore.common.item.terminal.api.PlacementCandidate;
import org.leodreamer.sftcore.common.item.terminal.api.RelativeBuildPos;
import org.leodreamer.sftcore.common.item.terminal.builder.IControllerRelativePosition;
import org.leodreamer.sftcore.common.item.terminal.builder.IPatternShapedBuilder;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import mekanism.generators.common.registries.GeneratorsBlocks;

@DataGenScanned
public final class FusionReactorBuilder
    implements IPatternShapedBuilder, IControllerRelativePosition {

    /**
     * The controller always occupies the fixed top-center position.
     */
    private static final RelativeBuildPos CONTROLLER = new RelativeBuildPos(2, 4, 2);

    private static final Pattern PATTERN = Pattern.start()
        // y = 0
        .layer(
            "  X  ",
            " XXX ",
            "XXXXX",
            " XXX ",
            "  X  "
        )
        // y = 1
        .layer(
            " XXX ",
            "X   X",
            "X   X",
            "X   X",
            " XXX "
        )
        // y = 2
        .layer(
            "XXPXX",
            "X   X",
            "P   P",
            "X   X",
            "XXPXX"
        )
        // y = 3
        .layer(
            " XXX ",
            "X   X",
            "X   X",
            "X   X",
            " XXX "
        )
        // y = 4
        .layer(
            "  X  ",
            " XXX ",
            "XXCXX",
            " XXX ",
            "  X  "
        )

        .where('X', PlacementCandidate.simple(GeneratorsBlocks.FUSION_REACTOR_FRAME))
        .where(
            'P',
            (inventory, pos) -> PlacementCandidate.anyOf(
                inventory,
                GeneratorsBlocks.FUSION_REACTOR_PORT,
                GeneratorsBlocks.FUSION_REACTOR_FRAME
            )
        )
        .where('C', PlacementCandidate.simple(GeneratorsBlocks.FUSION_REACTOR_CONTROLLER))
        .build();

    @Override
    public ResourceLocation id() {
        return SFTCore.id("fusion_reactor");
    }

    @Override
    public Block controllerBlock() {
        return GeneratorsBlocks.FUSION_REACTOR_CONTROLLER.getBlock();
    }

    @Override
    public RelativeBuildPos controllerInDimension(
        BuildContext ctx,
        CompoundTag terminalTag,
        BuildDimensions dimensions
    ) {
        return CONTROLLER;
    }

    @Override
    public Pattern pattern(CompoundTag terminalTag) {
        return PATTERN;
    }
}
