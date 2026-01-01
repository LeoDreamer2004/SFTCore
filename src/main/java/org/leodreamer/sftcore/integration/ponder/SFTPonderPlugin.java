package org.leodreamer.sftcore.integration.ponder;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.integration.ponder.misc.SFTPonderTag;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;

import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Ponder Integration for SFTCore.
 * <p>
 * Some codes are ported from KubeJS by modpack <a href="https://github.com/Jasons-impart/Create-Delight-Remake">Create Delight Remake</a>
 * and <a href="https://github.com/Eternal-Snowstorm/CodeNameCIM2">Create: Mechanisms and Innovations</a>,
 * both of which are adapted with Java modding.
 * </p>
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SFTPonderPlugin implements PonderPlugin {

    @Override
    public String getModId() {
        return SFTCore.MOD_ID;
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        SFTPonderTag.register(helper);
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        SFTPonderScenes.register(helper);
    }
}
