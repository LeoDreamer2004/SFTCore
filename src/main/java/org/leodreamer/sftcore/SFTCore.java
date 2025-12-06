package org.leodreamer.sftcore;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.cover.CoverDefinition;
import com.gregtechceu.gtceu.api.data.chemical.material.event.MaterialEvent;
import com.gregtechceu.gtceu.api.data.chemical.material.event.MaterialRegistryEvent;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.leodreamer.sftcore.api.registry.SFTRegistrate;
import org.leodreamer.sftcore.common.data.*;
import org.leodreamer.sftcore.common.data.recipe.SFTRecipeTypes;
import org.leodreamer.sftcore.integration.create.SFTCreateDataGen;


@Mod(SFTCore.MOD_ID)
public class SFTCore {
    public static final String MOD_ID = "sftcore";
    public static final SFTRegistrate REGISTRATE = SFTRegistrate.create(MOD_ID);
    public static final String NAME = "SFTCore";
    public static final Logger LOGGER = LogManager.getLogger(NAME);

    public SFTCore(FMLJavaModLoadingContext context) {
        SFTDataGen.init();

        REGISTRATE.registerRegistrate();
        var bus = context.getModEventBus();
        bus.register(this);
        bus.addGenericListener(GTRecipeType.class, this::registerRecipeTypes);
        bus.addGenericListener(MachineDefinition.class, this::registerMachines);
        bus.addGenericListener(CoverDefinition.class, this::registerCovers);
        bus.addListener(EventPriority.LOWEST, SFTCreateDataGen::gatherData);

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> SFTClient::init);
    }

    public static ResourceLocation id(String path) {
        if (path.isBlank()) {
            return ResourceLocation.fromNamespaceAndPath(MOD_ID, "");
        }
        int i = path.indexOf(':');
        if (i > 0) {
            return ResourceLocation.parse(path);
        } else if (i == 0) {
            path = path.substring(i + 1);
        }
        // only convert it to camel_case if it has any uppercase to begin with
        if (FormattingUtil.hasUpperCase(path)) {
            path = FormattingUtil.toLowerCaseUnderscore(path);
        }
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    @SubscribeEvent
    public void registerRecipeTypes(GTCEuAPI.RegisterEvent<ResourceLocation, GTRecipeType> event) {
        SFTRecipeTypes.init();
    }

    @SubscribeEvent
    public void registerMachines(GTCEuAPI.RegisterEvent<ResourceLocation, MachineDefinition> event) {
        SFTMachines.init();
    }

    @SubscribeEvent
    public void registerCovers(GTCEuAPI.RegisterEvent<ResourceLocation, CoverDefinition> event) {
        SFTCreativeTabs.init();
        SFTBlocks.init();
        SFTItems.init();
    }

    @SubscribeEvent
    public void registerMaterials(MaterialEvent event) {
        SFTMaterials.init();
    }

    @SubscribeEvent
    public void registerMaterialRegistry(MaterialRegistryEvent event) {
        GTCEuAPI.materialManager.createRegistry(MOD_ID);
    }

}
