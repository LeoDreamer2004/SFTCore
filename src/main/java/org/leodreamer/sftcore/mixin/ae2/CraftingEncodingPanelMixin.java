package org.leodreamer.sftcore.mixin.ae2;

import appeng.client.gui.Icon;
import appeng.client.gui.WidgetContainer;
import appeng.client.gui.me.items.CraftingEncodingPanel;
import appeng.client.gui.me.items.EncodingModePanel;
import appeng.client.gui.me.items.PatternEncodingTermScreen;
import appeng.client.gui.widgets.ToggleButton;
import net.minecraft.network.chat.Component;
import org.leodreamer.sftcore.common.data.lang.MixinTooltips;
import org.leodreamer.sftcore.integration.ae2.ISendToAssemblyMatrix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(CraftingEncodingPanel.class)
public abstract class CraftingEncodingPanelMixin extends EncodingModePanel {
    @Unique
    private ToggleButton sftcore$transferToMatrixBtn;

    private CraftingEncodingPanelMixin(PatternEncodingTermScreen<?> screen, WidgetContainer widgets) {
        super(screen, widgets);
    }

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    private void initWithNewBtn(PatternEncodingTermScreen<?> screen, WidgetContainer widgets, CallbackInfo ci) {
        var button = new ToggleButton(
                Icon.PATTERN_TERMINAL_ALL,
                Icon.PATTERN_TERMINAL_VISIBLE,
                ((ISendToAssemblyMatrix) menu)::sftcore$setTransferToMatrix
        );
        button.setTooltipOn(List.of(
                Component.translatable(MixinTooltips.SEND_TO_ASSEMBLY_MATRIX_ON),
                Component.translatable(MixinTooltips.SEND_TO_ASSEMBLY_MATRIX_DESC_ENABLED)
        ));
        button.setTooltipOff(List.of(
                Component.translatable(MixinTooltips.SEND_TO_ASSEMBLY_MATRIX_OFF),
                Component.translatable(MixinTooltips.SEND_TO_ASSEMBLY_MATRIX_DESC_DISABLED)
        ));
        button.setHalfSize(true);
        widgets.add("craftingAutoTransfer", button);
        sftcore$transferToMatrixBtn = button;
    }


    @Inject(method = "updateBeforeRender", at = @At("TAIL"), remap = false)
    private void updateWithNewBtn(CallbackInfo ci) {
        sftcore$transferToMatrixBtn.setState(((ISendToAssemblyMatrix) menu).sftcore$getTransferToMatrix());
    }

    @Inject(method = "setVisible", at = @At("TAIL"), remap = false)
    private void setVisibleWithNewBtn(boolean visible, CallbackInfo ci) {
        if (sftcore$transferToMatrixBtn != null) {
            sftcore$transferToMatrixBtn.setVisibility(visible);
        }
    }
}
