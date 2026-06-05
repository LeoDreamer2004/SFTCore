package org.leodreamer.sftcore.common.item.terminal.builder.impl;

import org.leodreamer.sftcore.common.item.terminal.MekTerminalTags;
import org.leodreamer.sftcore.common.item.terminal.api.RelativeBuildPos;

import net.minecraft.nbt.CompoundTag;

public final class ThermalEvaporationConfig {

    public static final int WIDTH = 4;
    public static final int DEPTH = 4;

    public static final int MIN_HEIGHT = 3;
    public static final int MAX_HEIGHT = 18;
    public static final int DEFAULT_HEIGHT = 4;

    private final CompoundTag tag;

    private ThermalEvaporationConfig(CompoundTag tag) {
        this.tag = tag;
        ensureDefaults();
    }

    public static ThermalEvaporationConfig resolve(CompoundTag terminalTag) {
        var config = terminalTag.getCompound(MekTerminalTags.THERMAL_EVAPORATION);
        terminalTag.put(MekTerminalTags.THERMAL_EVAPORATION, config);
        return new ThermalEvaporationConfig(config);
    }

    public int getHeight() {
        return clampHeight(tag.getInt(MekTerminalTags.THERMAL_EVAPORATION_HEIGHT));
    }

    public void setHeight(int height) {
        tag.putInt(MekTerminalTags.THERMAL_EVAPORATION_HEIGHT, clampHeight(height));
        sanitizeController();
    }

    public int getControllerX() {
        sanitizeController();
        return tag.getInt(MekTerminalTags.THERMAL_EVAPORATION_CONTROLLER_X);
    }

    public void setControllerX(int x) {
        tag.putInt(MekTerminalTags.THERMAL_EVAPORATION_CONTROLLER_X, clampHorizontal(x));
        sanitizeController();
    }

    public int getControllerY() {
        sanitizeController();
        return tag.getInt(MekTerminalTags.THERMAL_EVAPORATION_CONTROLLER_Y);
    }

    public void setControllerY(int y) {
        tag.putInt(MekTerminalTags.THERMAL_EVAPORATION_CONTROLLER_Y, y);
        sanitizeController();
    }

    public int getControllerZ() {
        sanitizeController();
        return tag.getInt(MekTerminalTags.THERMAL_EVAPORATION_CONTROLLER_Z);
    }

    public void setControllerZ(int z) {
        tag.putInt(MekTerminalTags.THERMAL_EVAPORATION_CONTROLLER_Z, clampHorizontal(z));
        sanitizeController();
    }

    public RelativeBuildPos controllerInDimension() {
        sanitizeController();

        return new RelativeBuildPos(
            getControllerX(),
            getControllerY(),
            getControllerZ()
        );
    }

    private void ensureDefaults() {
        if (!tag.contains(MekTerminalTags.THERMAL_EVAPORATION_HEIGHT)) {
            tag.putInt(MekTerminalTags.THERMAL_EVAPORATION_HEIGHT, DEFAULT_HEIGHT);
        }
        if (!tag.contains(MekTerminalTags.THERMAL_EVAPORATION_CONTROLLER_X)) {
            tag.putInt(MekTerminalTags.THERMAL_EVAPORATION_CONTROLLER_X, 1);
        }
        if (!tag.contains(MekTerminalTags.THERMAL_EVAPORATION_CONTROLLER_Y)) {
            tag.putInt(MekTerminalTags.THERMAL_EVAPORATION_CONTROLLER_Y, 1);
        }
        if (!tag.contains(MekTerminalTags.THERMAL_EVAPORATION_CONTROLLER_Z)) {
            tag.putInt(MekTerminalTags.THERMAL_EVAPORATION_CONTROLLER_Z, 0);
        }

        setHeight(getHeight());
        sanitizeController();
    }

    /**
     * Keep controller on one side wall, not a horizontal corner, and not top/bottom.
     */
    private void sanitizeController() {
        int height = getHeight();

        int x = clampHorizontal(tag.getInt(MekTerminalTags.THERMAL_EVAPORATION_CONTROLLER_X));
        int y = clampControllerY(tag.getInt(MekTerminalTags.THERMAL_EVAPORATION_CONTROLLER_Y), height);
        int z = clampHorizontal(tag.getInt(MekTerminalTags.THERMAL_EVAPORATION_CONTROLLER_Z));

        boolean xBoundary = x == 0 || x == WIDTH - 1;
        boolean zBoundary = z == 0 || z == DEPTH - 1;

        if (xBoundary == zBoundary) {
            x = 1;
            z = 0;
        }

        tag.putInt(MekTerminalTags.THERMAL_EVAPORATION_CONTROLLER_X, x);
        tag.putInt(MekTerminalTags.THERMAL_EVAPORATION_CONTROLLER_Y, y);
        tag.putInt(MekTerminalTags.THERMAL_EVAPORATION_CONTROLLER_Z, z);
    }

    private int clampHeight(int value) {
        return Math.max(MIN_HEIGHT, Math.min(MAX_HEIGHT, value));
    }

    private int clampHorizontal(int value) {
        return Math.max(0, Math.min(3, value));
    }

    private int clampControllerY(int value, int height) {
        int min = 1;
        int max = Math.max(1, height - 2);
        return Math.max(min, Math.min(max, value));
    }
}
