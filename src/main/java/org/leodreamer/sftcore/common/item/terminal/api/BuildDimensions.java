package org.leodreamer.sftcore.common.item.terminal.api;

public record BuildDimensions(
    int width,
    int height,
    int depth
) {

    public int volume() {
        return width * height * depth;
    }

    public int surface() {
        return 2 * (width * height + width * depth + height * depth);
    }
}
