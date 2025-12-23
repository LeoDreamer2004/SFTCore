package org.leodreamer.sftcore.integration.ponder.api;

import org.leodreamer.sftcore.SFTCore;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.createmod.ponder.api.scene.PonderStoryBoard;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SFTGeneralStoryBoard implements PonderStoryBoard {

    private final SFTStoryBoard inner;
    private final String schematic;

    public SFTGeneralStoryBoard(SFTStoryBoard inner, String schematic) {
        this.inner = inner;
        this.schematic = schematic;
    }

    @Override
    public void program(SceneBuilder builder, SceneBuildingUtil util) {
        var scene = new SFTSceneBuilder(builder);
        CompoundTag tag = read();
        if (tag != null) {
            fix(tag, scene, util);
        }
        inner.program(scene, util);
    }

    @SuppressWarnings("ConstantConditions")
    private CompoundTag read() {
        var minecraft = Minecraft.getInstance();
        if (minecraft == null) {
            // data gen environment
            return null;
        }
        var resource = minecraft.getResourceManager().getResource(SFTCore.id("ponder/" + schematic + ".nbt"));

        if (resource.isEmpty()) {
            SFTCore.LOGGER.error("Schematic for ponder scene not found: {}", schematic);
            return null;
        }

        try (InputStream is = resource.get().open()) {
            return NbtIo.readCompressed(is);
        } catch (IOException e) {
            SFTCore.LOGGER.error("Failed to read schematic for ponder scene: {}", schematic, e);
            return null;
        }
    }

    private void fix(CompoundTag tag, SFTSceneBuilder scene, SceneBuildingUtil util) {
        var blocks = tag.get("blocks");
        if (blocks instanceof ListTag blocksTag) {
            for (var entry : blocksTag) {
                if (entry instanceof CompoundTag entryTag) {
                    fixOnEntry(entryTag, scene, util);
                }
            }
        }
    }

    private void fixOnEntry(CompoundTag blockEntry, SFTSceneBuilder scene, SceneBuildingUtil util) {
        try {
            var pos = blockEntry.get("pos");
            if (pos instanceof ListTag posTag) {
                var blockPos = posTag.stream()
                        .mapToInt(
                                nbt -> {
                                    if (nbt instanceof IntTag intTag) {
                                        return intTag.getAsInt();
                                    } else {
                                        throw new RuntimeException("Invalid pos tag");
                                    }
                                })
                        .toArray();
                var nbt = blockEntry.getCompound("nbt");
                var cable = nbt.getCompound("cable");
                if (cable.isEmpty()) return;

                final var copy = cable.copy();
                scene
                        .world()
                        .modifyBlockEntityNBT(
                                util.select().position(blockPos[0], blockPos[1], blockPos[2]),
                                BlockEntity.class,
                                (cableNbt) -> {
                                    cableNbt.put("cable", copy);
                                });
            }
        } catch (RuntimeException ignored) {}
    }
}
