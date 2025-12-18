package org.leodreamer.sftcore.integration.ponder.api;

import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.EntityElement;
import net.createmod.ponder.api.element.TextElementBuilder;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.UnaryOperator;

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

        public void setBlock(BlockPos pos, BlockState state) {
            setBlocks(scene.getSceneBuildingUtil().select().position(pos), state, false);
        }

        public void setBlocks(Selection selection, BlockState state) {
            super.setBlocks(selection, state, false);
        }

        public void modifyBlock(BlockPos pos, UnaryOperator<BlockState> stateFunc) {
            super.modifyBlock(pos, stateFunc, false);
        }

        public void modifyBlocks(Selection sel, UnaryOperator<BlockState> stateFunc) {
            super.modifyBlocks(sel, stateFunc, false);
        }
    }
}
