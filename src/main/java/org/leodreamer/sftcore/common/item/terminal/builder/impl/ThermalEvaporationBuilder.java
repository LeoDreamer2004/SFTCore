package org.leodreamer.sftcore.common.item.terminal.builder.impl;

import mekanism.common.registries.MekanismBlocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.terminal.api.*;
import org.leodreamer.sftcore.common.item.terminal.builder.ICubeShapedBuilder;
import org.leodreamer.sftcore.common.item.terminal.builder.ISidedControllerRelativePosition;

import java.util.ArrayList;

@DataGenScanned
public class ThermalEvaporationBuilder implements ISidedControllerRelativePosition, ICubeShapedBuilder {

    @RegisterLanguage("Right-click the Thermal Evaporation Controller with Shift to start building")
    public static final String INVALID_START = "item.sftcore.mek_terminal.invalid_thermal_evaporation_start";

    @Override
    public ResourceLocation id() {
        return SFTCore.id("thermal_evaporation");
    }

    @Override
    public boolean canStart(BuildContext ctx) {
        return ctx.level()
            .getBlockState(ctx.clicked())
            .is(MekanismBlocks.THERMAL_EVAPORATION_CONTROLLER.getBlock());
    }

    @Override
    public Component invalidStartMessage() {
        return Component.translatable(INVALID_START);
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
    public Iterable<RelativeBuildPos> positions(CompoundTag terminalTag, BuildDimensions dimensions) {
        // simple optimization to skip inner air
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
