package org.leodreamer.sftcore.common.data.models;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.registry.registrate.MachineBuilder;
import com.gregtechceu.gtceu.client.model.machine.overlays.WorkableOverlays;
import com.gregtechceu.gtceu.common.data.models.GTMachineModels;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;

import java.util.Locale;


public final class SFTMachineModels {
    public static MachineBuilder.ModelInitializer createWorkableTieredCustomMachineModel(ResourceLocation baseModel, ResourceLocation overlayDir) {
        return (ctx, prov, builder) -> {
            WorkableOverlays overlays = WorkableOverlays.get(overlayDir, prov.getExistingFileHelper());
            ModelFile parentModel = prov.models().getExistingFile(baseModel);

            final String tierName = GTValues.VN[builder.getOwner().getTier()].toLowerCase(Locale.ROOT);
            builder.forAllStates(state -> {
                BlockModelBuilder model = prov.models().nested().parent(parentModel);
                RecipeLogic.Status status = state.getValue(RecipeLogic.STATUS_PROPERTY);

                GTMachineModels.casingTextures(model, GTCEu.id("block/casings/voltage/" + tierName));
                return GTMachineModels.addWorkableOverlays(overlays, status, model);
            });
        };
    }

    public static MachineBuilder.ModelInitializer createTieredCustomModel(ResourceLocation baseModel) {
        return (ctx, prov, builder) -> {
            ModelFile parentModel = prov.models().getExistingFile(baseModel);

            final String tierName = GTValues.VN[builder.getOwner().getTier()].toLowerCase(Locale.ROOT);
            BlockModelBuilder model = prov.models().nested().parent(parentModel);
            GTMachineModels.casingTextures(model, GTCEu.id("block/casings/voltage/" + tierName));
            builder.forAllStatesModels(state -> model);
        };
    }
}
