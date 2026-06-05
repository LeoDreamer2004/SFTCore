package org.leodreamer.sftcore.common.item.terminal.builder.impl;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.terminal.api.*;
import org.leodreamer.sftcore.common.item.terminal.builder.IControllerRelativePosition;
import org.leodreamer.sftcore.common.item.terminal.builder.ICubeShapedBuilder;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import mekanism.common.registries.MekanismBlocks;

@DataGenScanned
public class ThermalEvaporationBuilder implements IControllerRelativePosition, ICubeShapedBuilder {

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
    public RelativeBuildPos controllerInDimension(
        CompoundTag terminalTag,
        BuildDimensions dimensions
    ) {
        return ThermalEvaporationConfig.resolve(terminalTag).controllerInDimension();
    }

    @Override
    public PlacementCandidate candidateForPart(
        CompoundTag terminalTag,
        Part part,
        RelativeBuildPos pos,
        InventorySnapshot inventory
    ) {
        return pos.y() == dimensions(terminalTag).height() - 1 ?
            switch (part) {
                case CORNER -> PlacementCandidate
                    .simple(MekanismBlocks.THERMAL_EVAPORATION_BLOCK);
                case EDGE -> PlacementCandidate
                    .simple(MekanismBlocks.STRUCTURAL_GLASS);
                case FACE, INNER -> PlacementCandidate.air();
            } :
            switch (part) {
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
