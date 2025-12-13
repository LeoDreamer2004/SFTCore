package org.leodreamer.sftcore.integration.ponder;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEParts;
import appeng.core.definitions.ItemDefinition;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import org.leodreamer.sftcore.SFTCore;

public class SFTPonderTags {

    public static final ResourceLocation AE_AUTOMATION = SFTCore.id("ae2_automation");

    public static void register(PonderTagRegistrationHelper<ResourceLocation> helper) {

        PonderTagRegistrationHelper<ItemDefinition<?>> iHelper = helper.withKeyFunction(ItemDefinition::id);

        helper.registerTag(AE_AUTOMATION)
                .addToIndex()
                .item(AEBlocks.PATTERN_PROVIDER, true, false)
                .title("Automation with Applied Energistics 2")
                .description("Get rid of the pain of Manual Crafting from now on")
                .register();

        iHelper.addToTag(AE_AUTOMATION)
                .add(AEBlocks.INTERFACE)
                .add(AEBlocks.PATTERN_PROVIDER)
                .add(AEBlocks.CRAFTING_STORAGE_1K)
                .add(AEBlocks.MOLECULAR_ASSEMBLER)
                .add(AEParts.ANNIHILATION_PLANE)
                .add(AEParts.FORMATION_PLANE);
    }
}