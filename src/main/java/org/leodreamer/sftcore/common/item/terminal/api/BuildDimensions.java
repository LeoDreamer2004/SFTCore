package org.leodreamer.sftcore.common.item.terminal.api;

public record BuildDimensions(
    int width,
    int height,
    int depth
) {

    public int volume() {
        return width * height * depth;
    }
}
