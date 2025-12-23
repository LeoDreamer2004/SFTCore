package org.leodreamer.sftcore.common.data;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.common.cover.AccelerateCover;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.cover.CoverDefinition;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.client.renderer.cover.SimpleCoverRenderer;

import java.util.Arrays;
import java.util.List;

public final class SFTCovers {

    public static final List<CoverDefinition> ACCELERATE_COVERS = Arrays.stream(AccelerateCover.TIERS)
            .mapToObj(
                    tier -> register(
                            GTValues.VN[tier].toLowerCase() + "_accelerate_cover",
                            (a, b, c) -> new AccelerateCover(a, b, c, tier)))
            .toList();

    public static CoverDefinition register(
                                           String id, CoverDefinition.CoverBehaviourProvider behaviorCreator) {
        var definition = new CoverDefinition(
                SFTCore.id(id),
                behaviorCreator,
                () -> () -> new SimpleCoverRenderer(SFTCore.id("block/cover/" + id)));
        GTRegistries.COVERS.register(definition.getId(), definition);
        return definition;
    }
}
