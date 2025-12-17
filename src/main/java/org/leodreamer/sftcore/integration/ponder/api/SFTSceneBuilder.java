package org.leodreamer.sftcore.integration.ponder.api;

import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.EntityElement;
import net.createmod.ponder.api.element.TextElementBuilder;
import net.createmod.ponder.api.scene.OverlayInstructions;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SFTSceneBuilder extends CreateSceneBuilder {
    private final SFTWorldInstructions world;

    public SFTSceneBuilder(SceneBuilder baseSceneBuilder) {
        super(baseSceneBuilder);
        this.world = new SFTWorldInstructions();
    }

    public TextElementBuilder text(int duration, String text) {
        return overlay().showText(duration).text(text);
    }

    @Override
    public SFTWorldInstructions world() {
        return world;
    }

    public class SFTWorldInstructions extends WorldInstructions {
        public void killEntity(ElementLink<EntityElement> entity) {
            modifyEntity(entity, Entity::kill);
        }

        public ElementLink<EntityElement> createItemEntity(Vec3 location, ItemStack stack) {
            return createItemEntity(location, new Vec3(0, 0, 0), stack);
        }
    }
}
