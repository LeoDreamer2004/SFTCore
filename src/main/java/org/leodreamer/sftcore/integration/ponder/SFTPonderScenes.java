package org.leodreamer.sftcore.integration.ponder;

import mekanism.common.content.blocktype.FactoryType;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.tier.FactoryTier;
import net.createmod.ponder.api.registration.MultiSceneBuilder;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import org.leodreamer.sftcore.integration.ponder.api.SFTGeneralStoryBoard;
import org.leodreamer.sftcore.integration.ponder.api.SFTStoryBoard;
import org.leodreamer.sftcore.integration.ponder.scene.AEAutomationExamplesScenes;
import org.leodreamer.sftcore.integration.ponder.scene.AEAutomationScenes;
import org.leodreamer.sftcore.integration.ponder.scene.AENetworkScenes;
import org.leodreamer.sftcore.integration.ponder.scene.AEStorageScenes;
import org.leodreamer.sftcore.util.RLUtils;

import java.util.Arrays;

import static org.leodreamer.sftcore.integration.ponder.SFTPonderGroups.*;
import static org.leodreamer.sftcore.integration.ponder.SFTPonderTags.*;

public class SFTPonderScenes {
    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderSceneRegistrationHelper<ItemLike> HELPER = helper.withKeyFunction(RLUtils::getItemRL);

        of(HELPER, CONTROLLER).add(
                "ponder_me_controller",
                AENetworkScenes::controller,
                AE_NETWORK
        );

        of(HELPER, QUANTUM_BRIDGE).add(
                "ponder_quantum_network_bridge",
                AENetworkScenes::quantumBridge,
                AE_NETWORK
        );

        of(HELPER, IMPORT_BUS, EXPORT_BUS).add(
                "ponder_import_export_bus",
                AEStorageScenes::portBuses,
                AE_STORAGE
        );

        of(HELPER, STORAGE_BUS).add(
                "ponder_storage_bus",
                AEStorageScenes::storageBus,
                AE_STORAGE
        );

        of(HELPER, STORAGE_BUS, INTERFACE).add(
                "ponder_storage_bus_interface_interaction",
                AEStorageScenes::subnetwork,
                AE_STORAGE
        );

        of(HELPER, IO_PORT).add(
                "ponder_io_port",
                AEStorageScenes::ioPort,
                AE_STORAGE
        ).add(
                "ponder_io_port_output",
                AEStorageScenes::ioPortOutput,
                AE_STORAGE
        );

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

        of(HELPER, ANNIHILATION_PLANE).add(
                "ponder_annihilation_plane",
                AEAutomationScenes::annihilationPlane,
                AE_AUTOMATION
        ).add(
                "ponder_annihilation_plane_filter",
                AEAutomationScenes::annihilationPlaneFilter,
                AE_AUTOMATION
        );

        of(HELPER, FORMATION_PLANE).add(
                "ponder_formation_plane",
                AEAutomationScenes::formationPlane,
                AE_AUTOMATION
        );

        of(HELPER, MOLECULAR_ASSEMBLER).add(
                "ponder_molecular_assembler",
                AEAutomationScenes::molecularAssembler,
                AE_AUTOMATION
        );

        of(HELPER, PATTERN_PROVIDER, CPU, MOLECULAR_ASSEMBLER).add(
                "ponder_ae_crafting_system",
                AEAutomationScenes::craftingSystem,
                AE_AUTOMATION
        );

        of(HELPER, PATTERN, PATTERN_PROVIDER).add(
                "ponder_pattern_provider",
                AEAutomationScenes::craftingPrinciple,
                AE_AUTOMATION
        ).add(
                "ponder_ae_crafting_parallel",
                AEAutomationScenes::craftingParallel,
                AE_AUTOMATION
        );

        of(HELPER, PATTERN_PROVIDER, INTERFACE).add(
                "ponder_pattern_provider_interaction",
                AEAutomationScenes::providerSubnetwork,
                AE_AUTOMATION
        );

        of(HELPER,
                MekanismBlocks.METALLURGIC_INFUSER,
                MekanismBlocks.getFactory(FactoryTier.BASIC, FactoryType.INFUSING),
                MekanismBlocks.getFactory(FactoryTier.ADVANCED, FactoryType.INFUSING),
                MekanismBlocks.getFactory(FactoryTier.ELITE, FactoryType.INFUSING),
                MekanismBlocks.getFactory(FactoryTier.ULTIMATE, FactoryType.INFUSING)
        ).add(
                "ponder_infusion_automation",
                AEAutomationExamplesScenes::metallurgicInfuser,
                AE_AUTOMATION_EXAMPLES
        );
    }

    @SafeVarargs
    private static <T> StoryBoardBuilder<T> of(PonderSceneRegistrationHelper<T> helper, T... components) {
        return new StoryBoardBuilder<>(helper, Arrays.asList(components));
    }

    @SafeVarargs
    private static <T> StoryBoardBuilder<T> of(PonderSceneRegistrationHelper<T> helper, T[]... components) {
        return new StoryBoardBuilder<>(helper, Arrays.stream(components).flatMap(Arrays::stream).toList());
    }

    private static class StoryBoardBuilder<T> {
        private final MultiSceneBuilder builder;

        StoryBoardBuilder(PonderSceneRegistrationHelper<T> helper, Iterable<T> components) {
            builder = helper.forComponents(components);
        }

        StoryBoardBuilder<T> add(String schematic, SFTStoryBoard board, ResourceLocation... tag) {
            builder.addStoryBoard(schematic, new SFTGeneralStoryBoard(board, schematic), tag);
            return this;
        }
    }
}
