package org.leodreamer.sftcore.common.item.terminal.builder.impl;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.terminal.api.*;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import mekanism.api.providers.IBlockProvider;
import mekanism.common.registries.MekanismBlocks;
import org.leodreamer.sftcore.common.item.terminal.builder.ICubeRelativePosition;
import org.leodreamer.sftcore.common.item.terminal.builder.ICubeShapedBuilder;

import java.util.List;

@DataGenScanned
public class InductionMatrixBuilder implements ICubeShapedBuilder, ICubeRelativePosition {

    @RegisterLanguage("Induction Matrix")
    public static final String TITLE = "item.sftcore.mek_terminal.tab.induction";

    @RegisterLanguage("Right-click the induction casing with Shift to start building")
    public static final String INVALID_START = "item.sftcore.mek_terminal.invalid_induction_start";

    @Override
    public ResourceLocation id() {
        return SFTCore.id("induction_matrix");
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
    public BuildDimensions dimensions(BuildContext ctx, CompoundTag terminalTag) {
        var config = InductionMatrixConfig.resolve(terminalTag);
        return new BuildDimensions(config.getWidth(), config.getHeight(), config.getDepth());
    }

    private static final List<IBlockProvider> INDUCTION_CELLS = List.of(
        MekanismBlocks.ULTIMATE_INDUCTION_CELL,
        MekanismBlocks.ULTIMATE_INDUCTION_PROVIDER,
        MekanismBlocks.ELITE_INDUCTION_CELL,
        MekanismBlocks.ELITE_INDUCTION_PROVIDER,
        MekanismBlocks.ADVANCED_INDUCTION_CELL,
        MekanismBlocks.ADVANCED_INDUCTION_PROVIDER,
        MekanismBlocks.BASIC_INDUCTION_CELL,
        MekanismBlocks.BASIC_INDUCTION_PROVIDER
    );

    @Override
    public PlacementCandidate candidateForPart(
        CompoundTag terminalTag,
        Part part,
        RelativeBuildPos pos,
        InventorySnapshot inventory
    ) {
        return switch (part) {
            case FRAME -> PlacementCandidate
                .simple(MekanismBlocks.INDUCTION_CASING.getBlock())
                .consumeItem(!pos.isOrigin());
            case FACE -> PlacementCandidate
                .simple(MekanismBlocks.STRUCTURAL_GLASS.getBlock());
            case INNER -> PlacementCandidate.anyOf(
                inventory,
                INDUCTION_CELLS.stream().map(IBlockProvider::getBlock)
                    .map(PlacementCandidate::simple).toList()
            );
        };
    }
}
