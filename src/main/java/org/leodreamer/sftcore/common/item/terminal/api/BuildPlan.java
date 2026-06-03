package org.leodreamer.sftcore.common.item.terminal.api;

import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class BuildPlan {

    private final List<Placement> placements = new ArrayList<>();
    private final List<Component> warnings = new ArrayList<>();

    public void add(Placement placement) {
        placements.add(placement);
    }

    public void warn(Component component) {
        warnings.add(component);
    }

    public List<Placement> placements() {
        return placements;
    }

    public List<Component> warnings() {
        return warnings;
    }
}
