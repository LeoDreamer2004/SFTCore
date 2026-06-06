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
import mekanism.generators.common.registries.GeneratorsBlocks;

public final class IndustrialTurbineBuilder
    implements ISimpleRelativePosition, ICubeShapedBuilder {

    @Override
    public ResourceLocation id() {
        return SFTCore.id("industrial_turbine");
    }

    @Override
    public Block clickAt() {
        return GeneratorsBlocks.TURBINE_CASING.getBlock();
    }

    @Override
    public BuildDimensions dimensions(
        CompoundTag terminalTag
    ) {
        var config = IndustrialTurbineConfig.resolve(terminalTag);
        int width = config.getWidth();

        return new BuildDimensions(width, config.getHeight(), width);
    }

    enum LayerType {
        ABOVE,
        COMPLEX,
        BELOW
    }

    @Override
    public PlacementCandidate candidateForPart(
        CompoundTag terminalTag,
        Part part,
        RelativeBuildPos pos,
        InventorySnapshot inventory
    ) {
        var config = IndustrialTurbineConfig.resolve(terminalTag);
        var dimensions = dimensions(terminalTag);
        int center = dimensions.width() / 2;

        int complexY = config.getComplexY();
        LayerType layer;
        if (pos.y() > complexY) {
            layer = LayerType.ABOVE;
        } else if (pos.y() == complexY) {
            layer = LayerType.COMPLEX;
        } else {
            layer = LayerType.BELOW;
        }

        return switch (part) {
            case CORNER, EDGE -> PlacementCandidate.simple(GeneratorsBlocks.TURBINE_CASING);
            case FACE -> surfaceCandidate(pos, layer, inventory);
            case INNER -> innerCandidate(config, pos, layer, center);
        };
    }

    private PlacementCandidate surfaceCandidate(
        RelativeBuildPos pos,
        LayerType layer,
        InventorySnapshot inventory
    ) {
        if (pos.y() == 0) {
            return PlacementCandidate.simple(GeneratorsBlocks.TURBINE_CASING);
        }

        if (pos.y() == 1) {
            return PlacementCandidate.anyOf(
                inventory,
                GeneratorsBlocks.TURBINE_VALVE,
                MekanismBlocks.STRUCTURAL_GLASS
            );
        }

        return switch (layer) {
            case ABOVE -> PlacementCandidate.simple(GeneratorsBlocks.TURBINE_VENT);
            case COMPLEX -> PlacementCandidate.simple(GeneratorsBlocks.TURBINE_CASING);
            case BELOW -> PlacementCandidate.simple(MekanismBlocks.STRUCTURAL_GLASS);
        };
    }

    private PlacementCandidate innerCandidate(
        IndustrialTurbineConfig config,
        RelativeBuildPos pos,
        LayerType layer,
        int center
    ) {
        int upperY = config.getFirstUpperY();
        if (pos.x() == center && pos.z() == center) {
            if (pos.y() == upperY) {
                return PlacementCandidate.simple(GeneratorsBlocks.ELECTROMAGNETIC_COIL);
            }
            return switch (layer) {
                case ABOVE -> PlacementCandidate.simple(GeneratorsBlocks.SATURATING_CONDENSER);
                case COMPLEX -> PlacementCandidate.simple(GeneratorsBlocks.ROTATIONAL_COMPLEX);
                case BELOW -> PlacementCandidate.simple(GeneratorsBlocks.TURBINE_ROTOR);
            };
        }

        if (pos.y() == upperY && isCoilPosition(config.getRotorCount(), pos, center)) {
            return PlacementCandidate.simple(GeneratorsBlocks.ELECTROMAGNETIC_COIL);
        }
        return switch (layer) {
            case ABOVE -> PlacementCandidate.simple(GeneratorsBlocks.SATURATING_CONDENSER);
            case COMPLEX -> PlacementCandidate.simple(MekanismBlocks.PRESSURE_DISPERSER);
            case BELOW -> PlacementCandidate.air();
        };
    }

    /**
     * Coil layout on the first upper layer:
     * 
     * <pre>
     * R <= 6       7 <= R <= 10        R > 10
     * . . .            . C .           C C C
     * C C C            C C C           C C C
     * . . .            . C .           C C C
     * </pre>
     */
    private boolean isCoilPosition(
        int rotorCount,
        RelativeBuildPos pos,
        int center
    ) {
        int dx = Math.abs(pos.x() - center);
        int dz = Math.abs(pos.z() - center);

        if (rotorCount <= 6) {
            return dz == 0 && dx <= 1;
        }
        if (rotorCount <= 10) {
            return dx + dz <= 1;
        }
        return dx <= 1 && dz <= 1;
    }
}
