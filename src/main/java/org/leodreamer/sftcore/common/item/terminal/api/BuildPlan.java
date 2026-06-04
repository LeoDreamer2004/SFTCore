package org.leodreamer.sftcore.common.item.terminal.api;

import java.util.ArrayList;
import java.util.List;

public class BuildPlan {

    private final List<Placement> placements = new ArrayList<>();

    public void add(Placement placement) {
        placements.add(placement);
    }

    public List<Placement> placements() {
        return placements;
    }
}
