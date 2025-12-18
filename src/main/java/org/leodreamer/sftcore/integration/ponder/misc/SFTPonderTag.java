package org.leodreamer.sftcore.integration.ponder.misc;

import appeng.api.util.AEColor;
import appeng.core.definitions.*;
import lombok.Getter;
import mekanism.common.registries.MekanismBlocks;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import org.leodreamer.sftcore.SFTCore;

@Getter
public enum SFTPonderTag {

    AE_NETWORK("ae2_network"),
    AE_STORAGE("ae_storage"),
    AE_AUTOMATION("ae2_automation"),
    AE_AUTOMATION_EXAMPLES("ae2_automation_examples");

    private final ResourceLocation rl;

    SFTPonderTag(String name) {
        rl = SFTCore.id(name);
    }

    public static void register(PonderTagRegistrationHelper<ResourceLocation> helper) {

        PonderTagRegistrationHelper<ItemDefinition<?>> iHelper = helper.withKeyFunction(ItemDefinition::id);
        PonderTagRegistrationHelper<ColoredItemDefinition<?>> cHelper = helper.withKeyFunction((a) -> a.id(AEColor.TRANSPARENT));

        helper.registerTag(AE_NETWORK.rl)
                .addToIndex()
                .item(AEBlocks.CONTROLLER)
                .title("Networks in Applied Energistics 2")
                .description("Building a powerful ME Network starts from here")
                .register();

        iHelper.addToTag(AE_NETWORK.rl)
                .add(AEBlocks.ENERGY_ACCEPTOR)
                .add(AEBlocks.ENERGY_CELL)
                .add(AEParts.QUARTZ_FIBER)
                .add(AEBlocks.CONTROLLER)
                .add(AEBlocks.QUANTUM_LINK);

        cHelper.addToTag(AE_NETWORK.rl)
                .add(AEParts.GLASS_CABLE)
                .add(AEParts.COVERED_CABLE)
                .add(AEParts.SMART_CABLE)
                .add(AEParts.COVERED_DENSE_CABLE)
                .add(AEParts.SMART_DENSE_CABLE);

        helper.registerTag(AE_STORAGE.rl)
                .addToIndex()
                .item(AEItems.ITEM_CELL_1K, true, false)
                .title("Storage Solutions in Applied Energistics 2")
                .description("Store massive amounts of items and fluids with ease")
                .register();

        iHelper.addToTag(AE_STORAGE.rl)
                .add(AEBlocks.DRIVE)
                .add(AEItems.ITEM_CELL_1K)
                .add(AEItems.FLUID_CELL_1K)
                .add(AEParts.IMPORT_BUS)
                .add(AEParts.EXPORT_BUS)
                .add(AEParts.STORAGE_BUS)
                .add(AEBlocks.IO_PORT);

        helper.registerTag(AE_AUTOMATION.rl)
                .addToIndex()
                .item(AEBlocks.PATTERN_PROVIDER, true, false)
                .title("Automation with Applied Energistics 2")
                .description("Get rid of the pain of Manual Crafting from now on")
                .register();

        iHelper.addToTag(AE_AUTOMATION.rl)
                .add(AEBlocks.INTERFACE)
                .add(AEBlocks.PATTERN_PROVIDER)
                .add(AEBlocks.CRAFTING_STORAGE_1K)
                .add(AEBlocks.MOLECULAR_ASSEMBLER)
                .add(AEParts.ANNIHILATION_PLANE)
                .add(AEParts.FORMATION_PLANE);

        helper.registerTag(AE_AUTOMATION_EXAMPLES.rl)
                .addToIndex()
                .item(MekanismBlocks.METALLURGIC_INFUSER, true, false)
                .title("Automation Examples in Applied Energistics 2")
                .description("To master AE2 automation")
                .register();

        helper.addToTag(AE_AUTOMATION_EXAMPLES.rl)
                .add(MekanismBlocks.METALLURGIC_INFUSER.getRegistryName());
    }
}