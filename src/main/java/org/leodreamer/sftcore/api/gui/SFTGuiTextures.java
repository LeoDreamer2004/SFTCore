package org.leodreamer.sftcore.api.gui;

import org.leodreamer.sftcore.SFTCore;

import net.minecraft.resources.ResourceLocation;

import appeng.core.AppEng;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;

public class SFTGuiTextures {

    public static final ResourceTexture DISPLAY_CREATE = new ResourceTexture(
        SFTCore.id("textures/gui/base/display_create.png")
    );

    /**
     * See {@link appeng.client.gui.Icon#BACKGROUND_UPGRADE}
     */
    public static final ResourceTexture CARD_UPDATE = new ResourceTexture(
        ResourceLocation.fromNamespaceAndPath(AppEng.MOD_ID, "textures/guis/states.png"), (float) 240 / 256,
        (float) 208 / 256, (float) 16 / 256, (float) 16 / 256
    );
}
