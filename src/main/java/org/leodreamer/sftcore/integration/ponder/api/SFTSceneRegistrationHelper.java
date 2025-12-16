package org.leodreamer.sftcore.integration.ponder.api;

import net.createmod.ponder.api.registration.MultiSceneBuilder;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.StoryBoardEntry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public record SFTSceneRegistrationHelper<T>(PonderSceneRegistrationHelper<T> helper) {

    public StoryBoardEntry add(T component, String schematic, SFTStoryBoard board, ResourceLocation... tags) {
        return helper.addStoryBoard(component, schematic, new SFTGeneralStoryBoard(board, schematic), tags);
    }

    public MultiSceneBuilder forComponents(T... components) {
        return helper.forComponents(components);
    }
}
