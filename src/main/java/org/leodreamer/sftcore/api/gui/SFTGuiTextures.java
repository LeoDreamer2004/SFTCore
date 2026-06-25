package org.leodreamer.sftcore.api.gui;

import net.minecraft.resources.ResourceLocation;

import appeng.core.AppEng;
import brachy.modularui.drawable.UITexture;

public final class SFTGuiTextures {

    /**
     * See {@link appeng.client.gui.Icon#BACKGROUND_UPGRADE}
     */
    public static final UITexture CARD_UPDATE = UITexture.builder()
        .location(ResourceLocation.fromNamespaceAndPath(AppEng.MOD_ID, "textures/guis/states.png"))
        .imageSize(256, 256)
        .subAreaXYWH(240, 208, 16, 16)
        .build();

    private SFTGuiTextures() {}
}
