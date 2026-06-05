package org.leodreamer.sftcore.common.item.terminal.builder.impl;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.terminal.api.*;
import org.leodreamer.sftcore.common.item.terminal.builder.ICubeShapedBuilder;
import org.leodreamer.sftcore.common.item.terminal.builder.ISimpleRelativePosition;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import mekanism.common.registries.MekanismBlocks;

@DataGenScanned
public class InductionMatrixBuilder implements ISimpleRelativePosition, ICubeShapedBuilder {

    @RegisterLanguage("Right-click the induction casing with Shift to start building")
    public static final String INVALID_START = "item.sftcore.mek_terminal.invalid_induction_start";

    @Override
    public ResourceLocation id() {
        return SFTCore.id("induction_matrix");
    }

    @Override
    public boolean canStart(BuildContext ctx) {
        return ctx.level()
            .getBlockState(ctx.clicked())
            .is(MekanismBlocks.INDUCTION_CASING.getBlock());
    }

    @Override
    public Component invalidStartMessage() {
        return Component.translatable(INVALID_START);
    }

    @Override
    public BuildDimensions dimensions(CompoundTag terminalTag) {
        var config = InductionMatrixConfig.resolve(terminalTag);
        return new BuildDimensions(config.getWidth(), config.getHeight(), config.getDepth());
    }

    @Override
    public PlacementCandidate candidateForPart(
        CompoundTag terminalTag,
        Part part,
        RelativeBuildPos pos,
        InventorySnapshot inventory
    ) {
        return switch (part) {
            case CORNER, EDGE -> PlacementCandidate
                .simple(MekanismBlocks.INDUCTION_CASING);
            case FACE -> pos.y() == 1 ?
                PlacementCandidate.anyOf(
                    inventory,
                    MekanismBlocks.INDUCTION_PORT,
                    MekanismBlocks.STRUCTURAL_GLASS
                ) :
                PlacementCandidate.simple(MekanismBlocks.STRUCTURAL_GLASS);
            case INNER -> PlacementCandidate.anyOf(
                inventory,
                MekanismBlocks.ULTIMATE_INDUCTION_CELL,
                MekanismBlocks.ULTIMATE_INDUCTION_PROVIDER,
                MekanismBlocks.ELITE_INDUCTION_CELL,
                MekanismBlocks.ELITE_INDUCTION_PROVIDER,
                MekanismBlocks.ADVANCED_INDUCTION_CELL,
                MekanismBlocks.ADVANCED_INDUCTION_PROVIDER,
                MekanismBlocks.BASIC_INDUCTION_CELL,
                MekanismBlocks.BASIC_INDUCTION_PROVIDER
            );
        };
    }
}
