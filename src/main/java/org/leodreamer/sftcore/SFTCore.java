package org.leodreamer.sftcore;

import org.leodreamer.sftcore.api.registry.SFTRegistrate;
import org.leodreamer.sftcore.common.data.SFTCreativeTabs;
import org.leodreamer.sftcore.common.data.SFTDataGen;
import org.leodreamer.sftcore.common.data.SFTItems;
import org.leodreamer.sftcore.common.data.SFTMachines;
import org.leodreamer.sftcore.common.item.wildcard.impl.WildcardPatternDecoder;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.cover.CoverDefinition;
import com.gregtechceu.gtceu.api.data.chemical.material.event.MaterialRegistryEvent;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.utils.FormattingUtil;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import appeng.api.crafting.PatternDetailsHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(SFTCore.MOD_ID)
public class SFTCore {

    public static final String MOD_ID = "sftcore";
    public static final SFTRegistrate REGISTRATE = SFTRegistrate.create(MOD_ID);
    public static final String NAME = "SFTCore";
    public static final Logger LOGGER = LogManager.getLogger(NAME);

    public SFTCore(FMLJavaModLoadingContext context) {
        REGISTRATE.registerRegistrate();
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        var bus = context.getModEventBus();
        bus.register(this);
        bus.addListener(this::commonSetup);
        bus.addGenericListener(MachineDefinition.class, this::registerMachines);
        bus.addGenericListener(CoverDefinition.class, this::registerCovers);
        bus.addListener(EventPriority.LOWEST, SFTDataGen::gatherData);

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

    public void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> PatternDetailsHelper.registerDecoder(WildcardPatternDecoder.INSTANCE));
    }

    @SubscribeEvent
    public void registerMachines(GTCEuAPI.RegisterEvent<ResourceLocation, MachineDefinition> event) {
        SFTMachines.init();
    }

    @SubscribeEvent
    public void registerCovers(GTCEuAPI.RegisterEvent<ResourceLocation, CoverDefinition> event) {
        SFTCreativeTabs.init();
        SFTItems.init();
    }

    @SubscribeEvent
    public void registerMaterialRegistry(MaterialRegistryEvent event) {
        GTCEuAPI.materialManager.createRegistry(MOD_ID);
    }
}
