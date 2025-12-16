package org.leodreamer.sftcore.integration.ponder;

import appeng.core.definitions.AEParts;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import org.leodreamer.sftcore.util.RLUtils;
import org.leodreamer.sftcore.integration.ponder.api.SFTGeneralStoryBoard;
import org.leodreamer.sftcore.integration.ponder.api.SFTStoryBoard;
import org.leodreamer.sftcore.integration.ponder.scene.AEAutomationScenes;

import static org.leodreamer.sftcore.integration.ponder.SFTPonderGroups.*;

public class SFTPonderScenes {
    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderSceneRegistrationHelper<ItemLike> HELPER = helper.withKeyFunction(RLUtils::getItemRL);

        add(
                HELPER, CPU,
                "ponder_crafting_processing_unit",
                AEAutomationScenes::craftingCPU,
                SFTPonderTags.AE_AUTOMATION
        );

        add(
                HELPER, INTERFACE,
                "ponder_ae_interface",
                AEAutomationScenes::interfaceBasic,
                SFTPonderTags.AE_AUTOMATION
        );

        add(
                HELPER, new ItemLike[]{AEParts.ANNIHILATION_PLANE},
                "ponder_annihilation_plane",
                AEAutomationScenes::annihilationPlane,
                SFTPonderTags.AE_AUTOMATION
        );

        add(
                HELPER, new ItemLike[]{AEParts.ANNIHILATION_PLANE},
                "ponder_annihilation_plane_filter",
                AEAutomationScenes::annihilationPlaneFilter,
                SFTPonderTags.AE_AUTOMATION
        );

        add(
                HELPER, concat(PATTERN_PROVIDER, concat(CPU, MOLECULAR_ASSEMBLER)),
                "ae_crafting_system",
                AEAutomationScenes::craftingSystem,
                SFTPonderTags.AE_AUTOMATION
        );
    }

    private static <T> void add(PonderSceneRegistrationHelper<T> helper, T[] components,
                                String sceneId, SFTStoryBoard board, ResourceLocation... tag) {
        helper.forComponents(components)
                .addStoryBoard(sceneId, new SFTGeneralStoryBoard(board, sceneId), tag);
    }
}
