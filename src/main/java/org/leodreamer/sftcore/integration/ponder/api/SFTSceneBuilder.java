package org.leodreamer.sftcore.integration.ponder.api;

import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.ponder.api.element.TextElementBuilder;
import net.createmod.ponder.api.scene.SceneBuilder;

public class SFTSceneBuilder extends CreateSceneBuilder {
    public SFTSceneBuilder(SceneBuilder baseSceneBuilder) {
        super(baseSceneBuilder);
    }

    public TextElementBuilder text(int duration, String text) {
        return overlay().showText(duration).text(text);
    }
}
