package org.leodreamer.sftcore.common.item.terminal.builder.impl;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.common.item.terminal.api.BuildDimensions;
import org.leodreamer.sftcore.common.item.terminal.api.InventorySnapshot;
import org.leodreamer.sftcore.common.item.terminal.api.PlacementCandidate;
import org.leodreamer.sftcore.common.item.terminal.api.RelativeBuildPos;
import org.leodreamer.sftcore.common.item.terminal.builder.ICubeShapedBuilder;
import org.leodreamer.sftcore.common.item.terminal.builder.ISimpleRelativePosition;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import mekanism.common.registries.MekanismBlocks;

public final class ThermalBoilerBuilder
    implements ISimpleRelativePosition, ICubeShapedBuilder {

    @Override
    public ResourceLocation id() {
        return SFTCore.id("thermal_boiler");
    }

    @Override
    public Block clickAt() {
        return MekanismBlocks.BOILER_CASING.getBlock();
    }

    @Override
    public BuildDimensions dimensions(CompoundTag terminalTag) {
        var config = ThermalBoilerConfig.resolve(terminalTag);

        return new BuildDimensions(
            config.getWidth(),
            config.getHeight(),
            config.getDepth()
        );
    }

    @Override
    public PlacementCandidate candidateForPart(
        CompoundTag terminalTag,
        Part part,
        RelativeBuildPos pos,
        InventorySnapshot inventory
    ) {
        var config = ThermalBoilerConfig.resolve(terminalTag);
        int disperserY = config.getDisperserY();

        if (pos.y() == disperserY) {
            return PlacementCandidate.simple(
                part == Part.INNER ?
                    MekanismBlocks.PRESSURE_DISPERSER : MekanismBlocks.BOILER_CASING
            );
        }

        return switch (part) {
            case CORNER, EDGE -> PlacementCandidate.simple(MekanismBlocks.BOILER_CASING);
            case FACE -> pos.y() == 1 ?
                PlacementCandidate.anyOf(
                    inventory,
                    MekanismBlocks.BOILER_VALVE,
                    MekanismBlocks.STRUCTURAL_GLASS
                ) :
                PlacementCandidate.simple(MekanismBlocks.STRUCTURAL_GLASS);
            case INNER -> pos.y() > disperserY ? PlacementCandidate.air() :
                PlacementCandidate.simple(MekanismBlocks.SUPERHEATING_ELEMENT);
        };
    }
}
