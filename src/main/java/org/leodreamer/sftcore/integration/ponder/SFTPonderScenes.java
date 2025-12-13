package org.leodreamer.sftcore.integration.ponder;

import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import org.leodreamer.sftcore.integration.ponder.scene.AEAutomationScenes;

public class SFTPonderScenes {
    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderSceneRegistrationHelper<ItemLike> HELPER = helper.withKeyFunction((item) -> ForgeRegistries.ITEMS.getKey(item.asItem()));

        HELPER.forComponents(SFTPonderGroups.CPU)
                .addStoryBoard("ponder_crafting_processing_unit", AEAutomationScenes::craftingCPU, SFTPonderTags.AE_AUTOMATION);

        HELPER.forComponents(SFTPonderGroups.INTERFACE)
                .addStoryBoard("ponder_ae_interface", AEAutomationScenes::interfaceBasic, SFTPonderTags.AE_AUTOMATION);
    }
}
