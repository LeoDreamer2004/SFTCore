package org.leodreamer.sftcore.mixin.ae2.screen;

import org.leodreamer.sftcore.integration.ae2.feature.IGTTransferPanel;
import org.leodreamer.sftcore.integration.ae2.feature.ISendToGTMachine;
import org.leodreamer.sftcore.integration.ae2.gui.GTTransferPanel;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import appeng.client.gui.me.common.MEStorageScreen;
import appeng.client.gui.me.items.PatternEncodingTermScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.menu.me.items.PatternEncodingTermMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PatternEncodingTermScreen.class)
public class PatternEncodingTermScreenMixin<C extends PatternEncodingTermMenu>
    extends MEStorageScreen<C>
    implements IGTTransferPanel {

    @Unique
    GTTransferPanel sftcore$gtPanel;

    public PatternEncodingTermScreenMixin(
        C menu,
        Inventory playerInventory,
        Component title,
        ScreenStyle style
    ) {
        super(menu, playerInventory, title, style);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addGTPanel(
        PatternEncodingTermMenu menu,
        Inventory playerInventory,
        Component title,
        ScreenStyle style,
        CallbackInfo ci
    ) {
        var scrollbar = widgets.addScrollBar("gtPanelScrollbar");
        sftcore$gtPanel = new GTTransferPanel((ISendToGTMachine) menu, scrollbar);
        widgets.add("gtPanel", sftcore$gtPanel);
    }

    @Override
    public GTTransferPanel sftcore$gtPanel() {
        return sftcore$gtPanel;
    }
}
