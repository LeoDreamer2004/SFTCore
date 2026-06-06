package org.leodreamer.sftcore.common.item.terminal.builder.impl;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.common.item.terminal.api.*;
import org.leodreamer.sftcore.common.item.terminal.builder.ICubeShapedBuilder;
import org.leodreamer.sftcore.common.item.terminal.builder.ISidedControllerRelativePosition;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import mekanism.common.registries.MekanismBlocks;

import java.util.ArrayList;

@DataGenScanned
public class ThermalEvaporationBuilder implements ISidedControllerRelativePosition, ICubeShapedBuilder {

    @Override
    public ResourceLocation id() {
        return SFTCore.id("thermal_evaporation");
    }

    @Override
    public Block controllerBlock() {
        return MekanismBlocks.THERMAL_EVAPORATION_CONTROLLER.getBlock();
    }

    @Override
    public BuildDimensions dimensions(CompoundTag terminalTag) {
        var config = ThermalEvaporationConfig.resolve(terminalTag);

        return new BuildDimensions(
            ThermalEvaporationConfig.WIDTH,
            config.getHeight(),
            ThermalEvaporationConfig.DEPTH
        );
    }

    @Override
    public int controllerY(CompoundTag terminalTag, BuildDimensions dimensions) {
        return 1;
    }

    @Override
    public int controllerSideOffset(CompoundTag terminalTag, BuildDimensions dimensions) {
        return 1; // 1 or 2 are both OK. Use 1 for default
    }

    @Override
    public PlacementCandidate candidateForPart(
        CompoundTag terminalTag,
        Part part,
        RelativeBuildPos pos,
        InventorySnapshot inventory
    ) {
        if (pos.y() == dimensions(terminalTag).height() - 1)
            return switch (part) {
                case CORNER -> PlacementCandidate
                    .simple(MekanismBlocks.THERMAL_EVAPORATION_BLOCK);
                case EDGE -> PlacementCandidate
                    .simple(MekanismBlocks.STRUCTURAL_GLASS);
                case FACE, INNER -> PlacementCandidate.air();
            };

        return switch (part) {
            case CORNER, EDGE -> PlacementCandidate
                .simple(MekanismBlocks.THERMAL_EVAPORATION_BLOCK);
            case FACE -> pos.y() == 1 ?
                PlacementCandidate.anyOf(
                    inventory,
                    MekanismBlocks.THERMAL_EVAPORATION_VALVE,
                    MekanismBlocks.STRUCTURAL_GLASS
                ) :
                PlacementCandidate.simple(MekanismBlocks.STRUCTURAL_GLASS);
            case INNER -> PlacementCandidate.air();
        };
    }
}
