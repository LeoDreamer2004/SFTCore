package org.leodreamer.sftcore.integration.ponder;

import appeng.core.definitions.AEParts;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import net.createmod.ponder.api.registration.MultiSceneBuilder;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import org.leodreamer.sftcore.integration.ponder.api.SFTGeneralStoryBoard;
import org.leodreamer.sftcore.integration.ponder.api.SFTStoryBoard;
import org.leodreamer.sftcore.integration.ponder.scene.AEAutomationScenes;
import org.leodreamer.sftcore.util.RLUtils;

import static org.leodreamer.sftcore.integration.ponder.SFTPonderGroups.*;
import static org.leodreamer.sftcore.integration.ponder.SFTPonderTags.AE_AUTOMATION;

public class SFTPonderScenes {
    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderSceneRegistrationHelper<ItemLike> HELPER = helper.withKeyFunction(RLUtils::getItemRL);

        of(HELPER, CPU).add(
                "ponder_crafting_processing_unit",
                AEAutomationScenes::craftingCPU,
                AE_AUTOMATION
        );

        of(HELPER, INTERFACE).add(
                "ponder_ae_interface",
                AEAutomationScenes::interfaceBasic,
                AE_AUTOMATION
        );

        of(HELPER, AEParts.ANNIHILATION_PLANE).add(
                "ponder_annihilation_plane",
                AEAutomationScenes::annihilationPlane,
                AE_AUTOMATION
        ).add(
                "ponder_annihilation_plane_filter",
                AEAutomationScenes::annihilationPlaneFilter,
                AE_AUTOMATION
        );

        of(HELPER, AEParts.FORMATION_PLANE, EPPItemAndBlock.ACTIVE_FORMATION_PLANE).add(
                "ponder_formation_plane",
                AEAutomationScenes::formationPlane,
                AE_AUTOMATION
        );

        of(HELPER, MOLECULAR_ASSEMBLER).add(
                "ponder_molecular_assembler",
                AEAutomationScenes::molecularAssembler,
                AE_AUTOMATION
        );

        of(HELPER, concat(PATTERN_PROVIDER, concat(CPU, MOLECULAR_ASSEMBLER))).add(
                "ponder_ae_crafting_system",
                AEAutomationScenes::craftingSystem,
                AE_AUTOMATION
        );

        of(HELPER, concat(PATTERN, PATTERN_PROVIDER)).add(
                "ponder_pattern_provider",
                AEAutomationScenes::craftingPrinciple,
                AE_AUTOMATION
        ).add(
                "ponder_ae_crafting_parallel",
                AEAutomationScenes::craftingParallel,
                AE_AUTOMATION
        );

        of(HELPER, concat(PATTERN_PROVIDER, INTERFACE)).add(
                "ponder_pattern_provider_interaction",
                AEAutomationScenes::providerSubnetwork,
                AE_AUTOMATION
        );
    }

    @SafeVarargs
    private static <T> StoryBoardBuilder<T> of(PonderSceneRegistrationHelper<T> helper, T... components) {
        return new StoryBoardBuilder<>(helper, components);
    }

    private static class StoryBoardBuilder<T> {
        private final MultiSceneBuilder builder;

        @SafeVarargs
        StoryBoardBuilder(PonderSceneRegistrationHelper<T> helper, T... components) {
            builder = helper.forComponents(components);
        }

        StoryBoardBuilder<T> add(String schematic, SFTStoryBoard board, ResourceLocation... tag) {
            builder.addStoryBoard(schematic, new SFTGeneralStoryBoard(board, schematic), tag);
            return this;
        }
    }
}
