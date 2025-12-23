package org.leodreamer.sftcore.integration.ponder;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.integration.ponder.api.SFTGeneralStoryBoard;
import org.leodreamer.sftcore.integration.ponder.api.SFTStoryBoard;
import org.leodreamer.sftcore.integration.ponder.api.annotation.PonderScene;
import org.leodreamer.sftcore.integration.ponder.api.annotation.PonderSceneScanned;
import org.leodreamer.sftcore.integration.ponder.api.annotation.WithTags;
import org.leodreamer.sftcore.integration.ponder.misc.SFTPonderGroup;
import org.leodreamer.sftcore.integration.ponder.misc.SFTPonderTag;
import org.leodreamer.sftcore.util.RLUtils;
import org.leodreamer.sftcore.util.ReflectUtils;

import net.minecraft.resources.ResourceLocation;

import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SFTPonderScenes {

    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        for (var clazz : ReflectUtils.getClassesWithAnnotation(PonderSceneScanned.class)) {
            var withTags = clazz.getAnnotation(WithTags.class);
            List<SFTPonderTag> tags = withTags == null ? new ArrayList<>() : Arrays.asList(withTags.value());

            for (var method : clazz.getDeclaredMethods()) {
                var scene = method.getAnnotation(PonderScene.class);
                if (scene == null) {
                    continue;
                }
                var schematic = scene.file();

                var tagsCopy = new ArrayList<>(tags);
                var extraTags = method.getAnnotation(WithTags.class);
                if (extraTags != null) {
                    tagsCopy.addAll(Arrays.asList(extraTags.value()));
                }
                var methodTags = tagsCopy.stream().map(SFTPonderTag::getRl).toArray(ResourceLocation[]::new);

                SFTStoryBoard func = (builder, util) -> {
                    try {
                        method.invoke(null, builder, util);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        SFTCore.LOGGER.error(
                            "Failed to invoke ponder scene method {} in class {}",
                            method.getName(),
                            clazz.getName(),
                            e
                        );
                        SFTCore.LOGGER.error(
                            "Expected a static method BiConsumer<SFTSceneBuilder, SceneBuildingUtil>"
                        );
                    }
                };

                var components = Arrays.stream(scene.groups())
                    .map(SFTPonderGroup::getComponents)
                    .flatMap(Arrays::stream)
                    .map(RLUtils::getItemRL)
                    .toList();

                helper
                    .forComponents(components)
                    .addStoryBoard(schematic, new SFTGeneralStoryBoard(func, schematic), methodTags);
            }
        }
    }
}
